package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "내 아지트 멤버 프로필 수정 요청")
public class UpdateMyMemberProfileRequest {

	/**
	 * TODO: 인증 연동 후 Gateway/JWT에서 userUuid를 추출하도록 교체하고, request body 필드는 제거한다.
	 */
	@Schema(description = "요청자 사용자 UUID (UUIDv7, 임시 body 전달 — 추후 인증에서 추출)", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID userUuid;

	@Schema(description = "닉네임 (선택, 영문·숫자·한글 2~12자, 특수문자·공백 불가). 미전달 시 유지", example = "보드왕")
	private String nickname;

	@Schema(description = "프로필 이미지 경로 (선택). 미전달 시 유지, 빈 문자열이면 제거", example = "profiles/guest.png")
	private String profileImagePath;
}
