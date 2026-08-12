package com.plip.agit.adapter.out.persistence;

import com.plip.agit.adapter.out.persistence.mapper.AgitMemberProfileEntityMapper;
import com.plip.agit.application.port.out.ActiveMembershipAgit;
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
	public Optional<AgitMemberProfile> findActiveHostByAgitId(Long agitId) {
		return agitMemberProfileRepository
				.findByAgitIdAndRoleAndStatus(agitId, AgitMemberRole.HOST, AgitMemberStatus.ACTIVE)
				.map(agitMemberProfileEntityMapper::toDomain);
	}

	@Override
	public long countActiveByAgitId(Long agitId) {
		return agitMemberProfileRepository.countByAgitIdAndStatus(agitId, AgitMemberStatus.ACTIVE);
	}

	@Override
	public List<ActiveMembershipAgit> findActiveMembershipAgitsByUserUuid(UUID userUuid) {
		// 정렬: agit.updated_at DESC (임시). 이후 최근 토픽순(Redis/토픽 서비스)으로 교체한다.
		return agitMemberProfileRepository
				.findActiveMembershipAgitsByUserUuid(userUuid, AgitMemberStatus.ACTIVE, AgitStatus.ACTIVE)
				.stream()
				.map(row -> new ActiveMembershipAgit(row.agitUuid(), row.agitName()))
				.toList();
	}
}
