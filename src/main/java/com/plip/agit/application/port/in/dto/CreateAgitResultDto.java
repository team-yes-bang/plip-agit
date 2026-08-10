package com.plip.agit.application.port.in.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateAgitResultDto {

	private final UUID agitUuid;
	private final String agitName;
	private final String description;
	private final int maximumCapacity;
	private final String code;
	private final String thumbnailPath;
	private final Long ampId;
	private final String nickname;
	private final String profileImagePath;
	private final AgitMemberRole role;
}
