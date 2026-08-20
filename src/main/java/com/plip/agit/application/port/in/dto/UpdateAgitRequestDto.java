package com.plip.agit.application.port.in.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateAgitRequestDto {

	/** 인증된 요청자의 userUuid. */
	private final UUID userUuid;
	private final String agitName;
	private final String description;
	private final Integer maximumCapacity;
	private final String thumbnailPath;
}
