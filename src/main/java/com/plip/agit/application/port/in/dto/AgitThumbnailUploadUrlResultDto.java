package com.plip.agit.application.port.in.dto;

import java.time.Instant;
import java.util.UUID;

public record AgitThumbnailUploadUrlResultDto(
		UUID uploadKey,
		String thumbnailPath,
		String uploadUrl,
		Instant expiresAt
) {
}
