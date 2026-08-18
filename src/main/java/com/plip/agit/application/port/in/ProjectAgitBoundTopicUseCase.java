package com.plip.agit.application.port.in;

import java.time.Instant;
import java.util.UUID;

public interface ProjectAgitBoundTopicUseCase {

	void bind(UUID agitUuid, String topicId, Instant startedAt);

	void unbind(UUID agitUuid, String topicId);

	void start(UUID agitUuid, String topicId, Instant startedAt);
}
