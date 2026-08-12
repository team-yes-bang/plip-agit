package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아지트 정보 변경 요청")
public class UpdateAgitRequest {

	/**
	 * TODO: 인증 연동 후 Gateway/JWT에서 userUuid를 추출하도록 교체하고, request body 필드는 제거한다.
	 */
	@Schema(description = "요청자 사용자 UUID (UUIDv7, 임시 body 전달 — 추후 인증에서 추출)", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID userUuid;

	@Schema(description = "아지트 제목 (필수, 최대 20자)", example = "주말 보드게임")
	private String agitName;

	@Schema(description = "소개글 (선택, 최대 100자)", example = "가볍게 즐겨요")
	private String description;

	@Schema(description = "최대 인원 (필수, 기본 허용 상한 5, 현재 ACTIVE 인원 이상)", example = "5")
	private Integer maximumCapacity;

	@Schema(description = "섬네일 경로 (선택, 최대 255자)", example = "agits/thumbnails/sample.png")
	private String thumbnailPath;
}
