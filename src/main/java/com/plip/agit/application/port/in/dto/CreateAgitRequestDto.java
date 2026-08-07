package com.plip.agit.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateAgitRequestDto {

	private final String agitName;
	private final String description;
	private final Integer maximumCapacity;
}
