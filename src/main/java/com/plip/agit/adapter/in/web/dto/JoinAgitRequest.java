package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아지트 입장 요청")
public class JoinAgitRequest {

	/**
	 * TODO: 인증 연동 후 Gateway/JWT에서 userUuid를 추출하도록 교체하고, request body 필드는 제거한다.
	 */
	@Schema(description = "사용자 UUID (UUIDv7, 임시 body 전달 — 추후 인증에서 추출)", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID userUuid;

	@Schema(description = "닉네임 (필수, 영문·숫자·한글 2~12자, 특수문자·공백 불가)", example = "보드왕")
	private String nickname;

	@Schema(description = "프로필 이미지 경로 (선택)", example = "profiles/guest.png")
	private String profileImagePath;
}
