package com.plip.agit.application.service;

import com.plip.agit.application.port.in.BackfillAgitReadModelsUseCase;
import com.plip.agit.application.port.in.RefreshAgitReadModelUseCase;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.port.out.AgitReadMemberSnapshot;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitReadModelService implements RefreshAgitReadModelUseCase, BackfillAgitReadModelsUseCase {

	private final AgitPersistencePort agitPersistencePort;
	private final AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;
	private final AgitReadPersistencePort agitReadPersistencePort;

	@Override
	public void refresh(UUID agitUuid) {
		Optional<Agit> agit = agitPersistencePort.findByAgitUuid(agitUuid);
		if (agit.isEmpty()) {
			log.warn("아지트 읽기 모델 갱신 skip: MySQL에 없음 agitUuid={}", agitUuid);
			return;
		}
		replaceFromMysql(agit.get());
	}

	@Override
	public void backfillAll() {
		List<Agit> agits = agitPersistencePort.findAll();
		log.info("아지트 읽기 모델 백필 시작 count={}", agits.size());
		for (Agit agit : agits) {
			replaceFromMysql(agit);
		}
		log.info("아지트 읽기 모델 백필 완료 count={}", agits.size());
	}

	private void replaceFromMysql(Agit agit) {
		List<AgitReadMemberSnapshot> members = agitMemberProfilePersistencePort
				.findActiveByAgitId(agit.getId())
				.stream()
				.map(this::toMemberSnapshot)
				.toList();
		agitReadPersistencePort.replace(new AgitReadSnapshot(
				agit.getAgitUuid(),
				agit.getAgitName(),
				agit.getDescription(),
				agit.getThumbnailPath(),
				agit.getCode(),
				agit.getStatus(),
				agit.getMaximumCapacity(),
				members,
				Instant.now()
		));
	}

	private AgitReadMemberSnapshot toMemberSnapshot(AgitMemberProfile profile) {
		return new AgitReadMemberSnapshot(
				profile.getUserUuid(),
				profile.getNickname(),
				profile.getProfileImagePath(),
				profile.getRole()
		);
	}
}
