package com.plip.agit.adapter.in.web.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "아지트 입장 응답")
public class JoinAgitResponse {

	@Schema(description = "아지트 UUID", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID agitUuid;

	@Schema(description = "멤버 프로필 ID (amp_id)", example = "1")
	private Long ampId;

	@Schema(description = "닉네임", example = "보드왕")
	private String nickname;

	@Schema(description = "프로필 이미지 경로", example = "profiles/guest.png")
	private String profileImagePath;

	@Schema(description = "역할", example = "GUEST")
	private AgitMemberRole role;
}
