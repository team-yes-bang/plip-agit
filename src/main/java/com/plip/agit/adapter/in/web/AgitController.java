package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.CreateAgitRequest;
import com.plip.agit.adapter.in.web.dto.CreateAgitResponse;
import com.plip.agit.adapter.in.web.mapper.AgitWebMapper;
import com.plip.agit.application.port.in.AgitUseCase;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
