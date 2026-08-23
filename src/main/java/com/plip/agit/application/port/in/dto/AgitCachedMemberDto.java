package com.plip.agit.application.port.in.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitCachedMemberDto {

	private final UUID userUuid;
	private final AgitMemberRole role;
}
