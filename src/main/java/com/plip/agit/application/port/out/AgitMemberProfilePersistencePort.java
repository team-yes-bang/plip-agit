package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitMemberProfile;
import java.util.Optional;

public interface AgitMemberProfilePersistencePort {

	AgitMemberProfile save(AgitMemberProfile profile);

	Optional<AgitMemberProfile> findActiveHostByAgitId(Long agitId);

	long countActiveByAgitId(Long agitId);
}
