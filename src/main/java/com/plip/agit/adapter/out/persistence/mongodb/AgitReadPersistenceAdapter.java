package com.plip.agit.adapter.out.persistence.mongodb;

import com.plip.agit.application.port.out.ActiveMembershipAgit;
import com.plip.agit.application.port.out.AgitReadMemberSnapshot;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.application.port.out.AgitReadTopicSnapshot;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class AgitReadPersistenceAdapter implements AgitReadPersistencePort {

	private static final String ACTIVE = AgitStatus.ACTIVE.name();

	private final AgitReadMongoRepository agitReadMongoRepository;
	private final MongoTemplate mongoTemplate;

	@Override
	public void replace(AgitReadSnapshot snapshot) {
		List<AgitReadMemberDocument> members = toMemberDocuments(snapshot.members());
		Query query = Query.query(Criteria.where("_id").is(snapshot.agitUuid().toString()));
		Update update = new Update()
				.set("agitName", snapshot.agitName())
				.set("description", snapshot.description())
				.set("thumbnailPath", snapshot.thumbnailPath())
				.set("code", snapshot.code())
				.set("status", snapshot.status().name())
				.set("maximumCapacity", snapshot.maximumCapacity())
				.set("members", members)
				.set("updatedAt", snapshot.updatedAt())
				.setOnInsert("topics", List.of());
		mongoTemplate.upsert(query, update, AgitReadDocument.class);
	}

	@Override
	public Optional<AgitReadSnapshot> findByAgitUuid(UUID agitUuid) {
		return agitReadMongoRepository.findById(agitUuid.toString())
				.map(this::toSnapshot);
	}

	@Override
	public Optional<AgitReadSnapshot> findActiveByCode(String code) {
		return agitReadMongoRepository.findByCodeAndStatus(code, ACTIVE)
				.map(this::toSnapshot);
	}

	@Override
	public List<ActiveMembershipAgit> findActiveByMemberUserUuid(UUID userUuid) {
		return agitReadMongoRepository
				.findByStatusAndMembersUserUuidOrderByUpdatedAtDesc(ACTIVE, userUuid.toString())
				.stream()
				.map(document -> new ActiveMembershipAgit(
						UUID.fromString(document.getId()),
						document.getAgitName()
				))
				.toList();
	}

	@Override
	public boolean upsertTopic(UUID agitUuid, AgitReadTopicSnapshot topic) {
		Optional<AgitReadDocument> existing = agitReadMongoRepository.findById(agitUuid.toString());
		if (existing.isEmpty()) {
			return false;
		}
		AgitReadDocument document = existing.get();
		List<AgitReadTopicDocument> topics = new ArrayList<>(
				document.getTopics() != null ? document.getTopics() : List.of()
		);
		Optional<AgitReadTopicDocument> matched = topics.stream()
				.filter(item -> topic.topicId().equals(item.getTopicId()))
				.findFirst();
		if (matched.isPresent()) {
			AgitReadTopicDocument current = matched.get();
			if (topic.startedAt() != null) {
				current.setStartedAt(topic.startedAt());
			}
		} else {
			topics.add(new AgitReadTopicDocument(topic.topicId(), topic.startedAt()));
		}
		document.setTopics(topics);
		document.setUpdatedAt(Instant.now());
		agitReadMongoRepository.save(document);
		return true;
	}

	@Override
	public boolean removeTopic(UUID agitUuid, String topicId) {
		Optional<AgitReadDocument> existing = agitReadMongoRepository.findById(agitUuid.toString());
		if (existing.isEmpty()) {
			return false;
		}
		AgitReadDocument document = existing.get();
		List<AgitReadTopicDocument> topics = new ArrayList<>(
				document.getTopics() != null ? document.getTopics() : List.of()
		);
		boolean removed = topics.removeIf(item -> topicId.equals(item.getTopicId()));
		if (!removed) {
			return true;
		}
		document.setTopics(topics);
		document.setUpdatedAt(Instant.now());
		agitReadMongoRepository.save(document);
		return true;
	}

	private List<AgitReadMemberDocument> toMemberDocuments(List<AgitReadMemberSnapshot> members) {
		return members.stream()
				.map(member -> new AgitReadMemberDocument(
						member.userUuid().toString(),
						member.nickname(),
						member.profileImagePath(),
						member.role().name()
				))
				.toList();
	}

	private AgitReadSnapshot toSnapshot(AgitReadDocument document) {
		List<AgitReadMemberSnapshot> members = (document.getMembers() != null ? document.getMembers() : List.<AgitReadMemberDocument>of())
				.stream()
				.map(member -> new AgitReadMemberSnapshot(
						UUID.fromString(member.getUserUuid()),
						member.getNickname(),
						member.getProfileImagePath(),
						AgitMemberRole.valueOf(member.getRole())
				))
				.toList();
		List<AgitReadTopicSnapshot> topics = (document.getTopics() != null ? document.getTopics() : List.<AgitReadTopicDocument>of())
				.stream()
				.map(topic -> new AgitReadTopicSnapshot(topic.getTopicId(), topic.getStartedAt()))
				.toList();
		return new AgitReadSnapshot(
				UUID.fromString(document.getId()),
				document.getAgitName(),
				document.getDescription(),
				document.getThumbnailPath(),
				document.getCode(),
				AgitStatus.valueOf(document.getStatus()),
				document.getMaximumCapacity(),
				members,
				topics,
				document.getUpdatedAt()
		);
	}
}
