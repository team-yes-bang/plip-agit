package com.plip.agit.adapter.in.web.mapper;

import com.plip.agit.adapter.in.web.dto.AgitLandingResponse;
import com.plip.agit.adapter.in.web.dto.CreateAgitRequest;
import com.plip.agit.adapter.in.web.dto.CreateAgitResponse;
import com.plip.agit.adapter.in.web.dto.JoinAgitRequest;
import com.plip.agit.adapter.in.web.dto.JoinAgitResponse;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.in.dto.JoinAgitRequestDto;
import com.plip.agit.application.port.in.dto.JoinAgitResultDto;
import org.springframework.stereotype.Component;

@Component
public class AgitWebMapper {

	public CreateAgitRequestDto toDto(CreateAgitRequest request) {
		return CreateAgitRequestDto.builder()
				.agitName(request.getAgitName())
				.description(request.getDescription())
				.maximumCapacity(request.getMaximumCapacity())
				.thumbnailPath(request.getThumbnailPath())
				.userUuid(request.getUserUuid())
				.nickname(request.getNickname())
				.profileImagePath(request.getProfileImagePath())
				.build();
	}

	public CreateAgitResponse toResponse(CreateAgitResultDto resultDto) {
		return CreateAgitResponse.builder()
				.agitUuid(resultDto.getAgitUuid())
				.agitName(resultDto.getAgitName())
				.description(resultDto.getDescription())
				.maximumCapacity(resultDto.getMaximumCapacity())
				.code(resultDto.getCode())
				.thumbnailPath(resultDto.getThumbnailPath())
				.ampId(resultDto.getAmpId())
				.nickname(resultDto.getNickname())
				.profileImagePath(resultDto.getProfileImagePath())
				.role(resultDto.getRole())
				.build();
	}

	public AgitLandingResponse toLandingResponse(AgitLandingResultDto resultDto) {
		return AgitLandingResponse.builder()
				.agitName(resultDto.getAgitName())
				.description(resultDto.getDescription())
				.currentMemberCount(resultDto.getCurrentMemberCount())
				.maximumCapacity(resultDto.getMaximumCapacity())
				.hostNickname(resultDto.getHostNickname())
				.thumbnailPath(resultDto.getThumbnailPath())
				.build();
	}

	public JoinAgitRequestDto toJoinDto(JoinAgitRequest request) {
		return JoinAgitRequestDto.builder()
				.userUuid(request.getUserUuid())
				.nickname(request.getNickname())
				.profileImagePath(request.getProfileImagePath())
				.build();
	}

	public JoinAgitResponse toJoinResponse(JoinAgitResultDto resultDto) {
		return JoinAgitResponse.builder()
				.agitUuid(resultDto.getAgitUuid())
				.ampId(resultDto.getAmpId())
				.nickname(resultDto.getNickname())
				.profileImagePath(resultDto.getProfileImagePath())
				.role(resultDto.getRole())
				.build();
	}
}
