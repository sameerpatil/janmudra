package com.janmudra.kuberbot.scheduler;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
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

@Component
public class PlaceOrderScheduledTask {

	@Autowired
	private BinanceApiRestClient client;
	
//	private String symbol = "XLMETH";
	private String symbol = "NEOUSDT";
	private static final Logger log = LoggerFactory.getLogger(PlaceOrderScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 120000)
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

	private void placeNewOrder() {
		OrderBook orderBook = client.getOrderBook(symbol, 10);
		List<OrderBookEntry> asks = orderBook.getAsks();
		List<OrderBookEntry> bids = orderBook.getBids();
		OrderBookEntry firstAskEntry = asks.get(0);
		OrderBookEntry firstBidEntry = bids.get(0);
		
		String quantity = calculateQuantity(firstAskEntry);//Need to work on this <-
		NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC, quantity,firstAskEntry.getPrice());
		NewOrderResponse newOrderResponse = client.newOrder(order);
		log.info("** Placed Order = " + order.toString());
			System.out.println("** Placed Order = " + order.toString());
			System.out.println("*********** NOW SLEEPING *****************");
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
