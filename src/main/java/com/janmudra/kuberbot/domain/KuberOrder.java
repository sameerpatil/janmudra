package com.janmudra.kuberbot.domain;

import com.janmudra.kuberbot.utils.Symbol;

public class KuberOrder {

	private String	symbol;
	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public Double getPrice() {
		return price;
	}


	public void setPrice(Double price) {
		this.price = price;
	}


	public boolean isTradeOpen() {
		return tradeOpen;
	}


	public void setTradeOpen(boolean tradeOpen) {
		this.tradeOpen = tradeOpen;
	}


	public boolean isShortTrade() {
		return shortTrade;
	}


	public void setShortTrade(boolean shortTrade) {
		this.shortTrade = shortTrade;
	}


	public long getCreationTime() {
		return creationTime;
	}


	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}


	public long getTradeOpenTime() {
		return tradeOpenTime;
	}


	public void setTradeOpenTime(long tradeOpenTime) {
		this.tradeOpenTime = tradeOpenTime;
	}


	public long getTradeClosingTime() {
		return tradeClosingTime;
	}


	public void setTradeClosingTime(long tradeClosingTime) {
		this.tradeClosingTime = tradeClosingTime;
	}


	private Double	price;
	private boolean tradeOpen;
	private boolean shortTrade;
	private long	creationTime;
	private long	tradeOpenTime;
	private long	tradeClosingTime;
	
	
	public KuberOrder(String symbol, boolean shortTrade) {
		this.symbol = symbol;
		this.shortTrade = shortTrade;
	}
	
	
}
