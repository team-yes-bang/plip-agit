package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.ActorUserRequest;
import com.plip.agit.adapter.in.web.dto.AgitLandingResponse;
import com.plip.agit.adapter.in.web.dto.CreateAgitRequest;
import com.plip.agit.adapter.in.web.dto.CreateAgitResponse;
import com.plip.agit.adapter.in.web.mapper.AgitWebMapper;
import com.plip.agit.application.port.in.AgitUseCase;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
			summary = "아지트 랜딩 조회",
			description = "초대 코드로 ACTIVE 아지트의 랜딩 표시 정보를 조회합니다. 인증 없이 호출 가능합니다."
	)
	@GetMapping("/codes/{code}")
	public AgitLandingResponse getLandingByCode(@PathVariable String code) {
		AgitLandingResultDto resultDto = agitUseCase.getLandingByCode(code);
		return agitWebMapper.toLandingResponse(resultDto);
	}

	@Operation(
			summary = "아지트에서 내보내기",
			description = "아지트장이 ampId로 멤버를 내보냅니다. status를 BANNED로 바꾸고 bans 이력을 남깁니다. 이미 BANNED인 경우 변경 없이 성공합니다."
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
}
