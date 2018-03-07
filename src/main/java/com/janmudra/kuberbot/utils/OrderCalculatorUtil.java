package com.janmudra.kuberbot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderCalculatorUtil {
	
	private static final Logger log = LoggerFactory.getLogger(OrderCalculatorUtil.class);
public static void main(String[] args) {
	
	String ask = "10.00000000";
	Double ask1 = Double.parseDouble(ask);

	
	/*String bid = "11500.00000000";
	Double bid1 = Double.parseDouble(bid);
	
	Double spread = (ask1-bid1)/ask1;
	System.out.println("Spread = " +spread.doubleValue());*/
	//I am selling BTC at ask price... and i want to buy it back when the price drops by 1%... 
	//Implement a method which will give the new buy value after adjusting the brokerage for Sell as well as the brokerage for buying
	System.out.println(" percentage = " + getNewValueWithBrokerage(ask, "1"));
}


public static Double getNewValueWithBrokerage(String value, String percentageDiff) {
	Double orig = Double.parseDouble(value);
	Double percent = Double.parseDouble(percentageDiff);
	double unknown = orig.doubleValue()*percent.doubleValue();
	Double firstLegBrokerage = getBrokerage(orig);
	Double differenceAmmount = unknown/100;
	System.out.println("differenceAmmount = " + differenceAmmount);
	if(percent<0){
		differenceAmmount = differenceAmmount-firstLegBrokerage;
	}else{
		differenceAmmount = differenceAmmount+firstLegBrokerage;
	}
	
	Double secondLegBrokerage = getBrokerage(differenceAmmount);
	differenceAmmount = differenceAmmount + orig;
	if(percent<0){
		differenceAmmount = differenceAmmount-secondLegBrokerage;
	}else{
		differenceAmmount = differenceAmmount+secondLegBrokerage;
	}
	 

	System.out.println(differenceAmmount);
	return differenceAmmount ;
}


private static Double getBrokerage(Double orig) {
	double brokerage = 0.10;
	double unknown = orig*(brokerage/100);
	return unknown;
}
}
