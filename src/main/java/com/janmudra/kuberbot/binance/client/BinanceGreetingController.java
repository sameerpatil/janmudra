package com.janmudra.kuberbot.binance.client;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.janmudra.kuberbot.scheduler.PlaceOrderScheduledTask;

@RestController
public class BinanceGreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired 
    PlaceOrderScheduledTask placeOrder;
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="short") String name) {
    	String orderType = name;
    	return placeOrder.executeEventBasedKuberOrder(orderType);
    	//return new Greeting(counter.incrementAndGet(),
         //                   String.format(template, name));
    }
}