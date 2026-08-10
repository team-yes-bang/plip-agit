package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.Agit;
import java.util.Optional;
import java.util.UUID;

public interface AgitPersistencePort {

	Agit save(Agit agit);

	Optional<Agit> findByAgitUuid(UUID agitUuid);

	Optional<Agit> findByCode(String code);

	Optional<Agit> findActiveByCode(String code);

	boolean existsByCode(String code);
}
