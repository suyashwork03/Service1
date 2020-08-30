package com.dailmer.service1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@Configuration
public class Service1Config {
	@Bean
	RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
		return RSocketRequester.builder().rsocketStrategies(rSocketStrategies).connectTcp("localhost", 7000).block();
	}

}