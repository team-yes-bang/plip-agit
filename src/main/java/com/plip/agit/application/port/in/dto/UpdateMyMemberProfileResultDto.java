package com.plip.agit.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMyMemberProfileResultDto {

	private final String nickname;
	private final String profileImagePath;
}
