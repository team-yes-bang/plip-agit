package com.plip.agit.adapter.out.persistence;

import com.plip.agit.adapter.out.persistence.mapper.AgitBanEntityMapper;
import com.plip.agit.application.port.out.AgitBanPersistencePort;
import com.plip.agit.domain.model.AgitBan;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitBanPersistenceAdapter implements AgitBanPersistencePort {

	private final AgitBanRepository agitBanRepository;
	private final AgitBanEntityMapper agitBanEntityMapper;

	@Override
	@Transactional
	public AgitBan save(AgitBan ban) {
		if (ban.getId() == null) {
			AgitBanEntity saved = agitBanRepository.save(agitBanEntityMapper.toEntity(ban));
			return agitBanEntityMapper.toDomain(saved);
		}

		AgitBanEntity existing = agitBanRepository.findById(ban.getId())
				.orElseThrow(() -> new IllegalStateException("밴 이력을 찾을 수 없습니다."));
		existing.applyUnbannedAt(ban.getUnbannedAt());
		return agitBanEntityMapper.toDomain(existing);
	}

	@Override
	public Optional<AgitBan> findActiveByAgitIdAndUserUuid(Long agitId, UUID userUuid) {
		return agitBanRepository
				.findFirstByAgitIdAndUserUuidAndUnbannedAtIsNullOrderByBannedAtDesc(agitId, userUuid)
				.map(agitBanEntityMapper::toDomain);
	}
}
