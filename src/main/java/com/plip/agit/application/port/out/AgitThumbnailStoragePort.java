package com.plip.agit.application.port.out;

import java.util.UUID;

public interface AgitThumbnailStoragePort {

	String buildAgitThumbnailKey(UUID uploadKey);

	PresignedUploadUrl createPresignedPutUrl(UUID uploadKey, String contentType, long contentLengthBytes);

	String resolvePublicUrl(String objectKey);
}
