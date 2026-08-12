package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitMemberProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgitMemberProfilePersistencePort {

	AgitMemberProfile save(AgitMemberProfile profile);

	Optional<AgitMemberProfile> findById(Long id);

	Optional<AgitMemberProfile> findByAgitIdAndUserUuid(Long agitId, UUID userUuid);

	Optional<AgitMemberProfile> findActiveHostByAgitId(Long agitId);

	long countActiveByAgitId(Long agitId);

	/**
	 * ACTIVE 멤버십·ACTIVE 아지트 목록. 정렬은 agit.updated_at DESC(임시).
	 */
	List<ActiveMembershipAgit> findActiveMembershipAgitsByUserUuid(UUID userUuid);
}
