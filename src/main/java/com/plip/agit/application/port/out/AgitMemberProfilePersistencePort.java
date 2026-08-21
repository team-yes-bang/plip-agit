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

	List<AgitMemberProfile> findActiveByAgitId(Long agitId);

	long countActiveByAgitId(Long agitId);
}
