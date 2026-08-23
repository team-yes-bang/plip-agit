package com.plip.agit.application.port.in;

import com.plip.agit.application.port.in.dto.AgitCachedMemberDto;
import java.util.List;
import java.util.UUID;

public interface ReloadAgitMembershipCacheUseCase {

	List<AgitCachedMemberDto> reload(UUID agitUuid);
}
