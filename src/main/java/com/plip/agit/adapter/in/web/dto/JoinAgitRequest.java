package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아지트 입장 요청")
public class JoinAgitRequest {

	@Schema(description = "닉네임 (필수, 영문·숫자·한글 2~12자, 특수문자·공백 불가)", example = "보드왕")
	private String nickname;

	@Schema(description = "프로필 이미지 경로 (선택)", example = "profiles/guest.png")
	private String profileImagePath;
}
