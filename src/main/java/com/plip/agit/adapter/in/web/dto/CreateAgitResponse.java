package com.plip.agit.adapter.in.web.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "아지트 생성 응답")
public class CreateAgitResponse {

	@Schema(description = "아지트 UUID", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID agitUuid;

	@Schema(description = "아지트 제목", example = "주말 보드게임")
	private String agitName;

	@Schema(description = "소개글", example = "가볍게 즐겨요")
	private String description;

	@Schema(description = "최대 인원", example = "5")
	private int maximumCapacity;

	@Schema(description = "초대 코드", example = "AB12CD")
	private String code;

	@Schema(description = "섬네일 경로", example = "agits/thumbnails/sample.png")
	private String thumbnailPath;

	@Schema(description = "방장 프로필 ID (amp_id)", example = "1")
	private Long ampId;

	@Schema(description = "방장 닉네임", example = "보드왕")
	private String nickname;

	@Schema(description = "방장 프로필 이미지 경로", example = "profiles/host.png")
	private String profileImagePath;

	@Schema(description = "방장 역할", example = "HOST")
	private AgitMemberRole role;
}
