package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.MuteItemResponse;
import com.plip.agit.adapter.in.web.mapper.AgitMuteWebMapper;
import com.plip.agit.application.port.in.AgitMuteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agit Mute", description = "아지트 내 유저 뮤트 API")
@RequestMapping("/api/v1/agits")
@RestController
@RequiredArgsConstructor
public class AgitMuteController {

	private final AgitMuteUseCase agitMuteUseCase;
	private final AgitMuteWebMapper agitMuteWebMapper;

	@Operation(
			summary = "아지트 내 유저 뮤트",
			description = "해당 아지트의 ACTIVE 멤버가 다른 ACTIVE 멤버를 뮤트합니다. 이미 뮤트된 경우 변경 없이 성공합니다. 액터는 Access JWT에서 추출합니다."
	)
	@PostMapping("/{agitUuid}/mutes/{mutedUuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void muteMember(
			@PathVariable UUID agitUuid,
			@PathVariable UUID mutedUuid
	) {
		agitMuteUseCase.muteMember(agitUuid, mutedUuid, AuthenticatedActor.requireUserUuid());
	}

	@Operation(
			summary = "아지트 내 유저 뮤트 해제",
			description = "본인이 건 뮤트를 해제합니다. 뮤트 관계가 없으면 변경 없이 성공합니다. 액터는 Access JWT에서 추출합니다."
	)
	@DeleteMapping("/{agitUuid}/mutes/{mutedUuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unmuteMember(
			@PathVariable UUID agitUuid,
			@PathVariable UUID mutedUuid
	) {
		agitMuteUseCase.unmuteMember(agitUuid, mutedUuid, AuthenticatedActor.requireUserUuid());
	}

	@Operation(
			summary = "내 뮤트 목록 조회",
			description = "해당 아지트에서 내가 뮤트한 사용자 UUID 목록을 반환합니다. 액터는 Access JWT에서 추출합니다."
	)
	@GetMapping("/{agitUuid}/mutes")
	public List<MuteItemResponse> listMyMutes(@PathVariable UUID agitUuid) {
		return agitMuteWebMapper.toResponses(
				agitMuteUseCase.listMyMutes(agitUuid, AuthenticatedActor.requireUserUuid()));
	}
}
