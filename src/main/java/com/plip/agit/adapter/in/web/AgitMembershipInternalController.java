package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.AgitCachedMemberResponse;
import com.plip.agit.application.port.in.ReloadAgitMembershipCacheUseCase;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/internal/v1/agits")
@RequiredArgsConstructor
public class AgitMembershipInternalController {

	private final ReloadAgitMembershipCacheUseCase reloadAgitMembershipCacheUseCase;

	@GetMapping("/{agitUuid}/members")
	public List<AgitCachedMemberResponse> reloadMembers(@PathVariable UUID agitUuid) {
		return reloadAgitMembershipCacheUseCase.reload(agitUuid).stream()
				.map(item -> AgitCachedMemberResponse.builder()
						.userUuid(item.getUserUuid())
						.role(item.getRole())
						.build())
				.toList();
	}
}
