package com.plip.agit.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgitMemberProfileRepository extends JpaRepository<AgitMemberProfileEntity, Long> {

	Optional<AgitMemberProfileEntity> findByAgitIdAndUserUuid(Long agitId, UUID userUuid);

	boolean existsByAgitIdAndUserUuid(Long agitId, UUID userUuid);
}
