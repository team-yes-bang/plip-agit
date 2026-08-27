package com.plip.agit.adapter.in.web.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinRequestItemResponse {

	private Long ampId;
	private UUID userUuid;
	private String nickname;
	private String profileImagePath;
}
