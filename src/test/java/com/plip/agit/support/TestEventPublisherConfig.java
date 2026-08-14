package com.plip.agit.support;

import com.plip.agit.application.port.out.EventPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestEventPublisherConfig {

	@Bean
	public EventPublisherPort eventPublisherPort() {
		return (topic, key, payload) -> {
		};
	}
}
