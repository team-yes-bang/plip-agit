package com.plip.agit.domain.model;

import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agit {

	public static final int NAME_MAX_LENGTH = 20;
	public static final int DESCRIPTION_MAX_LENGTH = 100;
	public static final int THUMBNAIL_PATH_MAX_LENGTH = 255;
	public static final int DEFAULT_MAX_CAPACITY = 5;
	public static final int ABSOLUTE_MAX_CAPACITY = 20;
	public static final int CODE_LENGTH = 6;

	private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");

	private UUID agitUuid;
	private String agitName;
	private String description;
	private int maximumCapacity;
	private String code;
	private AgitStatus status;
	private String thumbnailPath;

	/**
	 * @param allowedMaxCapacity 아이템 보유 여부 등을 유스케이스에서 판정한 뒤 넘기는 허용 최대 인원
	 *                           (기본 5, 상한 20)
	 */
	public static Agit create(
			String agitName,
			String description,
			Integer maximumCapacity,
			String code,
			String thumbnailPath,
			int allowedMaxCapacity
	) {
		String normalizedName = requireName(agitName);
		String normalizedDescription = normalizeDescription(description);
		int capacity = requireCapacity(maximumCapacity, allowedMaxCapacity);
		String normalizedCode = requireCode(code);
		String normalizedThumbnailPath = normalizeThumbnailPath(thumbnailPath);

		return Agit.builder()
				.agitUuid(UUID.randomUUID())
				.agitName(normalizedName)
				.description(normalizedDescription)
				.maximumCapacity(capacity)
				.code(normalizedCode)
				.status(AgitStatus.ACTIVE)
				.thumbnailPath(normalizedThumbnailPath)
				.build();
	}

	public static Agit reconstitute(
			UUID agitUuid,
			String agitName,
			String description,
			int maximumCapacity,
			String code,
			AgitStatus status,
			String thumbnailPath
	) {
		return Agit.builder()
				.agitUuid(agitUuid)
				.agitName(agitName)
				.description(description != null ? description : "")
				.maximumCapacity(maximumCapacity)
				.code(code)
				.status(status != null ? status : AgitStatus.ACTIVE)
				.thumbnailPath(thumbnailPath)
				.build();
	}

	public void reissueCode(String newCode) {
		ensureActive();
		this.code = requireCode(newCode);
	}

	public void delete() {
		ensureActive();
		this.status = AgitStatus.DELETED;
	}

	private void ensureActive() {
		if (this.status != AgitStatus.ACTIVE) {
			throw new IllegalStateException("삭제된 아지트에서는 수행할 수 없습니다.");
		}
	}

	private static String requireName(String agitName) {
		if (agitName == null || agitName.isBlank()) {
			throw new IllegalArgumentException("아지트 제목은 필수입니다.");
		}
		String trimmed = agitName.trim();
		if (trimmed.length() > NAME_MAX_LENGTH) {
			throw new IllegalArgumentException("아지트 제목은 " + NAME_MAX_LENGTH + "자 이하여야 합니다.");
		}
		return trimmed;
	}

	private static String normalizeDescription(String description) {
		if (description == null || description.isBlank()) {
			return "";
		}
		String trimmed = description.trim();
		if (trimmed.length() > DESCRIPTION_MAX_LENGTH) {
			throw new IllegalArgumentException("소개글은 " + DESCRIPTION_MAX_LENGTH + "자 이하여야 합니다.");
		}
		return trimmed;
	}

	private static int requireCapacity(Integer maximumCapacity, int allowedMaxCapacity) {
		if (maximumCapacity == null) {
			throw new IllegalArgumentException("인원수는 필수입니다.");
		}
		if (allowedMaxCapacity < 1 || allowedMaxCapacity > ABSOLUTE_MAX_CAPACITY) {
			throw new IllegalArgumentException(
					"허용 최대 인원은 1 이상 " + ABSOLUTE_MAX_CAPACITY + " 이하여야 합니다."
			);
		}
		if (maximumCapacity < 1 || maximumCapacity > allowedMaxCapacity) {
			throw new IllegalArgumentException(
					"인원수는 1 이상 " + allowedMaxCapacity + " 이하여야 합니다."
			);
		}
		return maximumCapacity;
	}

	private static String requireCode(String code) {
		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("초대 코드는 필수입니다.");
		}
		String normalized = code.trim();
		if (!CODE_PATTERN.matcher(normalized).matches()) {
			throw new IllegalArgumentException("초대 코드는 영문 대문자와 숫자로 구성된 6자여야 합니다.");
		}
		return normalized;
	}

	private static String normalizeThumbnailPath(String thumbnailPath) {
		if (thumbnailPath == null || thumbnailPath.isBlank()) {
			return null;
		}
		String trimmed = thumbnailPath.trim();
		if (trimmed.length() > THUMBNAIL_PATH_MAX_LENGTH) {
			throw new IllegalArgumentException(
					"섬네일 경로는 " + THUMBNAIL_PATH_MAX_LENGTH + "자 이하여야 합니다."
			);
		}
		return trimmed;
	}

	@Builder(access = AccessLevel.PRIVATE)
	private Agit(
			UUID agitUuid,
			String agitName,
			String description,
			int maximumCapacity,
			String code,
			AgitStatus status,
			String thumbnailPath
	) {
		this.agitUuid = agitUuid;
		this.agitName = agitName;
		this.description = description;
		this.maximumCapacity = maximumCapacity;
		this.code = code;
		this.status = status;
		this.thumbnailPath = thumbnailPath;
	}
}
