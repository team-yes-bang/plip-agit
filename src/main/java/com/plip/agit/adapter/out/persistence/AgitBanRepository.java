package com.plip.agit.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgitBanRepository extends JpaRepository<AgitBanEntity, Long> {

	Optional<AgitBanEntity> findFirstByAgitIdAndUserUuidAndUnbannedAtIsNullOrderByBannedAtDesc(
			Long agitId,
			UUID userUuid
	);
}
