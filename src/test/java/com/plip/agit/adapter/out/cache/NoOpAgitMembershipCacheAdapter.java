package com.plip.agit.adapter.out.cache;

import com.plip.agit.application.port.out.AgitMembershipCachePort;
import com.plip.agit.domain.model.AgitMemberRole;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class NoOpAgitMembershipCacheAdapter implements AgitMembershipCachePort {

	@Override
	public void put(UUID agitUuid, UUID userUuid, AgitMemberRole role) {
		// test profile: Redis 없음
	}

	@Override
	public void remove(UUID agitUuid, UUID userUuid) {
		// test profile: Redis 없음
	}

	@Override
	public void replaceAll(UUID agitUuid, Map<UUID, AgitMemberRole> members) {
		// test profile: Redis 없음
	}

	@Override
	public void deleteAll(UUID agitUuid) {
		// test profile: Redis 없음
	}
}
