package com.plip.agit.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "plip.aws")
public record AwsProperties(
		boolean enabled,
		String region,
		int presignedUrlTtlSeconds,
		S3Properties s3
) {

	public record S3Properties(
			String rawBucket,
			String imagePrefix,
			String agitThumbnailPrefix,
			String cdnBaseUrl
	) {
	}
}
