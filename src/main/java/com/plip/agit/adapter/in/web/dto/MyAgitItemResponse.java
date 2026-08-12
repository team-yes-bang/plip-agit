package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 아지트 목록 항목")
public class MyAgitItemResponse {

	@Schema(description = "아지트 UUID", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID agitUuid;

	@Schema(description = "아지트 제목", example = "주말 보드게임")
	private String agitName;
}
