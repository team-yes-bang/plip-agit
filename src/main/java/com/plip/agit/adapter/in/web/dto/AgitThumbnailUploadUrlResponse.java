package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "아지트 썸네일 Presigned PUT URL 발급 응답")
public record AgitThumbnailUploadUrlResponse(
		@Schema(description = "업로드 식별자 (생성 전 임시 UUID 또는 agitUuid)") UUID uploadKey,
		@Schema(description = "S3 object key (raw bucket images/agit/*.jpg)", example = "images/agit/550e8400-e29b-41d4-a716-446655440000.jpg")
		String thumbnailPath,
		@Schema(description = "Presigned PUT URL") String uploadUrl,
		@Schema(description = "Presigned URL 만료 시각 (UTC)") Instant expiresAt
) {
}
