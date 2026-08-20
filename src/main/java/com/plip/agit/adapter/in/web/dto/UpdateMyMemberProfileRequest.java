package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "내 아지트 멤버 프로필 수정 요청")
public class UpdateMyMemberProfileRequest {

	@Schema(description = "닉네임 (선택, 영문·숫자·한글 2~12자, 특수문자·공백 불가). 미전달 시 유지", example = "보드왕")
	private String nickname;

	@Schema(description = "프로필 이미지 경로 (선택). 미전달 시 유지, 빈 문자열이면 제거", example = "profiles/guest.png")
	private String profileImagePath;
}
