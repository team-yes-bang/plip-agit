package com.plip.agit.adapter.in.web.mapper;

import com.plip.agit.adapter.in.web.dto.CreateAgitRequest;
import com.plip.agit.adapter.in.web.dto.CreateAgitResponse;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
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
}
