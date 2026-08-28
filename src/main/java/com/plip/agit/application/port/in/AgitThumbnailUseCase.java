package com.plip.agit.application.port.in;

import com.plip.agit.application.port.in.dto.AgitThumbnailUploadUrlResultDto;
import java.util.UUID;

public interface AgitThumbnailUseCase {

	AgitThumbnailUploadUrlResultDto issueUploadUrl(long contentLengthBytes, String contentType);

	AgitThumbnailUploadUrlResultDto issueUploadUrlForAgit(
			UUID agitUuid,
			UUID actorUserUuid,
			long contentLengthBytes,
			String contentType
	);
}
