package com.janmudra.kuberbot.binance.client;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.janmudra.kuberbot.scheduler.PlaceOrderScheduledTask;

@RestController
public class TradeController {

    private final AtomicLong counter = new AtomicLong();

    @Autowired 
    PlaceOrderScheduledTask placeOrder;
    @RequestMapping("/trade")
    public String greeting(@RequestParam(value="tradeDirection", defaultValue="short") String tradeDirection) {
    	String orderType = tradeDirection;
    	return placeOrder.executeEventBasedKuberOrder(orderType);
    }
}