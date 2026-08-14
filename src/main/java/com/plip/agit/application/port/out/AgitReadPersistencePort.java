package com.plip.agit.application.port.out;

import java.util.Optional;

public interface AgitReadPersistencePort {

	void replace(AgitReadSnapshot snapshot);

	Optional<AgitReadSnapshot> findActiveByCode(String code);
}
