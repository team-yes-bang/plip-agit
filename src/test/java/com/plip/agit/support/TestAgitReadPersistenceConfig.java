package com.plip.agit.support;

import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import java.util.Optional;
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
			public Optional<AgitReadSnapshot> findActiveByCode(String code) {
				return Optional.empty();
			}
		};
	}
}
