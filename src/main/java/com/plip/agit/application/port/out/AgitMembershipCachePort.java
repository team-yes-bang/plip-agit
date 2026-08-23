package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitMemberRole;
import java.util.Map;
import java.util.UUID;

/**
 * ACTIVE 멤버십 가드용 Redis Hash. 정본은 MySQL이며, 이 포트는 커밋 이후 복제본만 갱신한다.
 * 키: agit:{agitUuid}:members, 필드 userUuid, 값 HOST|GUEST. TTL 없음.
 */
public interface AgitMembershipCachePort {

	void put(UUID agitUuid, UUID userUuid, AgitMemberRole role);

	void remove(UUID agitUuid, UUID userUuid);

	void replaceAll(UUID agitUuid, Map<UUID, AgitMemberRole> members);

	void deleteAll(UUID agitUuid);
}
