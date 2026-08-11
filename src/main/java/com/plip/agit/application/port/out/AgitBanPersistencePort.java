package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitBan;
import java.util.Optional;
import java.util.UUID;

public interface AgitBanPersistencePort {

	AgitBan save(AgitBan ban);

	Optional<AgitBan> findActiveByAgitIdAndUserUuid(Long agitId, UUID userUuid);
}
