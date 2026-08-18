package com.plip.agit.application.port.out;

import java.time.Instant;

public record AgitReadTopicSnapshot(
		String topicId,
		Instant startedAt
) {
}
