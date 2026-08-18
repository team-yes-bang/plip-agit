package com.plip.agit.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgitMuteRepository extends JpaRepository<AgitMuteEntity, Long> {

	Optional<AgitMuteEntity> findByAgitIdAndMuterUuidAndMutedUuid(Long agitId, UUID muterUuid, UUID mutedUuid);

	List<AgitMuteEntity> findAllByAgitIdAndMuterUuid(Long agitId, UUID muterUuid);
}
