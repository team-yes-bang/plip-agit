package com.plip.agit.application.port.out;

import java.time.Instant;

public record PresignedUploadUrl(
		String objectKey,
		String uploadUrl,
		Instant expiresAt
) {
}
