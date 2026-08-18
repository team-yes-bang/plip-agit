package com.plip.agit.adapter.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.agit.application.port.in.RefreshAgitReadModelUseCase;
import com.plip.agit.application.port.out.AgitEventTopics;
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
public class AgitReadModelConsumer {

	private final ObjectMapper objectMapper;
	private final RefreshAgitReadModelUseCase refreshAgitReadModelUseCase;

	@KafkaListener(
			topics = {
					AgitEventTopics.CREATED,
					AgitEventTopics.MEMBER_JOINED,
					AgitEventTopics.MEMBER_LEFT,
					AgitEventTopics.DELETED,
					AgitEventTopics.MEMBER_BANNED,
					AgitEventTopics.MEMBER_UNBANNED,
					AgitEventTopics.UPDATED,
					AgitEventTopics.HOST_TRANSFERRED,
					AgitEventTopics.INVITE_CODE_REISSUED,
					AgitEventTopics.MEMBER_PROFILE_UPDATED
			},
			groupId = "agit-read-model"
	)
	public void consume(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			JsonNode uuidNode = node.get("agitUuid");
			if (uuidNode == null || uuidNode.isNull() || uuidNode.asText().isBlank()) {
				log.warn("아지트 읽기 모델 갱신 skip: agitUuid 없음");
				return;
			}
			UUID agitUuid = UUID.fromString(uuidNode.asText());
			refreshAgitReadModelUseCase.refresh(agitUuid);
		} catch (IllegalArgumentException e) {
			log.warn("아지트 읽기 모델 갱신 skip: agitUuid 파싱 실패 {}", e.getMessage());
		} catch (Exception e) {
			log.warn("아지트 읽기 모델 갱신 실패: {}", e.getMessage());
		}
	}
}
