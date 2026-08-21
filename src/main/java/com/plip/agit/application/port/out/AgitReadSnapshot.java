package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 아지트 읽기 문서 스냅샷.
 *
 * <p>{@code topics}는 조회 결과용이다. {@code replace}는 이 필드를 쓰지 않고 기존 배열을 유지한다.
 */
public record AgitReadSnapshot(
		UUID agitUuid,
		String agitName,
		String description,
		String thumbnailPath,
		String code,
		AgitStatus status,
		int maximumCapacity,
		List<AgitReadMemberSnapshot> members,
		List<AgitReadTopicSnapshot> topics,
		Instant updatedAt
) {
}
