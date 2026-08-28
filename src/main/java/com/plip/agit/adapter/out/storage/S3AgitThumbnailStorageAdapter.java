package com.plip.agit.adapter.out.storage;

import com.plip.agit.application.port.out.AgitThumbnailStoragePort;
import com.plip.agit.application.port.out.PresignedUploadUrl;
import com.plip.agit.global.config.AwsProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@ConditionalOnProperty(prefix = "plip.aws", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class S3AgitThumbnailStorageAdapter implements AgitThumbnailStoragePort {

	private final S3Presigner s3Presigner;
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
		Duration ttl = Duration.ofSeconds(awsProperties.presignedUrlTtlSeconds());
		Instant expiresAt = Instant.now().plus(ttl);

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(awsProperties.s3().rawBucket())
				.key(objectKey)
				.contentType(contentType)
				.contentLength(contentLengthBytes)
				.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(ttl)
				.putObjectRequest(putObjectRequest)
				.build();

		String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
		return new PresignedUploadUrl(objectKey, uploadUrl, expiresAt);
	}

	@Override
	public String resolvePublicUrl(String objectKey) {
		if (objectKey == null || objectKey.isBlank()) {
			return null;
		}
		String cdnBaseUrl = awsProperties.s3().cdnBaseUrl();
		if (cdnBaseUrl == null || cdnBaseUrl.isBlank()) {
			return objectKey;
		}
		return cdnBaseUrl.endsWith("/")
				? cdnBaseUrl + objectKey
				: cdnBaseUrl + "/" + objectKey;
	}
}
