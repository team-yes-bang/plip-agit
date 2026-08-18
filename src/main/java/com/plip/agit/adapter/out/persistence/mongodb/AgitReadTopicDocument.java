package com.plip.agit.adapter.out.persistence.mongodb;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgitReadTopicDocument {

	private String topicId;
	private Instant startedAt;

	public AgitReadTopicDocument(String topicId, Instant startedAt) {
		this.topicId = topicId;
		this.startedAt = startedAt;
	}
}
