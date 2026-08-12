package com.plip.agit.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueInviteCodeResultDto {

	private final String code;
}
