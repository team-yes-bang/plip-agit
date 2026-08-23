package com.plip.agit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.port.in.dto.AgitCachedMemberDto;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitMembershipCachePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgitMembershipCacheServiceTest {

	@Mock
	private AgitPersistencePort agitPersistencePort;

	@Mock
	private AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;

	@Mock
	private AgitMembershipCachePort agitMembershipCachePort;

	@InjectMocks
	private AgitMembershipCacheService agitMembershipCacheService;

	@Test
	void reload_writesActiveMembersToCache() {
		UUID agitUuid = UUID.randomUUID();
		UUID hostUuid = UUID.randomUUID();
		Agit agit = org.mockito.Mockito.mock(Agit.class);
		when(agit.getId()).thenReturn(1L);
		when(agit.getStatus()).thenReturn(AgitStatus.ACTIVE);
		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));

		AgitMemberProfile host = org.mockito.Mockito.mock(AgitMemberProfile.class);
		when(host.getUserUuid()).thenReturn(hostUuid);
		when(host.getRole()).thenReturn(AgitMemberRole.HOST);
		when(agitMemberProfilePersistencePort.findActiveByAgitId(1L)).thenReturn(List.of(host));

		List<AgitCachedMemberDto> result = agitMembershipCacheService.reload(agitUuid);

		assertEquals(1, result.size());
		assertEquals(hostUuid, result.get(0).getUserUuid());
		assertEquals(AgitMemberRole.HOST, result.get(0).getRole());
		verify(agitMembershipCachePort).replaceAll(agitUuid, Map.of(hostUuid, AgitMemberRole.HOST));
	}

	@Test
	void reload_deletesCacheWhenAgitDeleted() {
		UUID agitUuid = UUID.randomUUID();
		Agit agit = org.mockito.Mockito.mock(Agit.class);
		when(agit.getStatus()).thenReturn(AgitStatus.DELETED);
		when(agitPersistencePort.findByAgitUuid(agitUuid)).thenReturn(Optional.of(agit));

		assertThrows(AgitNotFoundException.class, () -> agitMembershipCacheService.reload(agitUuid));
		verify(agitMembershipCachePort).deleteAll(agitUuid);
	}
}
