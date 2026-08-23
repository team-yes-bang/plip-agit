package com.plip.agit.adapter.in.web.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(hidden = true)
public class AgitCachedMemberResponse {

	private UUID userUuid;
	private AgitMemberRole role;
}
