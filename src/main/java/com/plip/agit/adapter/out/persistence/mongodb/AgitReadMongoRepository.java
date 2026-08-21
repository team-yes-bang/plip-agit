package com.plip.agit.adapter.out.persistence.mongodb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgitReadMongoRepository extends MongoRepository<AgitReadDocument, String> {

	Optional<AgitReadDocument> findByCodeAndStatus(String code, String status);

	List<AgitReadDocument> findByStatusAndMembersUserUuidOrderByUpdatedAtDesc(
			String status,
			String userUuid
	);
}
