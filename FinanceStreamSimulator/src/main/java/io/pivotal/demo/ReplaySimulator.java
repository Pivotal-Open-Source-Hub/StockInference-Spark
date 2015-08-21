package io.pivotal.demo;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ComponentScan
@Configuration
public class ReplaySimulator implements CommandLineRunner {

	@Value("${serverUrl}") 
	private String URL;
	
	@Value("${geodeUrl}") 
	private String geodeURL;

	@Value("${numberOfMessages}") 
	private int numberOfMessages;

	@Value("${symbol}") 
	private String symbol;
	
	@Value("${delayInMs}") 
	private long delay;

	private RestTemplate restTemplate = new RestTemplate();
	
	Logger logger = Logger.getLogger(ReplaySimulator.class.getName());

	@Override
	public void run(String... args) throws Exception {
		
				
		logger.info("--------------------------------------");
		logger.info(">>> Geode rest endpoint: "+geodeURL);
		logger.info(">>> Endpoint URL: "+URL);
		logger.info(">>> Number of messages: "+numberOfMessages);
		logger.info(">>> Symbol: "+symbol);
		logger.info("--------------------------------------");
		
		List objects = restTemplate.getForObject(geodeURL+"/gemfire-api/v1/queries/adhoc?q=SELECT%20DISTINCT%20*%20FROM%20/Stocks%20s%20ORDER%20BY%22timestamp%22%20desc%20LIMIT%20"+numberOfMessages, List.class);

		logger.info(">>> Posting "+objects.size()+" messages ...");

		for (int i=0; i<objects.size(); i++){
			Map<String,Object> map = (Map)objects.get(i);
			StockPrice price = new StockPrice();
			price.setPrice(new Double(map.get("LastTradePriceOnly").toString()));
			price.setSymbol((String)map.get("Symbol"));
			price.setLow(new Double(map.get("DaysLow").toString()));
			price.setHigh(new Double(map.get("DaysHigh").toString()));
			price.setTimestamp((Long)map.get("entryTimestamp"));
			price.setTradeTimestamp((Long)map.get("tradeTimestamp"));
			price.setOpen(new Double(map.get("Open").toString()));
			price.setLastTradeDate(map.get("LastTradeDate").toString());
			price.setLastTradeTime(map.get("LastTradeTime").toString());
			
			StockPrice response = restTemplate.postForObject(URL, price, StockPrice.class);
			Thread.sleep(delay);
		}
		
		
		
		logger.info("done");
		
		
	}

}