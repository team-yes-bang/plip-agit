package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "아지트 랜딩 조회 응답 (표시 전용)")
public class AgitLandingResponse {

	@Schema(description = "아지트 제목", example = "주말 보드게임")
	private String agitName;

	@Schema(description = "소개글", example = "가볍게 즐겨요")
	private String description;

	@Schema(description = "현재 인원 (ACTIVE 멤버 수)", example = "1")
	private long currentMemberCount;

	@Schema(description = "최대 인원", example = "5")
	private int maximumCapacity;

	@Schema(description = "아지트장 닉네임", example = "보드왕")
	private String hostNickname;

	@Schema(description = "섬네일 경로", example = "agits/thumbnails/sample.png")
	private String thumbnailPath;
}
