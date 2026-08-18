package com.plip.agit.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface AgitReadPersistencePort {

	void replace(AgitReadSnapshot snapshot);

	Optional<AgitReadSnapshot> findByAgitUuid(UUID agitUuid);

	Optional<AgitReadSnapshot> findActiveByCode(String code);

	boolean upsertTopic(UUID agitUuid, AgitReadTopicSnapshot topic);

	boolean removeTopic(UUID agitUuid, String topicId);
}
