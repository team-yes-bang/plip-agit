package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitMemberProfile;

public interface AgitMemberProfilePersistencePort {

	AgitMemberProfile save(AgitMemberProfile profile);
}
