package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "아지트 내 뮤트 목록 항목")
public class MuteItemResponse {

	@Schema(description = "뮤트된 사용자 UUID", example = "01912345-6789-7abc-def0-123456789abc")
	private UUID mutedUuid;
}
