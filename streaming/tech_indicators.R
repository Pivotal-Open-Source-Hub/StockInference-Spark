require("RCurl")
require("quantmod")
require("TTR")
require("jsonlite")




f <- file("stdin")
open(f)
while(TRUE) {
  line <- readLines(f,n=1)

  streamRow <- fromJSON(line)
  

  historical <- getURL(paste0('http://geode-server:8080/gemfire-api/v1/queries/adhoc?q=SELECT%20DISTINCT%20*%20FROM%20/Stocks%20s%20WHERE%20s.entryTimestamp%20<%20',streamRow$entryTimestamp,'l%20ORDER%20BY%20s.entryTimestamp%20desc%20LIMIT%20100'))

  
  historicalSet <- fromJSON(historical)
  
  # reverse order
  historicalSet <-historicalSet[order(historicalSet$entryTimestamp),]

  dataset <- subset(historicalSet, select = c("DaysHigh", "DaysLow", "LastTradePriceOnly")) 
  names(dataset) <- c("High","Low","Close")

  #Add new row to the end of historical dataset for computing technical indicators.
  temprow <- matrix(c(rep.int(NA,length(dataset))),nrow=1,ncol=length(dataset))
  newrow <- data.frame(temprow)
  colnames(newrow) <- colnames(dataset)
  dataset <- rbind(dataset,newrow)
  dataset[nrow(dataset),] <- c(as.numeric(streamRow$DaysHigh), as.numeric(streamRow$DaysLow), as.numeric(streamRow$LastTradePriceOnly))
  

  # include technical indicators
  ema <- EMA(as.numeric(dataset$Close)) # lag = n-1 (default=9)
  ema_diff <- as.numeric(dataset$Close) - ema # lag = above
  rsi <- RSI(as.numeric(dataset$Close)) # lag = n (default=14)
#  smi <- SMI(HLC(dataset))     # lag = nSlow+nSig (default=34)
#  sar <- SAR(HLC(dataset))     # lag = 0

  ema_lag <- lag.xts (ema, k=-1)

  high_diff = as.numeric(dataset$High)-as.numeric(dataset$Close)
  low_diff = as.numeric(dataset$Close)-as.numeric(dataset$Low)

  inputs <- data.frame(dataset$Close, ema, ema_diff, rsi, high_diff, low_diff)
  names(inputs) <- c("close", "ema", "ema_diff", "rsi", "high_diff", "low_diff")
 
  inputs <- inputs[-1:-35,]
  historicalSet <- historicalSet[-1:-35,]
  dataset <- dataset[-1:-35,]
  ema_lag <- ema_lag[-1:-35]

  if (nrow(inputs)>0){

  	# Update last line, with the real future_ema value
  	updated_rows <- data.frame(historicalSet$entryTimestamp[nrow(historicalSet)], inputs$ema[nrow(inputs)-1], inputs$ema[nrow(inputs)],inputs$close[nrow(inputs)-1]);
  	names(updated_rows) <- c("entryTimestamp", "ema", "future_ema", "close")


  	#Add new row to the end of historical dataset for computing technical indicators.
  	temprow <- matrix(c(rep.int(NA,length(updated_rows))),nrow=1,ncol=length(updated_rows))
  	newrow <- data.frame(temprow)
  	colnames(newrow) <- colnames(updated_rows)
  	updated_rows <- rbind(updated_rows,newrow)

  	# create the new line, with values coming from the std input and calculated indicators
  	updated_rows[nrow(updated_rows),] <- data.frame(streamRow$entryTimestamp, inputs$ema[nrow(inputs)], 0, inputs$close[nrow(inputs)]);

  	updated_rows <- toJSON(updated_rows);
  	cat (updated_rows)
  }

  write('\r\n',stdout())
}
close(f)
# Remember to close file
