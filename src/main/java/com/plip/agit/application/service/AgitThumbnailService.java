package com.plip.agit.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.plip.agit.application.port.in.AgitThumbnailUseCase;
import com.plip.agit.application.port.in.dto.AgitThumbnailUploadUrlResultDto;
import com.plip.agit.application.port.out.AgitThumbnailStoragePort;
import com.plip.agit.application.port.out.PresignedUploadUrl;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.application.exception.NotAgitHostException;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.exception.AgitNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgitThumbnailService implements AgitThumbnailUseCase {

	private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
	private static final long MAX_THUMBNAIL_BYTES = 2L * 1024 * 1024;

	private final AgitThumbnailStoragePort agitThumbnailStoragePort;
	private final AgitPersistencePort agitPersistencePort;
	private final AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;

	@Override
	@Transactional(readOnly = true)
	public AgitThumbnailUploadUrlResultDto issueUploadUrl(long contentLengthBytes, String contentType) {
		UUID uploadKey = UuidCreator.getTimeOrderedEpoch();
		return issueForKey(uploadKey, contentLengthBytes, contentType);
	}

	@Override
	@Transactional(readOnly = true)
	public AgitThumbnailUploadUrlResultDto issueUploadUrlForAgit(
			UUID agitUuid,
			UUID actorUserUuid,
			long contentLengthBytes,
			String contentType
	) {
		Agit agit = agitPersistencePort.findByAgitUuid(agitUuid)
				.orElseThrow(AgitNotFoundException::new);
		requireActiveHost(agit.getId(), actorUserUuid);
		return issueForKey(agitUuid, contentLengthBytes, contentType);
	}

	private AgitThumbnailUploadUrlResultDto issueForKey(
			UUID uploadKey,
			long contentLengthBytes,
			String contentType
	) {
		String resolvedContentType = resolveContentType(contentType);
		long resolvedContentLength = requireValidContentLength(contentLengthBytes);
		PresignedUploadUrl presigned = agitThumbnailStoragePort.createPresignedPutUrl(
				uploadKey,
				resolvedContentType,
				resolvedContentLength
		);
		return new AgitThumbnailUploadUrlResultDto(
				uploadKey,
				presigned.objectKey(),
				presigned.uploadUrl(),
				presigned.expiresAt()
		);
	}

	private void requireActiveHost(Long agitId, UUID actorUserUuid) {
		AgitMemberProfile actor = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agitId, actorUserUuid)
				.orElseThrow(NotAgitHostException::new);
		if (actor.getRole() != AgitMemberRole.HOST || actor.getStatus() != AgitMemberStatus.ACTIVE) {
			throw new NotAgitHostException();
		}
	}

	private static String resolveContentType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			return DEFAULT_CONTENT_TYPE;
		}
		String normalized = contentType.trim().toLowerCase();
		if (!DEFAULT_CONTENT_TYPE.equals(normalized)) {
			throw new IllegalArgumentException("contentType must be image/jpeg");
		}
		return normalized;
	}

	private static long requireValidContentLength(long contentLengthBytes) {
		if (contentLengthBytes <= 0) {
			throw new IllegalArgumentException("contentLengthBytes must be positive");
		}
		if (contentLengthBytes > MAX_THUMBNAIL_BYTES) {
			throw new IllegalArgumentException("thumbnail must be 2MB or smaller");
		}
		return contentLengthBytes;
	}
}
