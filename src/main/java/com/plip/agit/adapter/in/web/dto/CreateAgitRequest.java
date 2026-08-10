package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아지트 생성 요청")
public class CreateAgitRequest {

	@Schema(description = "아지트 제목 (필수, 최대 20자)", example = "주말 보드게임")
	private String agitName;

	@Schema(description = "소개글 (선택, 최대 100자)", example = "가볍게 즐겨요")
	private String description;

	@Schema(description = "최대 인원 (필수, 기본 허용 상한 5)", example = "5")
	private Integer maximumCapacity;

	@Schema(description = "섬네일 경로 (선택, 최대 255자)", example = "agits/thumbnails/sample.png")
	private String thumbnailPath;
}
