package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.ActorUserRequest;
import com.plip.agit.adapter.in.web.dto.AgitLandingResponse;
import com.plip.agit.adapter.in.web.dto.CreateAgitRequest;
import com.plip.agit.adapter.in.web.dto.CreateAgitResponse;
import com.plip.agit.adapter.in.web.dto.JoinAgitRequest;
import com.plip.agit.adapter.in.web.dto.JoinAgitResponse;
import com.plip.agit.adapter.in.web.dto.MyAgitItemResponse;
import com.plip.agit.adapter.in.web.dto.ReissueInviteCodeResponse;
import com.plip.agit.adapter.in.web.dto.UpdateAgitRequest;
import com.plip.agit.adapter.in.web.dto.UpdateAgitResponse;
import com.plip.agit.adapter.in.web.dto.UpdateMyMemberProfileRequest;
import com.plip.agit.adapter.in.web.dto.UpdateMyMemberProfileResponse;
import com.plip.agit.adapter.in.web.mapper.AgitWebMapper;
import com.plip.agit.application.port.in.AgitUseCase;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agit", description = "아지트 API")
@RequestMapping("/api/v1/agits")
@RestController
@RequiredArgsConstructor
public class AgitController {

	private final AgitUseCase agitUseCase;
	private final AgitWebMapper agitWebMapper;

