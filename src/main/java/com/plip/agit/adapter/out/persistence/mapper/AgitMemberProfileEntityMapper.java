package com.plip.agit.adapter.out.persistence.mapper;

import com.plip.agit.adapter.out.persistence.AgitMemberProfileEntity;
import com.plip.agit.adapter.out.persistence.AgitMemberRole;
import com.plip.agit.adapter.out.persistence.AgitMemberStatus;
import com.plip.agit.domain.model.AgitMemberProfile;
import org.springframework.stereotype.Component;

@Component
public class AgitMemberProfileEntityMapper {

	public AgitMemberProfileEntity toEntity(AgitMemberProfile domain) {
		return AgitMemberProfileEntity.builder()
				.agitId(domain.getAgitId())
				.userUuid(domain.getUserUuid())
				.nickname(domain.getNickname())
				.profileImagePath(domain.getProfileImagePath())
				.status(toEntityStatus(domain.getStatus()))
				.role(toEntityRole(domain.getRole()))
				.applyItems(domain.getApplyItems())
				.build();
	}

	public AgitMemberProfile toDomain(AgitMemberProfileEntity entity) {
		return AgitMemberProfile.reconstitute(
				entity.getId(),
				entity.getAgitId(),
				entity.getUserUuid(),
				entity.getNickname(),
				entity.getProfileImagePath(),
				toDomainStatus(entity.getStatus()),
				toDomainRole(entity.getRole()),
				entity.getApplyItems()
		);
	}

	public AgitMemberStatus toEntityStatus(com.plip.agit.domain.model.AgitMemberStatus status) {
		if (status == null) {
			return AgitMemberStatus.ACTIVE;
		}
		return AgitMemberStatus.valueOf(status.name());
	}

	public com.plip.agit.domain.model.AgitMemberStatus toDomainStatus(AgitMemberStatus status) {
		if (status == null) {
			return com.plip.agit.domain.model.AgitMemberStatus.ACTIVE;
		}
		return com.plip.agit.domain.model.AgitMemberStatus.valueOf(status.name());
	}

	public AgitMemberRole toEntityRole(com.plip.agit.domain.model.AgitMemberRole role) {
		if (role == null) {
			return AgitMemberRole.GUEST;
		}
		return AgitMemberRole.valueOf(role.name());
	}

	public com.plip.agit.domain.model.AgitMemberRole toDomainRole(AgitMemberRole role) {
		if (role == null) {
			return com.plip.agit.domain.model.AgitMemberRole.GUEST;
		}
		return com.plip.agit.domain.model.AgitMemberRole.valueOf(role.name());
	}
}
