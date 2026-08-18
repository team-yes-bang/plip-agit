package com.plip.agit.adapter.in.messaging;

import com.plip.agit.application.port.in.BackfillAgitReadModelsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@ConditionalOnProperty(name = "agit.mongo.backfill", havingValue = "true")
@RequiredArgsConstructor
public class AgitReadModelBackfillRunner implements ApplicationRunner {

	private final BackfillAgitReadModelsUseCase backfillAgitReadModelsUseCase;

	@Override
	public void run(ApplicationArguments args) {
		log.info("AGIT_MONGO_BACKFILL=true, 읽기 모델 백필을 실행합니다.");
		backfillAgitReadModelsUseCase.backfillAll();
	}
}
