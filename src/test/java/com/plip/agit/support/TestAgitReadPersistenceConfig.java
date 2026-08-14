package com.plip.agit.support;

import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.application.port.out.AgitReadTopicSnapshot;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestAgitReadPersistenceConfig {

	@Bean
	public AgitReadPersistencePort agitReadPersistencePort() {
		return new AgitReadPersistencePort() {
			@Override
			public void replace(AgitReadSnapshot snapshot) {
			}

			@Override
			public Optional<AgitReadSnapshot> findByAgitUuid(UUID agitUuid) {
				return Optional.empty();
			}

			@Override
			public Optional<AgitReadSnapshot> findActiveByCode(String code) {
				return Optional.empty();
			}

			@Override
			public boolean upsertTopic(UUID agitUuid, AgitReadTopicSnapshot topic) {
				return false;
			}

			@Override
			public boolean removeTopic(UUID agitUuid, String topicId) {
				return false;
			}
		};
	}
}
