package com.plip.agit.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgitRepository extends JpaRepository<AgitEntity, Long> {

	Optional<AgitEntity> findByAgitUuid(UUID agitUuid);

	Optional<AgitEntity> findByCode(String code);

	boolean existsByCode(String code);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			UPDATE AgitEntity a
			   SET a.agitName = :agitName,
			       a.description = :description,
			       a.maximumCapacity = :maximumCapacity,
			       a.code = :code,
			       a.status = :status,
			       a.thumbnailPath = :thumbnailPath,
			       a.updatedAt = :updatedAt
			 WHERE a.agitUuid = :agitUuid
			""")
	int updateByAgitUuid(
			@Param("agitUuid") UUID agitUuid,
			@Param("agitName") String agitName,
			@Param("description") String description,
			@Param("maximumCapacity") Integer maximumCapacity,
			@Param("code") String code,
			@Param("status") AgitStatus status,
			@Param("thumbnailPath") String thumbnailPath,
			@Param("updatedAt") LocalDateTime updatedAt
	);
}
