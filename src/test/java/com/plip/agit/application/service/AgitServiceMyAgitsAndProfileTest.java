package com.plip.agit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.agit.application.exception.AgitMemberNotActiveException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.port.in.RefreshAgitReadModelUseCase;
import com.plip.agit.application.port.in.dto.MyAgitItemDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileRequestDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileResultDto;
import com.plip.agit.application.port.out.ActiveMembershipAgit;
import com.plip.agit.application.port.out.AgitBanPersistencePort;
import com.plip.agit.application.port.out.AgitEventTopics;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.EventPublisherPort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.domain.model.AgitStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgitServiceMyAgitsAndProfileTest {

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

	private UUID userUuid;
	private UUID agitUuid;

	@BeforeEach
	void setUp() {
		userUuid = UUID.randomUUID();
		agitUuid = UUID.randomUUID();
	}

	@Test
	void listMyAgits_returnsMappedItems() {
		when(agitReadPersistencePort.findActiveByMemberUserUuid(userUuid))
				.thenReturn(List.of(new ActiveMembershipAgit(agitUuid, "주말 보드게임")));

		List<MyAgitItemDto> result = agitService.listMyAgits(userUuid);

		assertEquals(1, result.size());
		assertEquals(agitUuid, result.get(0).getAgitUuid());
		assertEquals("주말 보드게임", result.get(0).getAgitName());
		verifyNoInteractions(agitMemberProfilePersistencePort);
	}

	@Test
	void listMyAgits_requiresUserUuid() {
		assertThrows(IllegalArgumentException.class, () -> agitService.listMyAgits(null));
	}

	@Test
	void updateMyMemberProfile_updatesNickname() {
		Agit agit = Agit.reconstitute(
				10L, agitUuid, "주말 보드게임", "소개", 5, "AB12CD", AgitStatus.ACTIVE, null
		);
		AgitMemberProfile profile = AgitMemberProfile.reconstitute(
				1L, 10L, userUuid, "보드왕", "profiles/old.png",
				AgitMemberStatus.ACTIVE, AgitMemberRole.GUEST, null
		);
		AgitMemberProfile saved = AgitMemberProfile.reconstitute(
				1L, 10L, userUuid, "새닉네임", "profiles/old.png",
				AgitMemberStatus.ACTIVE, AgitMemberRole.GUEST, null
		);

		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(10L, userUuid))
				.thenReturn(Optional.of(profile));
		when(agitMemberProfilePersistencePort.save(any(AgitMemberProfile.class))).thenReturn(saved);

		UpdateMyMemberProfileResultDto result = agitService.updateMyMemberProfile(
				agitUuid,
				UpdateMyMemberProfileRequestDto.builder()
						.userUuid(userUuid)
						.nickname("새닉네임")
						.build()
		);

		assertEquals("새닉네임", result.getNickname());
		assertEquals("profiles/old.png", result.getProfileImagePath());
		verify(agitMemberProfilePersistencePort).save(any(AgitMemberProfile.class));
		verify(eventPublisherPort).publish(
				eq(AgitEventTopics.MEMBER_PROFILE_UPDATED),
				eq(agitUuid.toString()),
				anyString()
		);
		verify(refreshAgitReadModelUseCase).refresh(agitUuid);
	}

	@Test
	void updateMyMemberProfile_keepsWriteWhenRefreshFails() {
		Agit agit = Agit.reconstitute(
				10L, agitUuid, "주말 보드게임", "소개", 5, "AB12CD", AgitStatus.ACTIVE, null
		);
		AgitMemberProfile profile = AgitMemberProfile.reconstitute(
				1L, 10L, userUuid, "보드왕", "profiles/old.png",
				AgitMemberStatus.ACTIVE, AgitMemberRole.GUEST, null
		);
		AgitMemberProfile saved = AgitMemberProfile.reconstitute(
				1L, 10L, userUuid, "새닉네임", "profiles/old.png",
				AgitMemberStatus.ACTIVE, AgitMemberRole.GUEST, null
		);

		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(10L, userUuid))
				.thenReturn(Optional.of(profile));
		when(agitMemberProfilePersistencePort.save(any(AgitMemberProfile.class))).thenReturn(saved);
		doThrow(new RuntimeException("mongo down")).when(refreshAgitReadModelUseCase).refresh(agitUuid);

		UpdateMyMemberProfileResultDto result = agitService.updateMyMemberProfile(
				agitUuid,
				UpdateMyMemberProfileRequestDto.builder()
						.userUuid(userUuid)
						.nickname("새닉네임")
						.build()
		);

		assertEquals("새닉네임", result.getNickname());
		verify(agitMemberProfilePersistencePort).save(any(AgitMemberProfile.class));
	}

	@Test
	void updateMyMemberProfile_rejectsWhenNotActive() {
		Agit agit = Agit.reconstitute(
				10L, agitUuid, "주말 보드게임", "소개", 5, "AB12CD", AgitStatus.ACTIVE, null
		);
		AgitMemberProfile profile = AgitMemberProfile.reconstitute(
				1L, 10L, userUuid, "보드왕", null,
				AgitMemberStatus.LEFT, AgitMemberRole.GUEST, null
		);

		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(10L, userUuid))
				.thenReturn(Optional.of(profile));

		assertThrows(
				AgitMemberNotActiveException.class,
				() -> agitService.updateMyMemberProfile(
						agitUuid,
						UpdateMyMemberProfileRequestDto.builder()
								.userUuid(userUuid)
								.nickname("새닉네임")
								.build()
				)
		);
		verify(refreshAgitReadModelUseCase, never()).refresh(any());
	}

	@Test
	void updateMyMemberProfile_rejectsWhenMemberMissing() {
		Agit agit = Agit.reconstitute(
				10L, agitUuid, "주말 보드게임", "소개", 5, "AB12CD", AgitStatus.ACTIVE, null
		);

		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));
		when(agitMemberProfilePersistencePort.findByAgitIdAndUserUuid(10L, userUuid))
				.thenReturn(Optional.empty());

		assertThrows(
				AgitMemberNotFoundException.class,
				() -> agitService.updateMyMemberProfile(
						agitUuid,
						UpdateMyMemberProfileRequestDto.builder()
								.userUuid(userUuid)
								.nickname("새닉네임")
								.build()
				)
		);
		verify(refreshAgitReadModelUseCase, never()).refresh(any());
	}
}
