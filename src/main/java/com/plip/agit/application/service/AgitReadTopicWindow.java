package com.plip.agit.application.service;

import com.plip.agit.application.port.out.AgitReadTopicSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * 아지트 read model에 유지하는 묶인 토픽 슬라이딩 윈도우.
 * 배열 앞쪽이 더 오래 바인딩된 항목이며, 신규 bind는 뒤에 붙인 뒤 최대 {@link #MAX_SIZE}개만 남긴다.
 */
public final class AgitReadTopicWindow {

	public static final int MAX_SIZE = 30;

	private AgitReadTopicWindow() {
	}

	public static List<AgitReadTopicSnapshot> upsert(
			List<AgitReadTopicSnapshot> current,
			AgitReadTopicSnapshot topic
	) {
		List<AgitReadTopicSnapshot> next = new ArrayList<>(
				current != null ? current : List.of()
		);
		int index = indexOf(next, topic.topicId());
		if (index >= 0) {
			AgitReadTopicSnapshot existing = next.get(index);
			next.set(index, new AgitReadTopicSnapshot(
					existing.topicId(),
					topic.startedAt() != null ? topic.startedAt() : existing.startedAt()
			));
		} else {
			next.add(topic);
		}
		return trim(next);
	}

	public static List<AgitReadTopicSnapshot> trim(List<AgitReadTopicSnapshot> topics) {
		if (topics == null || topics.isEmpty()) {
			return List.of();
		}
		if (topics.size() <= MAX_SIZE) {
			return List.copyOf(topics);
		}
		return List.copyOf(topics.subList(topics.size() - MAX_SIZE, topics.size()));
	}

	private static int indexOf(List<AgitReadTopicSnapshot> topics, String topicId) {
		for (int i = 0; i < topics.size(); i++) {
			if (topicId.equals(topics.get(i).topicId())) {
				return i;
			}
		}
		return -1;
	}
}
