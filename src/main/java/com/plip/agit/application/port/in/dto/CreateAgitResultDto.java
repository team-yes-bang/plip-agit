package com.plip.agit.application.port.in.dto;

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
}
