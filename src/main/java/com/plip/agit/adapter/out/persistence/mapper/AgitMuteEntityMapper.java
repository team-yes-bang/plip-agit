package com.plip.agit.adapter.out.persistence.mapper;

import com.plip.agit.adapter.out.persistence.AgitMuteEntity;
import com.plip.agit.domain.model.AgitMute;
import org.springframework.stereotype.Component;

@Component
public class AgitMuteEntityMapper {

	public AgitMuteEntity toEntity(AgitMute domain) {
		return AgitMuteEntity.builder()
				.agitId(domain.getAgitId())
				.muterUuid(domain.getMuterUuid())
				.mutedUuid(domain.getMutedUuid())
				.build();
	}

	public AgitMute toDomain(AgitMuteEntity entity) {
		return AgitMute.reconstitute(
				entity.getId(),
				entity.getAgitId(),
				entity.getMuterUuid(),
				entity.getMutedUuid()
		);
	}
}
