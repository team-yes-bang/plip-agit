package com.plip.agit.application.port.in.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateAgitRequestDto {

	private final String agitName;
	private final String description;
	private final Integer maximumCapacity;
	private final String thumbnailPath;
	/** TODO: 인증 연동 후 Gateway/JWT에서 추출한 userUuid로 교체한다. */
	private final UUID userUuid;
	private final String nickname;
	private final String profileImagePath;
}
