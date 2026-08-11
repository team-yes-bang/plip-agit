package com.plip.agit.application.port.in;

import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import java.util.UUID;

public interface AgitUseCase {

	CreateAgitResultDto createAgit(CreateAgitRequestDto requestDto);

	AgitLandingResultDto getLandingByCode(String code);

	void banMember(UUID agitUuid, Long ampId, UUID actorUserUuid);

	void leaveAgit(UUID agitUuid, UUID actorUserUuid);
}
