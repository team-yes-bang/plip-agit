package com.plip.agit.adapter.out.persistence.mongodb;

import com.plip.agit.application.port.out.AgitReadMemberSnapshot;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class AgitReadPersistenceAdapter implements AgitReadPersistencePort {

	private static final String ACTIVE = AgitStatus.ACTIVE.name();

	private final AgitReadMongoRepository agitReadMongoRepository;

	@Override
	public void replace(AgitReadSnapshot snapshot) {
		agitReadMongoRepository.save(toDocument(snapshot));
	}

	@Override
	public Optional<AgitReadSnapshot> findActiveByCode(String code) {
		return agitReadMongoRepository.findByCodeAndStatus(code, ACTIVE)
				.map(this::toSnapshot);
	}

	private AgitReadDocument toDocument(AgitReadSnapshot snapshot) {
		List<AgitReadMemberDocument> members = snapshot.members().stream()
				.map(member -> new AgitReadMemberDocument(
						member.userUuid().toString(),
						member.nickname(),
						member.profileImagePath(),
						member.role().name()
				))
				.toList();
		return new AgitReadDocument(
				snapshot.agitUuid().toString(),
				snapshot.agitName(),
				snapshot.description(),
				snapshot.thumbnailPath(),
				snapshot.code(),
				snapshot.status().name(),
				snapshot.maximumCapacity(),
				members,
				snapshot.updatedAt()
		);
	}

	private AgitReadSnapshot toSnapshot(AgitReadDocument document) {
		List<AgitReadMemberSnapshot> members = document.getMembers().stream()
				.map(member -> new AgitReadMemberSnapshot(
						UUID.fromString(member.getUserUuid()),
						member.getNickname(),
						member.getProfileImagePath(),
						AgitMemberRole.valueOf(member.getRole())
				))
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
				document.getUpdatedAt()
		);
	}
}
