package com.plip.agit.application.port.in;

import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.in.dto.JoinAgitRequestDto;
import com.plip.agit.application.port.in.dto.JoinAgitResultDto;
import com.plip.agit.application.port.in.dto.MyAgitItemDto;
import com.plip.agit.application.port.in.dto.ReissueInviteCodeResultDto;
import com.plip.agit.application.port.in.dto.UpdateAgitRequestDto;
import com.plip.agit.application.port.in.dto.UpdateAgitResultDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileRequestDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileResultDto;
import java.util.List;
import java.util.UUID;

public interface AgitUseCase {

	CreateAgitResultDto createAgit(CreateAgitRequestDto requestDto);

	AgitLandingResultDto getLandingByCode(String code);

	JoinAgitResultDto joinAgit(String code, JoinAgitRequestDto requestDto);

	void banMember(UUID agitUuid, Long ampId, UUID actorUserUuid);

	void leaveAgit(UUID agitUuid, UUID actorUserUuid);

	void unbanMember(UUID agitUuid, UUID targetUserUuid, UUID actorUserUuid);

	UpdateAgitResultDto updateAgit(UUID agitUuid, UpdateAgitRequestDto requestDto);

	void transferHost(UUID agitUuid, Long ampId, UUID actorUserUuid);

	ReissueInviteCodeResultDto reissueInviteCode(UUID agitUuid, UUID actorUserUuid);

	List<MyAgitItemDto> listMyAgits(UUID userUuid);

	UpdateMyMemberProfileResultDto updateMyMemberProfile(
			UUID agitUuid,
			UpdateMyMemberProfileRequestDto requestDto
	);
}
