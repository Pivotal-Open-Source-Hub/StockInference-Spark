require("RCurl")
require("quantmod")
require("TTR")
require("jsonlite")




f <- file("stdin")
open(f)
while(TRUE) {
  line <- readLines(f,n=1)

  streamRow <- fromJSON(line)
  

  historical <- getURL(paste0('http://localhost:8888/gemfire-api/v1/queries/adhoc?q=SELECT%20DISTINCT%20*%20FROM%20/Stocks%20s%20WHERE%20s.entryTimestamp%20<%20',streamRow$entryTimestamp,'l%20ORDER%20BY%20s.entryTimestamp%20desc%20LIMIT%20100'))

  
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
  

  # include a few technical indicators
  ema <- EMA(as.numeric(dataset$Close)) # lag = n-1 (default=9)
  ema_diff <- as.numeric(dataset$Close) - ema # lag = above
  rsi <- RSI(as.numeric(dataset$Close)) # lag = n (default=14)
#  smi <- SMI(HLC(dataset))     # lag = nSlow+nSig (default=34)
#  sar <- SAR(HLC(dataset))     # lag = 0
#  ema_lag <- lag.xts (ema, k=-1)

  high_diff = as.numeric(dataset$High)-as.numeric(dataset$Close)
  low_diff = as.numeric(dataset$Low)-as.numeric(dataset$Close)

  inputs <- data.frame(dataset$Close, ema, ema_diff, rsi, high_diff, low_diff)
  names(inputs) <- c("close", "ema", "ema_diff", "rsi", "high_diff", "low_diff")
 
# Skipping the first 15 lines due to lag on indicators
  inputs <- inputs[-1:-15,]
  historicalSet <- historicalSet[-1:-15,]
  dataset <- dataset[-1:-15,]

  if (nrow(inputs)>0){

  	# Update last line, with the real future_ema value
  	updated_rows <- data.frame(historicalSet$entryTimestamp[nrow(historicalSet)], inputs$ema[nrow(inputs)-1], inputs$ema[nrow(inputs)],inputs$close[nrow(inputs)-1], inputs$rsi[nrow(inputs)-1], inputs$ema_diff[nrow(inputs)-1], inputs$high_diff[nrow(inputs)-1], inputs$low_diff[nrow(inputs)-1]) ;
  	names(updated_rows) <- c("entryTimestamp", "ema", "future_ema", "close", "rsi", "ema_diff", "high_diff", "low_diff")


  	#Add new row to the end of historical dataset for computing technical indicators.
  	temprow <- matrix(c(rep.int(NA,length(updated_rows))),nrow=1,ncol=length(updated_rows))
  	newrow <- data.frame(temprow)
  	colnames(newrow) <- colnames(updated_rows)
  	updated_rows <- rbind(updated_rows,newrow)

  	# create the new line, with values coming from the std input and calculated indicators
  	updated_rows[nrow(updated_rows),] <- data.frame(streamRow$entryTimestamp, inputs$ema[nrow(inputs)], inputs$ema[nrow(inputs)], inputs$close[nrow(inputs)], inputs$rsi[nrow(inputs)], inputs$ema_diff[nrow(inputs)], inputs$high_diff[nrow(inputs)], inputs$low_diff[nrow(inputs)] );

        # output all numbers as strings, leave the rounding and type convertion up to the ML algorithm

	updated_rows$ema <- as.character(updated_rows$ema)
	updated_rows$future_ema <- as.character(updated_rows$future_ema)
	updated_rows$close <- as.character(updated_rows$close)
	updated_rows$rsi <- as.character(updated_rows$rsi)
	updated_rows$ema_diff <- as.character(updated_rows$ema_diff)
	updated_rows$high_diff <- as.character(updated_rows$high_diff)
	updated_rows$low_diff <- as.character(updated_rows$low_diff)

  	updated_rows <- toJSON(updated_rows);
  	cat (updated_rows)
  }

  write('\r\n',stdout())
}
close(f)
# Remember to close file
