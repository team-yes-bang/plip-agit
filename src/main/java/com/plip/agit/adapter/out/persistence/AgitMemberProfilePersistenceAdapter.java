package com.plip.agit.adapter.out.persistence;

import com.plip.agit.adapter.out.persistence.mapper.AgitMemberProfileEntityMapper;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.domain.model.AgitMemberProfile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
		if (profile.getId() == null) {
			AgitMemberProfileEntity saved = agitMemberProfileRepository.save(
					agitMemberProfileEntityMapper.toEntity(profile)
			);
			return agitMemberProfileEntityMapper.toDomain(saved);
		}

		agitMemberProfileRepository.updateMembershipById(
				profile.getId(),
				agitMemberProfileEntityMapper.toEntityStatus(profile.getStatus()),
				agitMemberProfileEntityMapper.toEntityRole(profile.getRole()),
				profile.getNickname(),
				profile.getProfileImagePath(),
				LocalDateTime.now()
		);

		return agitMemberProfileRepository.findById(profile.getId())
				.map(agitMemberProfileEntityMapper::toDomain)
				.orElseThrow(() -> new IllegalStateException("멤버 프로필 저장 후 조회에 실패했습니다."));
	}

	@Override
	public Optional<AgitMemberProfile> findById(Long id) {
		return agitMemberProfileRepository.findById(id)
				.map(agitMemberProfileEntityMapper::toDomain);
	}

	@Override
	public Optional<AgitMemberProfile> findByAgitIdAndUserUuid(Long agitId, UUID userUuid) {
		return agitMemberProfileRepository.findByAgitIdAndUserUuid(agitId, userUuid)
				.map(agitMemberProfileEntityMapper::toDomain);
	}

	@Override
	public List<AgitMemberProfile> findActiveByAgitId(Long agitId) {
		return agitMemberProfileRepository
				.findByAgitIdAndStatus(agitId, AgitMemberStatus.ACTIVE)
				.stream()
				.map(agitMemberProfileEntityMapper::toDomain)
				.toList();
	}

	@Override
	public Optional<AgitMemberProfile> findActiveHostByAgitId(Long agitId) {
		return agitMemberProfileRepository
				.findByAgitIdAndRoleAndStatus(agitId, AgitMemberRole.HOST, AgitMemberStatus.ACTIVE)
				.map(agitMemberProfileEntityMapper::toDomain);
	}

	@Override
	public long countActiveByAgitId(Long agitId) {
		return agitMemberProfileRepository.countByAgitIdAndStatus(agitId, AgitMemberStatus.ACTIVE);
	}
}
