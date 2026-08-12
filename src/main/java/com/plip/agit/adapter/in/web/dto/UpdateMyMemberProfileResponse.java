package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 아지트 멤버 프로필 수정 응답")
public class UpdateMyMemberProfileResponse {

	@Schema(description = "닉네임", example = "보드왕")
	private String nickname;

	@Schema(description = "프로필 이미지 경로", example = "profiles/guest.png")
	private String profileImagePath;
}
