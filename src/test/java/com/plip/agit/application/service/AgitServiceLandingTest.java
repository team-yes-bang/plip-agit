package com.plip.agit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.port.in.RefreshAgitReadModelUseCase;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.out.AgitBanPersistencePort;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.port.out.AgitReadMemberSnapshot;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.application.port.out.EventPublisherPort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
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
class AgitServiceLandingTest {

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
	void getLandingByCode_usesMongoWhenHit() {
		UUID agitUuid = UUID.randomUUID();
		UUID hostUuid = UUID.randomUUID();
		AgitReadSnapshot snapshot = new AgitReadSnapshot(
				agitUuid,
				"주말 보드게임",
				"가볍게 즐겨요",
				null,
				"A1B2C3",
				AgitStatus.ACTIVE,
				5,
				List.of(
						new AgitReadMemberSnapshot(10L, hostUuid, "보드왕", null, AgitMemberRole.HOST),
						new AgitReadMemberSnapshot(11L, UUID.randomUUID(), "게스트", null, AgitMemberRole.GUEST)
				),
				List.of(),
				Instant.parse("2026-08-14T00:00:00Z")
		);
		when(agitReadPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.of(snapshot));

		AgitLandingResultDto result = agitService.getLandingByCode("a1b2c3");

		assertEquals("주말 보드게임", result.getAgitName());
		assertEquals("가볍게 즐겨요", result.getDescription());
		assertEquals(2, result.getCurrentMemberCount());
		assertEquals(5, result.getMaximumCapacity());
		assertEquals("보드왕", result.getHostNickname());
		verify(agitPersistencePort, never()).findActiveByCode("A1B2C3");
		verify(refreshAgitReadModelUseCase, never()).refresh(any());
	}

	@Test
	void getLandingByCode_fallsBackToMysqlOnMiss() {
		UUID agitUuid = UUID.randomUUID();
		Agit agit = Agit.reconstitute(
				1L, agitUuid, "주말 보드게임", "소개", 5, "A1B2C3", AgitStatus.ACTIVE, null
		);
		AgitMemberProfile host = AgitMemberProfile.reconstitute(
				10L, 1L, UUID.randomUUID(), "보드왕", null,
				com.plip.agit.domain.model.AgitMemberStatus.ACTIVE, AgitMemberRole.HOST, null
		);
		when(agitReadPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.empty());
		when(agitPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.of(agit));
		when(agitMemberProfilePersistencePort.findActiveHostByAgitId(1L)).thenReturn(Optional.of(host));
		when(agitMemberProfilePersistencePort.countActiveByAgitId(1L)).thenReturn(1L);

		AgitLandingResultDto result = agitService.getLandingByCode("A1B2C3");

		assertEquals("주말 보드게임", result.getAgitName());
		assertEquals("보드왕", result.getHostNickname());
		assertEquals(1, result.getCurrentMemberCount());
		verify(refreshAgitReadModelUseCase).refresh(agitUuid);
	}

	@Test
	void getLandingByCode_throwsWhenMissingInMongoAndMysql() {
		when(agitReadPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.empty());
		when(agitPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.empty());

		assertThrows(AgitNotFoundException.class, () -> agitService.getLandingByCode("A1B2C3"));
	}

	@Test
	void getLandingByCode_fallsBackWhenMongoHasNoHost() {
		AgitReadSnapshot snapshot = new AgitReadSnapshot(
				UUID.randomUUID(),
				"주말 보드게임",
				"",
				null,
				"A1B2C3",
				AgitStatus.ACTIVE,
				5,
				List.of(new AgitReadMemberSnapshot(
						11L, UUID.randomUUID(), "게스트", null, AgitMemberRole.GUEST
				)),
				List.of(),
				Instant.parse("2026-08-14T00:00:00Z")
		);
		when(agitReadPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.of(snapshot));
		when(agitPersistencePort.findActiveByCode("A1B2C3")).thenReturn(Optional.empty());

		assertThrows(AgitNotFoundException.class, () -> agitService.getLandingByCode("A1B2C3"));
		verify(agitPersistencePort).findActiveByCode("A1B2C3");
	}
}
