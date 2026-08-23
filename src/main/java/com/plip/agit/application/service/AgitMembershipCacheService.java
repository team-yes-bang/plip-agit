package com.plip.agit.application.service;

import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.port.in.ReloadAgitMembershipCacheUseCase;
import com.plip.agit.application.port.in.dto.AgitCachedMemberDto;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitMembershipCachePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitMembershipCacheService implements ReloadAgitMembershipCacheUseCase {

	private final AgitPersistencePort agitPersistencePort;
	private final AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;
	private final AgitMembershipCachePort agitMembershipCachePort;

	@Override
	public List<AgitCachedMemberDto> reload(UUID agitUuid) {
		if (agitUuid == null) {
			throw new IllegalArgumentException("아지트 UUID는 필수입니다.");
		}
		Agit agit = agitPersistencePort.findByAgitUuid(agitUuid)
				.orElseThrow(AgitNotFoundException::new);
		if (agit.getStatus() != AgitStatus.ACTIVE) {
			agitMembershipCachePort.deleteAll(agitUuid);
			throw new AgitNotFoundException();
		}

		List<AgitMemberProfile> activeMembers =
				agitMemberProfilePersistencePort.findActiveByAgitId(agit.getId());
		Map<UUID, AgitMemberRole> snapshot = new LinkedHashMap<>();
		for (AgitMemberProfile profile : activeMembers) {
			snapshot.put(profile.getUserUuid(), profile.getRole());
		}
		agitMembershipCachePort.replaceAll(agitUuid, snapshot);

		return snapshot.entrySet().stream()
				.map(entry -> AgitCachedMemberDto.builder()
						.userUuid(entry.getKey())
						.role(entry.getValue())
						.build())
				.toList();
	}
}
