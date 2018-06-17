package com.janmudra.kuberbot.binance.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerStatistics;

@RestController
public class JanMudraQuoteController {

//	private RestTemplate restTemplate;

	@Autowired
	private BinanceApiRestClient client;
	
	@RequestMapping("/quote")
	public String getQuote(@RequestParam(value="symbol", defaultValue="BTCUSDT") String symbol) {
		OrderBook orderBook = client.getOrderBook(symbol, 10);
		List<OrderBookEntry> asks = orderBook.getAsks();
		List<OrderBookEntry> bids = orderBook.getBids();
		OrderBookEntry firstAskEntry = asks.get(0);
		OrderBookEntry firstBidEntry = bids.get(0);
		Double spread = (Double.valueOf(firstAskEntry.getPrice()) - Double.valueOf(firstBidEntry.getPrice())) / Double.valueOf(firstAskEntry.getPrice());
		String symbolDetails =  "For " + symbol +  "  Bid = " + firstBidEntry + " and Ask = "+ firstAskEntry + " and Spread = " + spread +"%";
		System.out.println(symbolDetails);
		return symbolDetails;
	}

	@RequestMapping("/ticker")
	public TickerStatistics getTicker(@RequestParam(value="symbol", defaultValue="BTCUSDT") String symbol) {
		TickerStatistics ticker = client.get24HrPriceStatistics(symbol);
			System.out.println(" Ticker = " + ticker.getLastPrice());
		return ticker;
	}	

}