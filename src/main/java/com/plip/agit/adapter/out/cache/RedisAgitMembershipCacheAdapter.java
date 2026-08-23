package com.plip.agit.adapter.out.cache;

import com.plip.agit.application.port.out.AgitMembershipCachePort;
import com.plip.agit.domain.model.AgitMemberRole;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class RedisAgitMembershipCacheAdapter implements AgitMembershipCachePort {

	static final String KEY_PREFIX = "agit:";
	static final String KEY_SUFFIX = ":members";

	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public void put(UUID agitUuid, UUID userUuid, AgitMemberRole role) {
		if (agitUuid == null || userUuid == null || role == null) {
			return;
		}
		run("put", agitUuid, () -> stringRedisTemplate.opsForHash()
				.put(key(agitUuid), userUuid.toString(), role.name()));
	}

	@Override
	public void remove(UUID agitUuid, UUID userUuid) {
		if (agitUuid == null || userUuid == null) {
			return;
		}
		run("remove", agitUuid, () -> stringRedisTemplate.opsForHash()
				.delete(key(agitUuid), userUuid.toString()));
	}

	@Override
	public void replaceAll(UUID agitUuid, Map<UUID, AgitMemberRole> members) {
		if (agitUuid == null) {
			return;
		}
		run("replaceAll", agitUuid, () -> {
			String redisKey = key(agitUuid);
			stringRedisTemplate.delete(redisKey);
			if (members == null || members.isEmpty()) {
				return;
			}
			Map<String, String> hash = members.entrySet().stream()
					.collect(Collectors.toMap(
							entry -> entry.getKey().toString(),
							entry -> entry.getValue().name()
					));
			stringRedisTemplate.opsForHash().putAll(redisKey, hash);
		});
	}

	@Override
	public void deleteAll(UUID agitUuid) {
		if (agitUuid == null) {
			return;
		}
		run("deleteAll", agitUuid, () -> stringRedisTemplate.delete(key(agitUuid)));
	}

	private void run(String operation, UUID agitUuid, Runnable command) {
		try {
			command.run();
		} catch (RuntimeException first) {
			log.warn("멤버십 Redis {} 실패, 1회 재시도 agitUuid={}: {}",
					operation, agitUuid, first.getMessage());
			try {
				command.run();
			} catch (RuntimeException retry) {
				log.error("멤버십 Redis {} 재시도 실패 agitUuid={}: {}",
						operation, agitUuid, retry.getMessage());
			}
		}
	}

	static String key(UUID agitUuid) {
		return KEY_PREFIX + agitUuid + KEY_SUFFIX;
	}
}
