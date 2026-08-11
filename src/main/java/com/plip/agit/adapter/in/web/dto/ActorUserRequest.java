package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "액터 사용자 UUID 요청 (임시 — 인증 연동 전)")
public class ActorUserRequest {

	/**
	 * TODO: 인증 연동 후 Gateway/JWT에서 userUuid를 추출하도록 교체하고, request body 필드는 제거한다.
	 */
	@Schema(description = "요청자 사용자 UUID (UUIDv7, 임시 body 전달 — 추후 인증에서 추출)", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID userUuid;
}
