package com.plip.agit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.agit.application.exception.AgitMemberNotActiveException;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.port.in.RefreshAgitReadModelUseCase;
import com.plip.agit.application.port.in.dto.AgitDetailResultDto;
import com.plip.agit.application.port.out.AgitBanPersistencePort;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.port.out.AgitReadMemberSnapshot;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.application.port.out.AgitReadTopicSnapshot;
import com.plip.agit.application.port.out.EventPublisherPort;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgitServiceGetAgitTest {

	@Mock
	private AgitPersistencePort agitPersistencePort;

	@Mock
	private AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;

	@Mock
	private AgitBanPersistencePort agitBanPersistencePort;

	@Mock
	private AgitReadPersistencePort agitReadPersistencePort;

	@Mock
	private RefreshAgitReadModelUseCase refreshAgitReadModelUseCase;

	@Mock
	private EventPublisherPort eventPublisherPort;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private AgitService agitService;

	@Test
	void getAgit_usesMongoWhenHit() {
		UUID agitUuid = UUID.randomUUID();
		UUID hostUuid = UUID.randomUUID();
		AgitReadSnapshot snapshot = snapshot(agitUuid, hostUuid, AgitStatus.ACTIVE);
		when(agitReadPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(snapshot));

		AgitDetailResultDto result = agitService.getAgit(agitUuid, hostUuid);

		assertEquals(agitUuid, result.getAgitUuid());
		assertEquals("주말 보드게임", result.getAgitName());
		assertEquals(AgitMemberRole.HOST, result.getMyRole());
		assertEquals("보드왕", result.getHostNickname());
		assertEquals(1, result.getTopics().size());
		assertEquals("topic-1", result.getTopics().get(0).getTopicId());
		verify(refreshAgitReadModelUseCase, never()).refresh(agitUuid);
	}

	@Test
	void getAgit_refreshesThenReadsOnMongoMiss() {
		UUID agitUuid = UUID.randomUUID();
		UUID hostUuid = UUID.randomUUID();
		AgitReadSnapshot snapshot = snapshot(agitUuid, hostUuid, AgitStatus.ACTIVE);
		when(agitReadPersistencePort.findByAgitUuid(agitUuid))
				.thenReturn(Optional.empty(), Optional.of(snapshot));

		AgitDetailResultDto result = agitService.getAgit(agitUuid, hostUuid);

		assertEquals("주말 보드게임", result.getAgitName());
		assertEquals(AgitMemberRole.HOST, result.getMyRole());
		verify(refreshAgitReadModelUseCase).refresh(agitUuid);
	}

	@Test
	void getAgit_throwsWhenMissingAfterRefresh() {
		UUID agitUuid = UUID.randomUUID();
		when(agitReadPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.empty());

		assertThrows(
				AgitNotFoundException.class,
				() -> agitService.getAgit(agitUuid, UUID.randomUUID())
		);
		verify(refreshAgitReadModelUseCase).refresh(agitUuid);
	}

	@Test
	void getAgit_throwsWhenNotActiveMember() {
		UUID agitUuid = UUID.randomUUID();
		UUID hostUuid = UUID.randomUUID();
		AgitReadSnapshot snapshot = snapshot(agitUuid, hostUuid, AgitStatus.ACTIVE);
		when(agitReadPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(snapshot));

		assertThrows(
				AgitMemberNotActiveException.class,
				() -> agitService.getAgit(agitUuid, UUID.randomUUID())
		);
		verify(refreshAgitReadModelUseCase, never()).refresh(agitUuid);
	}

	@Test
	void getAgit_throwsWhenDeleted() {
		UUID agitUuid = UUID.randomUUID();
		UUID hostUuid = UUID.randomUUID();
		AgitReadSnapshot snapshot = snapshot(agitUuid, hostUuid, AgitStatus.DELETED);
		when(agitReadPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(snapshot));

		assertThrows(AgitNotFoundException.class, () -> agitService.getAgit(agitUuid, hostUuid));
	}

	private AgitReadSnapshot snapshot(UUID agitUuid, UUID hostUuid, AgitStatus status) {
		return new AgitReadSnapshot(
				agitUuid,
				"주말 보드게임",
				"가볍게 즐겨요",
				null,
				"A1B2C3",
				status,
				5,
				List.of(new AgitReadMemberSnapshot(hostUuid, "보드왕", null, AgitMemberRole.HOST)),
				List.of(new AgitReadTopicSnapshot("topic-1", Instant.parse("2026-08-14T00:00:00Z"))),
				Instant.parse("2026-08-14T00:00:00Z")
		);
	}
}
