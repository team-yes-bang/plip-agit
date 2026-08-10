package com.plip.agit.adapter.out.persistence;

import com.plip.agit.adapter.out.persistence.mapper.AgitEntityMapper;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitPersistenceAdapter implements AgitPersistencePort {

	private final AgitRepository agitRepository;
	private final AgitEntityMapper agitEntityMapper;

	@Override
	@Transactional
	public Agit save(Agit agit) {
		Optional<AgitEntity> existing = agitRepository.findByAgitUuid(agit.getAgitUuid());
		if (existing.isEmpty()) {
			AgitEntity saved = agitRepository.save(agitEntityMapper.toEntity(agit));
			return agitEntityMapper.toDomain(saved);
		}

		agitRepository.updateByAgitUuid(
				agit.getAgitUuid(),
				agit.getAgitName(),
				agit.getDescription() != null ? agit.getDescription() : "",
				agit.getMaximumCapacity(),
				agit.getCode(),
				agitEntityMapper.toEntityStatus(agit.getStatus()),
				agit.getThumbnailPath(),
				LocalDateTime.now()
		);

		return agitRepository.findByAgitUuid(agit.getAgitUuid())
				.map(agitEntityMapper::toDomain)
				.orElseThrow(() -> new IllegalStateException("아지트 저장 후 조회에 실패했습니다."));
	}

	@Override
	public Optional<Agit> findByAgitUuid(UUID agitUuid) {
		return agitRepository.findByAgitUuid(agitUuid)
				.map(agitEntityMapper::toDomain);
	}

	@Override
	public Optional<Agit> findByCode(String code) {
		return agitRepository.findByCode(code)
				.map(agitEntityMapper::toDomain);
	}

	@Override
	public boolean existsByCode(String code) {
		return agitRepository.existsByCode(code);
	}
}
