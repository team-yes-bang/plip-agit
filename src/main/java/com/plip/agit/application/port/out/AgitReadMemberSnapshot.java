package com.plip.agit.application.port.out;

import com.plip.agit.domain.model.AgitMemberRole;
import java.util.UUID;

public record AgitReadMemberSnapshot(
		Long ampId,
		UUID userUuid,
		String nickname,
		String profileImagePath,
		AgitMemberRole role
) {
}
