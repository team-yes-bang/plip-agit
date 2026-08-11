package com.plip.agit.application.port.in.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinAgitRequestDto {

	private final UUID userUuid;
	private final String nickname;
	private final String profileImagePath;
}
