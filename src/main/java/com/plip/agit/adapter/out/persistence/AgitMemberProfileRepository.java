package com.plip.agit.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgitMemberProfileRepository extends JpaRepository<AgitMemberProfileEntity, Long> {

	Optional<AgitMemberProfileEntity> findByAgitIdAndUserUuid(Long agitId, UUID userUuid);

	boolean existsByAgitIdAndUserUuid(Long agitId, UUID userUuid);

	Optional<AgitMemberProfileEntity> findByAgitIdAndRoleAndStatus(
			Long agitId,
			AgitMemberRole role,
			AgitMemberStatus status
	);

	long countByAgitIdAndStatus(Long agitId, AgitMemberStatus status);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			UPDATE AgitMemberProfileEntity p
			   SET p.status = :status,
			       p.updatedAt = :updatedAt
			 WHERE p.id = :id
			""")
	int updateStatusById(
			@Param("id") Long id,
			@Param("status") AgitMemberStatus status,
			@Param("updatedAt") LocalDateTime updatedAt
	);
}
