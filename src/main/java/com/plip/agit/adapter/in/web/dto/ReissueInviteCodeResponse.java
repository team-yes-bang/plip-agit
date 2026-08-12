package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "초대 코드 재발급 응답")
public class ReissueInviteCodeResponse {

	@Schema(description = "새로 발급된 초대 코드", example = "AB12CD")
	private String code;
}
