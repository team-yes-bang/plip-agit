package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitMute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgitMutePersistencePort {

	AgitMute save(AgitMute mute);

	Optional<AgitMute> findByAgitIdAndMuterUuidAndMutedUuid(Long agitId, UUID muterUuid, UUID mutedUuid);

	List<AgitMute> findAllByAgitIdAndMuterUuid(Long agitId, UUID muterUuid);

	void delete(AgitMute mute);
}
