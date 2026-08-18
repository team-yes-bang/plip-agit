package com.plip.agit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plip.agit.application.exception.AgitMemberNotActiveException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.exception.CannotMuteSelfException;
import com.plip.agit.application.port.in.dto.MuteItemDto;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitMutePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.domain.model.AgitMute;
import com.plip.agit.domain.model.AgitStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgitMuteServiceTest {

	@Mock
	private AgitPersistencePort agitPersistencePort;

	@Mock
	private AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;

	@Mock
	private AgitMutePersistencePort agitMutePersistencePort;

	@InjectMocks
	private AgitMuteService agitMuteService;

	private UUID agitUuid;
	private UUID actorUuid;
	private UUID mutedUuid;
	private Agit agit;

	@BeforeEach
	void setUp() {
		agitUuid = UUID.randomUUID();
		actorUuid = UUID.randomUUID();
		mutedUuid = UUID.randomUUID();
		agit = Agit.reconstitute(1L, agitUuid, "아지트", "소개", 5, "AB12CD", AgitStatus.ACTIVE, null);
	}

	@Test
	void muteMember_savesWhenBothActive() {
		stubActiveAgit();
		stubActiveMember(actorUuid, 10L);
		stubActiveMember(mutedUuid, 11L);
		when(agitMutePersistencePort.findByAgitIdAndMuterUuidAndMutedUuid(1L, actorUuid, mutedUuid))
				.thenReturn(Optional.empty());

		agitMuteService.muteMember(agitUuid, mutedUuid, actorUuid);

		verify(agitMutePersistencePort).save(any(AgitMute.class));
	}

	@Test
	void muteMember_isIdempotentWhenAlreadyMuted() {
		stubActiveAgit();
		stubActiveMember(actorUuid, 10L);
		stubActiveMember(mutedUuid, 11L);
		when(agitMutePersistencePort.findByAgitIdAndMuterUuidAndMutedUuid(1L, actorUuid, mutedUuid))
				.thenReturn(Optional.of(AgitMute.reconstitute(1L, 1L, actorUuid, mutedUuid)));

		agitMuteService.muteMember(agitUuid, mutedUuid, actorUuid);

		verify(agitMutePersistencePort, never()).save(any(AgitMute.class));
	}

	@Test
	void muteMember_rejectsSelfMute() {
		assertThrows(
				CannotMuteSelfException.class,
				() -> agitMuteService.muteMember(agitUuid, actorUuid, actorUuid)
		);
		verify(agitMutePersistencePort, never()).save(any(AgitMute.class));
	}

	@Test
	void muteMember_rejectsInactiveTarget() {
		stubActiveAgit();
		stubActiveMember(actorUuid, 10L);
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(1L, mutedUuid))
				.thenReturn(Optional.of(profile(11L, mutedUuid, AgitMemberStatus.LEFT)));

		assertThrows(
				AgitMemberNotActiveException.class,
				() -> agitMuteService.muteMember(agitUuid, mutedUuid, actorUuid)
		);
	}

	@Test
	void muteMember_rejectsUnknownMember() {
		stubActiveAgit();
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(1L, actorUuid))
				.thenReturn(Optional.empty());

		assertThrows(
				AgitMemberNotFoundException.class,
				() -> agitMuteService.muteMember(agitUuid, mutedUuid, actorUuid)
		);
	}

	@Test
	void unmuteMember_deletesExistingMute() {
		stubActiveAgit();
		stubActiveMember(actorUuid, 10L);
		AgitMute existing = AgitMute.reconstitute(7L, 1L, actorUuid, mutedUuid);
		when(agitMutePersistencePort.findByAgitIdAndMuterUuidAndMutedUuid(1L, actorUuid, mutedUuid))
				.thenReturn(Optional.of(existing));

		agitMuteService.unmuteMember(agitUuid, mutedUuid, actorUuid);

		verify(agitMutePersistencePort).delete(existing);
	}

	@Test
	void unmuteMember_isIdempotentWhenMissing() {
		stubActiveAgit();
		stubActiveMember(actorUuid, 10L);
		when(agitMutePersistencePort.findByAgitIdAndMuterUuidAndMutedUuid(1L, actorUuid, mutedUuid))
				.thenReturn(Optional.empty());

		agitMuteService.unmuteMember(agitUuid, mutedUuid, actorUuid);

		verify(agitMutePersistencePort, never()).delete(any(AgitMute.class));
	}

	@Test
	void listMyMutes_returnsMutedUuids() {
		stubActiveAgit();
		stubActiveMember(actorUuid, 10L);
		when(agitMutePersistencePort.findAllByAgitIdAndMuterUuid(1L, actorUuid))
				.thenReturn(List.of(AgitMute.reconstitute(1L, 1L, actorUuid, mutedUuid)));

		List<MuteItemDto> result = agitMuteService.listMyMutes(agitUuid, actorUuid);

		assertEquals(1, result.size());
		assertEquals(mutedUuid, result.get(0).getMutedUuid());
	}

	private void stubActiveAgit() {
		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));
	}

	private void stubActiveMember(UUID userUuid, Long ampId) {
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(1L, userUuid))
				.thenReturn(Optional.of(profile(ampId, userUuid, AgitMemberStatus.ACTIVE)));
	}

	private AgitMemberProfile profile(Long id, UUID userUuid, AgitMemberStatus status) {
		return AgitMemberProfile.reconstitute(
				id,
				1L,
				userUuid,
				"닉네임12",
				null,
				status,
				AgitMemberRole.GUEST,
				null
		);
	}
}
