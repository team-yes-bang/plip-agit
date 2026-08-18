package com.plip.agit.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.agit.application.port.in.ProjectAgitBoundTopicUseCase;
import com.plip.agit.application.port.out.TopicEventTopics;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class AgitBoundTopicConsumer {

	private final ObjectMapper objectMapper;
	private final ProjectAgitBoundTopicUseCase projectAgitBoundTopicUseCase;

	@KafkaListener(topics = TopicEventTopics.BOUND, groupId = "agit-read-model")
	public void consumeBound(String payload) {
		ParsedTopicEvent event = parse(payload);
		if (event == null) {
			return;
		}
		projectAgitBoundTopicUseCase.bind(event.agitUuid(), event.topicId(), event.startedAt());
	}

	@KafkaListener(topics = TopicEventTopics.UNBOUND, groupId = "agit-read-model")
	public void consumeUnbound(String payload) {
		ParsedTopicEvent event = parse(payload);
		if (event == null) {
			return;
		}
		projectAgitBoundTopicUseCase.unbind(event.agitUuid(), event.topicId());
	}

	@KafkaListener(topics = TopicEventTopics.STARTED, groupId = "agit-read-model")
	public void consumeStarted(String payload) {
		ParsedTopicEvent event = parse(payload);
		if (event == null) {
			return;
		}
		projectAgitBoundTopicUseCase.start(event.agitUuid(), event.topicId(), event.startedAt());
	}

	private ParsedTopicEvent parse(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			JsonNode agitUuidNode = node.get("agitUuid");
			JsonNode topicIdNode = node.get("topicId");
			if (agitUuidNode == null || agitUuidNode.isNull() || agitUuidNode.asText().isBlank()
					|| topicIdNode == null || topicIdNode.isNull() || topicIdNode.asText().isBlank()) {
				log.warn("묶인 토픽 투영 skip: agitUuid 또는 topicId 없음");
				return null;
			}
			UUID agitUuid = UUID.fromString(agitUuidNode.asText());
			Instant startedAt = parseInstant(node.get("startedAt"));
			return new ParsedTopicEvent(agitUuid, topicIdNode.asText(), startedAt);
		} catch (IllegalArgumentException e) {
			log.warn("묶인 토픽 투영 skip: 식별자 파싱 실패 {}", e.getMessage());
			return null;
		} catch (Exception e) {
			log.warn("묶인 토픽 투영 실패: {}", e.getMessage());
			return null;
		}
	}

	private Instant parseInstant(JsonNode node) {
		if (node == null || node.isNull() || node.asText().isBlank()) {
			return null;
		}
		String text = node.asText();
		try {
			return Instant.parse(text);
		} catch (DateTimeParseException ignored) {
			return LocalDateTime.parse(text).toInstant(ZoneOffset.UTC);
		}
	}

	private record ParsedTopicEvent(UUID agitUuid, String topicId, Instant startedAt) {
	}
}
