package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "에러 응답")
public class ErrorResponse {

	@Schema(description = "에러 코드", example = "ALREADY_JOINED")
	private String code;

	@Schema(description = "에러 메시지", example = "이미 참여 중인 아지트입니다.")
	private String message;

	@Schema(description = "이미 가입된 멤버 프로필 ID (해당 시)", example = "1")
	private Long ampId;
}
