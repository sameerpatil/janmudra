package com.janmudra.kuberbot.scheduler;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.janmudra.kuberbot.utils.CSVUtils;
import com.janmudra.kuberbot.utils.OrderCalculatorUtil;

@Component
public class PlaceOrderScheduledTask {

	@Autowired
	private BinanceApiRestClient client;
	
//	private String symbol = "XLMETH";
	private String symbol = "NEOBTC";
	private String profitPercent = "1";
	private static final Logger log = LoggerFactory.getLogger(PlaceOrderScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(fixedRate = 120000)
    public void executeKuberOrder() {
//        log.info("The time is now {}", dateFormat.format(new Date()));
//        TickerStatistics ticker = client.get24HrPriceStatistics(symbol);
//        log.info(dateFormat.format(new Date()) + " Ticker = " + ticker.getLastPrice());
    	
    	//1 Check if the open order exists... if not place a new order. it can be for a fresh trade.
    	boolean orderExists = checkOldOpenOrders();
    	if(!orderExists) {
    		placeNewOrder();
    	}else {
//    		SpringApplication.exit(arg0, arg1)
    		System.out.println("Order exists so cannot place another order");
    		log.info("Order exists so cannot place another order");
    	}
    }

    
    public String executeEventBasedKuberOrder(String orderType) {
    	//boolean orderExists = checkOldOpenOrders();
    	//if(!orderExists) {
    		return placeEventBasedNewOrder(orderType);
    	//}else {
    		//System.out.println("Order exists so cannot place another order");
    		//log.info("Order exists so cannot place another order");
    	//}
    	//return null;
    }
    
    /**
     * Places a new order for the dedicate pair of MARKET type. Followed by gives call to coverOrder
     */
	private void placeNewOrder() {
		OrderBook orderBook = client.getOrderBook(symbol, 10);
		List<OrderBookEntry> asks = orderBook.getAsks();
		List<OrderBookEntry> bids = orderBook.getBids();
		OrderBookEntry firstAskEntry = asks.get(0);
		OrderBookEntry firstBidEntry = bids.get(0);
		
		String quantity = calculateQuantity(firstAskEntry);//<- Need to work on this
		/** 
		 * Important in the below line the orderType is set to MARKET 
		 * because we want to execute the order immediately so it would be easy to calculate and 
		 * place the cover order immediately.
		 * We cannot change it to LIMIT type order because we don't have a mechanism yet to
		 * poll and check if the order got executed or not without which we cannot place the cover
		 * order...!!!
		**/
		NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, quantity,firstAskEntry.getPrice());
		//NewOrderResponse newOrderResponse = client.newOrder(order);
		log.info("Placed Order = " + order.toString());
		//log.info("Order Response = " + newOrderResponse.getOrderId());
		System.out.println("** Placed Order = " + order.toString());
		log.info("Now calculating and placing the cover-order");
		placeCoverOrderForThisOrder(order);
	}

	/**
     * Places a new order for the dedicate pair of MARKET type. Followed by gives call to coverOrder
     */
	private String placeEventBasedNewOrder(String orderType) {
		OrderBook orderBook = client.getOrderBook(symbol, 10);
		List<OrderBookEntry> asks = orderBook.getAsks();
		List<OrderBookEntry> bids = orderBook.getBids();
		OrderBookEntry firstAskEntry = asks.get(0);
		OrderBookEntry firstBidEntry = bids.get(0);
		
		String quantity = calculateQuantity(firstAskEntry);//<- Need to work on this
		/** 
		 * Important in the below line the orderType is set to MARKET 
		 * because we want to execute the order immediately so it would be easy to calculate and 
		 * place the cover order immediately.
		 * We cannot change it to LIMIT type order because we don't have a mechanism yet to
		 * poll and check if the order got executed or not without which we cannot place the cover
		 * order...!!!
		**/
		//NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, TimeInForce.GTC, quantity,firstAskEntry.getPrice());
		// Persists this order in CSV File
		String csvFile = "analysis.csv";
        FileWriter writer;
		try {
			writer = new FileWriter(csvFile,true);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			CSVUtils.writeLine(writer, Arrays.asList(orderType, symbol, OrderSide.SELL.toString(), OrderType.MARKET.toString(), dateFormat.format(date), quantity.toString(), firstAskEntry.getPrice()));
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//NewOrderResponse newOrderResponse = client.newOrder(order);
		//log.info("Placed Order = " + order.toString());
		//log.info("Order Response = " + newOrderResponse.getOrderId());
		//System.out.println("** Placed Order = " + order.toString());
		log.info("Now calculating and placing the cover-order");
		//placeCoverOrderForThisOrder(order);
		return orderType;
	}
	/**
	 * This method looks at the order passed in the parameter and place the cover order for that
	 * If the passed order is of type OrderSide.SELL then the new order is of OrderSide.BUY of
	 * same pair type and passed configured percentage. 
	 * Eg.1 For USD
	 * Orig order is of 1 NEO sold @80 USD and profit percentage is configured to 1% then
	 * the cover order would be 
	 * BUY NEO of 80.01 @ 79.2 (Brokerage excluded for clarity) in the formula in code it includes
	 * brokerage on both legs of the order.
	 * 
	 * Eg.2 For NEO/ETH
	 * TODO complete the example
	 * 
	 * @param order
	 */
	private void placeCoverOrderForThisOrder(NewOrder order) {
		OrderSide coverOrderSide = OrderSide.SELL;
		
		if(order.getSide().equals(OrderSide.SELL))
			coverOrderSide = OrderSide.BUY;
		
		Double coverQuantity = OrderCalculatorUtil.
				getQuantityByPercentage(order.getQuantity(), profitPercent);
		Double coverPrice = OrderCalculatorUtil.
				getNewPriceWithBrokerage(order.getPrice(), profitPercent);
		NewOrder coverOrder = new NewOrder(symbol, coverOrderSide, OrderType.LIMIT, 
				TimeInForce.GTC, String.valueOf(coverQuantity),String.valueOf(coverPrice));
	
	}

	private String calculateQuantity(OrderBookEntry firstBidEntry) {
		//TODO implement this by doing some calculations...In next version we need to have formulas for position sizing and riskmanagement  
		return "1";
	}

	private boolean checkOldOpenOrders() {
		List<Order> openOrders = client.getOpenOrders(new OrderRequest(symbol));
		log.info(openOrders.size() + " Older order(s) for symbol" + symbol  );
		System.out.println(">>>>>>>>>>>>"+openOrders.size() + " Older order(s) for symbol" + symbol  );
		return openOrders.size() > 0 ? true:false;
	}
}
