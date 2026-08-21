package com.plip.agit.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.domain.model.AgitStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgitReadModelServiceBoundTopicTest {

	@Mock
	private AgitPersistencePort agitPersistencePort;

	@Mock
	private AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;

	@Mock
	private AgitReadPersistencePort agitReadPersistencePort;

	@InjectMocks
	private AgitReadModelService agitReadModelService;

	@Test
	void refresh_doesNotReadExistingTopics() {
		UUID agitUuid = UUID.randomUUID();
		Agit agit = Agit.reconstitute(
				1L, agitUuid, "주말 보드게임", "", 5, "A1B2C3", AgitStatus.ACTIVE, null
		);
		AgitMemberProfile host = AgitMemberProfile.reconstitute(
				10L, 1L, UUID.randomUUID(), "보드왕", null,
				AgitMemberStatus.ACTIVE, AgitMemberRole.HOST, null
		);
		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));
		when(agitMemberProfilePersistencePort.findActiveByAgitId(1L)).thenReturn(List.of(host));

		agitReadModelService.refresh(agitUuid);

		ArgumentCaptor<AgitReadSnapshot> captor = ArgumentCaptor.forClass(AgitReadSnapshot.class);
		verify(agitReadPersistencePort).replace(captor.capture());
		verify(agitReadPersistencePort, never()).findByAgitUuid(any());
		AgitReadSnapshot saved = captor.getValue();
		org.junit.jupiter.api.Assertions.assertTrue(saved.topics().isEmpty());
		org.junit.jupiter.api.Assertions.assertEquals("주말 보드게임", saved.agitName());
		org.junit.jupiter.api.Assertions.assertEquals(1, saved.members().size());
	}

	@Test
	void bind_skipsWhenDocumentMissing() {
		UUID agitUuid = UUID.randomUUID();
		when(agitReadPersistencePort.upsertTopic(eq(agitUuid), any())).thenReturn(false);

		agitReadModelService.bind(agitUuid, "topic-1", null);

		verify(agitReadPersistencePort).upsertTopic(eq(agitUuid), any());
	}

	@Test
	void start_skipsWhenStartedAtMissing() {
		UUID agitUuid = UUID.randomUUID();

		agitReadModelService.start(agitUuid, "topic-1", null);

		verify(agitReadPersistencePort, never()).upsertTopic(any(), any());
	}
}
