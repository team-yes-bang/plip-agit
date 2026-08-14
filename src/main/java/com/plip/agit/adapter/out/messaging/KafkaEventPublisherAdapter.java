package com.plip.agit.adapter.out.messaging;

import com.plip.agit.application.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements EventPublisherPort {

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void publish(String topic, String key, String payload) {
		kafkaTemplate.send(topic, key, payload);
	}
}