	@Operation(
			summary = "아지트 생성",
			description = "아지트와 방장 프로필을 함께 생성하고 초대 코드를 발급합니다."
	)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CreateAgitResponse createAgit(@RequestBody CreateAgitRequest request) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		CreateAgitRequestDto requestDto = agitWebMapper.toDto(request);
		CreateAgitResultDto resultDto = agitUseCase.createAgit(requestDto);
		return agitWebMapper.toResponse(resultDto);
	}

	@Operation(
			summary = "내 아지트 목록 조회",
			description = "접속 유저가 ACTIVE로 속한 아지트 목록(제목·UUID)을 반환합니다. "
					+ "정렬은 agit.updated_at 내림차순(임시)이며, 이후 최근 토픽순으로 교체 예정입니다. "
					+ "userUuid는 임시로 request body로 받습니다."
	)
	@GetMapping("/me")
	public List<MyAgitItemResponse> listMyAgits(@RequestBody ActorUserRequest request) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		List<MyAgitItemDto> resultDtos = agitUseCase.listMyAgits(request.getUserUuid());
		return agitWebMapper.toMyAgitResponses(resultDtos);
	}

	@Operation(
			summary = "아지트 랜딩 조회",
			description = "초대 코드로 ACTIVE 아지트의 랜딩 표시 정보를 조회합니다. 인증 없이 호출 가능합니다."
	)
	@GetMapping("/{code}/landing")
	public AgitLandingResponse getLandingByCode(@PathVariable String code) {
		AgitLandingResultDto resultDto = agitUseCase.getLandingByCode(code);
		return agitWebMapper.toLandingResponse(resultDto);
	}

	@Operation(
			summary = "아지트 입장",
			description = "초대 코드로 아지트에 입장합니다. 신규는 GUEST 프로필을 생성하고, LEFT는 닉네임·이미지를 갱신한 뒤 ACTIVE로 전환합니다. 이미 ACTIVE면 409, BANNED면 403, 정원 초과면 409를 반환합니다."
	)
	@PostMapping("/{code}/join")
	@ResponseStatus(HttpStatus.CREATED)
	public JoinAgitResponse joinAgit(
			@PathVariable String code,
			@RequestBody JoinAgitRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		JoinAgitRequestDto requestDto = agitWebMapper.toJoinDto(request);
		JoinAgitResultDto resultDto = agitUseCase.joinAgit(code, requestDto);
		return agitWebMapper.toJoinResponse(resultDto);
	}

	@Operation(
			summary = "아지트 정보 변경",
			description = "아지트장이 제목·인원·소개글·섬네일을 수정합니다. 변경 인원수는 현재 ACTIVE 인원보다 작을 수 없으며, 그 외 검증은 생성과 동일합니다."
	)
	@PatchMapping("/{agitUuid}")
	public UpdateAgitResponse updateAgit(
			@PathVariable UUID agitUuid,
			@RequestBody UpdateAgitRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		UpdateAgitRequestDto requestDto = agitWebMapper.toUpdateDto(request);
		UpdateAgitResultDto resultDto = agitUseCase.updateAgit(agitUuid, requestDto);
		return agitWebMapper.toUpdateResponse(resultDto);
	}

	@Operation(
			summary = "내 아지트 프로필 수정",
			description = "접속 유저가 해당 아지트에서 ACTIVE인 본인 닉네임·프로필 이미지를 부분 수정합니다. "
					+ "미전달 필드는 유지합니다."
	)
	@PatchMapping("/{agitUuid}/members/me")
	public UpdateMyMemberProfileResponse updateMyMemberProfile(
			@PathVariable UUID agitUuid,
			@RequestBody UpdateMyMemberProfileRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		UpdateMyMemberProfileRequestDto requestDto = agitWebMapper.toUpdateMyProfileDto(request);
		UpdateMyMemberProfileResultDto resultDto =
				agitUseCase.updateMyMemberProfile(agitUuid, requestDto);
		return agitWebMapper.toUpdateMyProfileResponse(resultDto);
	}

	@Operation(
			summary = "아지트장 위임",
			description = "아지트장이 ampId의 ACTIVE 게스트에게 방장 권한을 위임합니다. 성공 시 204를 반환합니다."
	)
	@PostMapping("/{agitUuid}/members/{ampId}/transfer-host")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void transferHost(
			@PathVariable UUID agitUuid,
			@PathVariable Long ampId,
			@RequestBody ActorUserRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		agitUseCase.transferHost(agitUuid, ampId, request.getUserUuid());
	}

	@Operation(
			summary = "초대 코드 재발급",
			description = "아지트장이 초대 코드를 재발급합니다. 호출마다 새 코드를 반환합니다. 연타 방지는 FE에서 처리합니다."
	)
	@PostMapping("/{agitUuid}/invite-code")
	public ReissueInviteCodeResponse reissueInviteCode(
			@PathVariable UUID agitUuid,
			@RequestBody ActorUserRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		ReissueInviteCodeResultDto resultDto = agitUseCase.reissueInviteCode(agitUuid, request.getUserUuid());
		return agitWebMapper.toReissueResponse(resultDto);
	}

	@Operation(
			summary = "아지트에서 내보내기",
			description = "아지트장이 ampId로 멤버를 내보냅니다. status를 BANNED로 바꾸고 bans 이력을 남깁니다. ACTIVE HOST는 내보낼 수 없습니다. 이미 BANNED인 경우 변경 없이 성공합니다."
	)
	@PostMapping("/{agitUuid}/members/{ampId}/ban")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void banMember(
			@PathVariable UUID agitUuid,
			@PathVariable Long ampId,
			@RequestBody ActorUserRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		agitUseCase.banMember(agitUuid, ampId, request.getUserUuid());
	}

	@Operation(
			summary = "아지트 나가기",
			description = "본인이 아지트에서 나갑니다. GUEST는 status를 LEFT로 변경합니다. HOST는 ACTIVE 인원이 본인뿐일 때만 나갈 수 있으며, 이때 아지트는 소프트 삭제됩니다. 이미 LEFT이거나 BANNED인 경우 변경 없이 성공합니다."
	)
	@PostMapping("/{agitUuid}/leave")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void leaveAgit(
			@PathVariable UUID agitUuid,
			@RequestBody ActorUserRequest request
	) {
		// TODO: userUuid는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		agitUseCase.leaveAgit(agitUuid, request.getUserUuid());
	}

	@Operation(
			summary = "아지트 밴 해제",
			description = "아지트장이 userUuid 기준으로 밴을 해제합니다. BANNED인 멤버는 status를 LEFT로 바꾸고 bans 이력에 해제 시각을 기록합니다. 이미 LEFT인 경우 변경 없이 성공합니다."
	)
	@PostMapping("/{agitUuid}/members/{userUuid}/unban")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unbanMember(
			@PathVariable UUID agitUuid,
			@PathVariable UUID userUuid,
			@RequestBody ActorUserRequest request
	) {
		// TODO: userUuid(actor)는 현재 request body로 수신. 인증 연동 후 Gateway/JWT에서 추출하도록 교체한다.
		agitUseCase.unbanMember(agitUuid, userUuid, request.getUserUuid());
	}
}
