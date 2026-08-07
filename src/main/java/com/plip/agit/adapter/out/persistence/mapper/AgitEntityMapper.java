package com.plip.agit.adapter.out.persistence.mapper;

import com.plip.agit.adapter.out.persistence.AgitEntity;
import com.plip.agit.adapter.out.persistence.AgitStatus;
import com.plip.agit.domain.model.Agit;
import org.springframework.stereotype.Component;

@Component
public class AgitEntityMapper {

	public AgitEntity toEntity(Agit domain) {
		return AgitEntity.builder()
				.agitUuid(domain.getAgitUuid())
				.agitName(domain.getAgitName())
				.description(domain.getDescription() != null ? domain.getDescription() : "")
				.maximumCapacity(domain.getMaximumCapacity())
				.code(domain.getCode())
				.status(toEntityStatus(domain.getStatus()))
				.build();
	}

	public Agit toDomain(AgitEntity entity) {
		return Agit.reconstitute(
				entity.getAgitUuid(),
				entity.getAgitName(),
				entity.getDescription(),
				entity.getMaximumCapacity(),
				entity.getCode(),
				toDomainStatus(entity.getStatus())
		);
	}

	public AgitStatus toEntityStatus(com.plip.agit.domain.model.AgitStatus status) {
		if (status == null) {
			return AgitStatus.ACTIVE;
		}
		return AgitStatus.valueOf(status.name());
	}

	public com.plip.agit.domain.model.AgitStatus toDomainStatus(AgitStatus status) {
		if (status == null) {
			return com.plip.agit.domain.model.AgitStatus.ACTIVE;
		}
		return com.plip.agit.domain.model.AgitStatus.valueOf(status.name());
	}
}
