package com.plip.agit.adapter.out.storage;

import com.plip.agit.application.port.out.AgitThumbnailStoragePort;
import com.plip.agit.application.port.out.PresignedUploadUrl;
import com.plip.agit.global.config.AwsProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "plip.aws", name = "enabled", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class NoOpAgitThumbnailStorageAdapter implements AgitThumbnailStoragePort {

	private final AwsProperties awsProperties;

	@Override
	public String buildAgitThumbnailKey(UUID uploadKey) {
		return awsProperties.s3().imagePrefix()
				+ awsProperties.s3().agitThumbnailPrefix()
				+ uploadKey
				+ ".jpg";
	}

	@Override
	public PresignedUploadUrl createPresignedPutUrl(
			UUID uploadKey,
			String contentType,
			long contentLengthBytes
	) {
		String objectKey = buildAgitThumbnailKey(uploadKey);
		Instant expiresAt = Instant.now().plus(Duration.ofSeconds(awsProperties.presignedUrlTtlSeconds()));
		String uploadUrl = "http://localhost/stub-presigned-put/" + objectKey
				+ "?contentLength=" + contentLengthBytes;
		log.warn("AWS disabled — stub presigned PUT URL for {}", objectKey);
		return new PresignedUploadUrl(objectKey, uploadUrl, expiresAt);
	}

	@Override
	public String resolvePublicUrl(String objectKey) {
		if (objectKey == null || objectKey.isBlank()) {
			return null;
		}
		return "/stub-media/" + objectKey;
	}
}
