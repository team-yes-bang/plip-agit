package com.plip.agit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.plip.agit.application.port.out.AgitReadTopicSnapshot;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgitReadTopicWindowTest {

	@Test
	void upsert_appendsNewTopic() {
		List<AgitReadTopicSnapshot> current = List.of(
				new AgitReadTopicSnapshot("t1", Instant.parse("2026-01-01T00:00:00Z"))
		);

		List<AgitReadTopicSnapshot> next = AgitReadTopicWindow.upsert(
				current, new AgitReadTopicSnapshot("t2", Instant.parse("2026-02-01T00:00:00Z"))
		);

		assertEquals(List.of("t1", "t2"), next.stream().map(AgitReadTopicSnapshot::topicId).toList());
	}

	@Test
	void upsert_updatesStartedAtWithoutMoving() {
		Instant first = Instant.parse("2026-01-01T00:00:00Z");
		Instant second = Instant.parse("2026-02-01T00:00:00Z");
		List<AgitReadTopicSnapshot> current = List.of(
				new AgitReadTopicSnapshot("t1", first),
				new AgitReadTopicSnapshot("t2", second)
		);

		List<AgitReadTopicSnapshot> next = AgitReadTopicWindow.upsert(
				current, new AgitReadTopicSnapshot("t1", Instant.parse("2026-03-01T00:00:00Z"))
		);

		assertEquals("t1", next.get(0).topicId());
		assertEquals(Instant.parse("2026-03-01T00:00:00Z"), next.get(0).startedAt());
		assertEquals("t2", next.get(1).topicId());
	}

	@Test
	void upsert_keepsExistingStartedAtWhenIncomingNull() {
		Instant startedAt = Instant.parse("2026-01-01T00:00:00Z");
		List<AgitReadTopicSnapshot> next = AgitReadTopicWindow.upsert(
				List.of(new AgitReadTopicSnapshot("t1", startedAt)),
				new AgitReadTopicSnapshot("t1", null)
		);

		assertEquals(startedAt, next.get(0).startedAt());
	}

	@Test
	void upsert_dropsOldestWhenOverMaxSize() {
		List<AgitReadTopicSnapshot> current = new ArrayList<>();
		for (int i = 1; i <= AgitReadTopicWindow.MAX_SIZE; i++) {
			current.add(new AgitReadTopicSnapshot("t" + i, null));
		}

		List<AgitReadTopicSnapshot> next = AgitReadTopicWindow.upsert(
				current, new AgitReadTopicSnapshot("t-new", null)
		);

		assertEquals(AgitReadTopicWindow.MAX_SIZE, next.size());
		assertEquals("t2", next.get(0).topicId());
		assertEquals("t-new", next.get(next.size() - 1).topicId());
		assertFalse(next.stream().anyMatch(topic -> "t1".equals(topic.topicId())));
	}

	@Test
	void trim_keepsLastMaxSize() {
		List<AgitReadTopicSnapshot> topics = new ArrayList<>();
		for (int i = 1; i <= 35; i++) {
			topics.add(new AgitReadTopicSnapshot("t" + i, null));
		}

		List<AgitReadTopicSnapshot> trimmed = AgitReadTopicWindow.trim(topics);

		assertEquals(30, trimmed.size());
		assertEquals("t6", trimmed.get(0).topicId());
		assertEquals("t35", trimmed.get(29).topicId());
		assertNull(trimmed.get(0).startedAt());
	}
}
