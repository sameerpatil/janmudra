package com.janmudra.kuberbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.janmudra.kuberbot.config.ExchangeConfig;

@SpringBootApplication(scanBasePackages = {"com.janmudra.*"})
@EnableScheduling
public class KuberBotApplication {

	private static final Logger logger = LoggerFactory.getLogger(KuberBotApplication.class);
	 @Autowired
	 ExchangeConfig config;
	public static void main(String[] args) {
		SpringApplication.run(KuberBotApplication.class, args);
	}
	
	@Bean
	public BinanceApiRestClient initRestClient() {
//		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("oemLMHzcZ3hmC43yzIsXJ2Q2mllHiSYfp1bd0lhqpSlbzyyFJYrJR8fxZQjqDFtw", "xBAjqYW7CSrfnvMlHyVXNzMwAsI4vJbSTAue7OsD8HwUxBThxWLr7H2mvCOoh4LH");
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(config.getApiKey(),config.getSecretKey());
		BinanceApiRestClient restClient = factory.newRestClient();
		restClient.ping();
		long serverTime = restClient.getServerTime();
		logger.info("The server time  = " + serverTime);
		logger.info("##### The System is Up and Running!! #####");
		System.out.println("##### The System is Up and Running!! #####");
		return restClient;
	}
	
	@Bean
	public BinanceApiWebSocketClient initWSClient(){
		
	BinanceApiWebSocketClient wsClient = BinanceApiClientFactory.newInstance().newWebSocketClient();
	return wsClient;
	}
}
