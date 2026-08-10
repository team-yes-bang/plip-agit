package com.plip.agit.adapter.out.persistence;

import com.plip.agit.adapter.out.persistence.mapper.AgitMemberProfileEntityMapper;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.domain.model.AgitMemberProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitMemberProfilePersistenceAdapter implements AgitMemberProfilePersistencePort {

	private final AgitMemberProfileRepository agitMemberProfileRepository;
	private final AgitMemberProfileEntityMapper agitMemberProfileEntityMapper;

	@Override
	@Transactional
	public AgitMemberProfile save(AgitMemberProfile profile) {
		AgitMemberProfileEntity saved = agitMemberProfileRepository.save(
				agitMemberProfileEntityMapper.toEntity(profile)
		);
		return agitMemberProfileEntityMapper.toDomain(saved);
	}
}
