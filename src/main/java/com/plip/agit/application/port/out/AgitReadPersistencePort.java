package com.plip.agit.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgitReadPersistencePort {

	/**
	 * 메타·멤버를 upsert한다. 기존 문서의 {@code topics}는 유지하고, 신규 insert 시에만 빈 배열로 둔다.
	 */
	void replace(AgitReadSnapshot snapshot);

	Optional<AgitReadSnapshot> findByAgitUuid(UUID agitUuid);

	Optional<AgitReadSnapshot> findActiveByCode(String code);

	/**
	 * ACTIVE 아지트 중 해당 유저가 멤버인 목록. 정렬은 {@code updatedAt} DESC(임시).
	 */
	List<ActiveMembershipAgit> findActiveByMemberUserUuid(UUID userUuid);

	boolean upsertTopic(UUID agitUuid, AgitReadTopicSnapshot topic);

	boolean removeTopic(UUID agitUuid, String topicId);
}
