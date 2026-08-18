package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
