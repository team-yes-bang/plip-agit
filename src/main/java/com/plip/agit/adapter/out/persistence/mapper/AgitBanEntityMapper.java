package com.plip.agit.adapter.out.persistence.mapper;

import com.plip.agit.adapter.out.persistence.AgitBanEntity;
import com.plip.agit.domain.model.AgitBan;
import org.springframework.stereotype.Component;

@Component
public class AgitBanEntityMapper {

	public AgitBanEntity toEntity(AgitBan domain) {
		return AgitBanEntity.builder()
				.agitId(domain.getAgitId())
				.userUuid(domain.getUserUuid())
				.ampId(domain.getAmpId())
				.bannedNickname(domain.getBannedNickname())
				.bannedAt(domain.getBannedAt())
				.unbannedAt(domain.getUnbannedAt())
				.build();
	}

	public AgitBan toDomain(AgitBanEntity entity) {
		return AgitBan.reconstitute(
				entity.getId(),
				entity.getAgitId(),
				entity.getUserUuid(),
				entity.getAmpId(),
				entity.getBannedNickname(),
				entity.getBannedAt(),
				entity.getUnbannedAt()
		);
	}
}
