package com.plip.agit.adapter.in.web.dto;

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
}
