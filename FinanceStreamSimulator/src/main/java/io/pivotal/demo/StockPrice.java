package io.pivotal.demo;

import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.annotation.JsonProperty;

@ComponentScan
public class StockPrice {

	@JsonProperty("Symbol")
	private String symbol;
	
	@JsonProperty("LastTradeDate")	
	private String lastTradeDate;
	
	@JsonProperty("LastTradeTime")
	private String lastTradeTime;

	@JsonProperty("tradeTimestamp")
	private long tradeTimestamp;
	
	@JsonProperty("Open")
	private double open;
	
	@JsonProperty("LastTradePriceOnly")
	private double price;

	@JsonProperty("DaysHigh")	
	private double high;

	@JsonProperty("DaysLow")	
	private double low;

	@JsonProperty("entryTimestamp")	
	private long timestamp;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getLastTradeDate() {
		return lastTradeDate;
	}

	public void setLastTradeDate(String lastTradeDate) {
		this.lastTradeDate = lastTradeDate;
	}

	public String getLastTradeTime() {
		return lastTradeTime;
	}

	public void setLastTradeTime(String lastTradeTime) {
		this.lastTradeTime = lastTradeTime;
	}

	public long getTradeTimestamp() {
		return tradeTimestamp;
	}

	public void setTradeTimestamp(long tradeTimestamp) {
		this.tradeTimestamp = tradeTimestamp;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}
	
	
	
	
}
