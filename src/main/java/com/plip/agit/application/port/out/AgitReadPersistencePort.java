package com.plip.agit.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface AgitReadPersistencePort {

	/**
	 * 메타·멤버를 upsert한다. 기존 문서의 {@code topics}는 유지하고, 신규 insert 시에만 빈 배열로 둔다.
	 */
	void replace(AgitReadSnapshot snapshot);

	Optional<AgitReadSnapshot> findByAgitUuid(UUID agitUuid);

	Optional<AgitReadSnapshot> findActiveByCode(String code);

	boolean upsertTopic(UUID agitUuid, AgitReadTopicSnapshot topic);

	boolean removeTopic(UUID agitUuid, String topicId);
}
