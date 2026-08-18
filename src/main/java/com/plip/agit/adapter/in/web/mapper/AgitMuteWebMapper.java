package com.plip.agit.adapter.in.web.mapper;

import com.plip.agit.adapter.in.web.dto.MuteItemResponse;
import com.plip.agit.application.port.in.dto.MuteItemDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AgitMuteWebMapper {

	public List<MuteItemResponse> toResponses(List<MuteItemDto> dtos) {
		return dtos.stream()
				.map(dto -> MuteItemResponse.builder().mutedUuid(dto.getMutedUuid()).build())
				.toList();
	}
}
