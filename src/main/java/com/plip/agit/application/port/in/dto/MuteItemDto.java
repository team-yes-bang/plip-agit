package com.plip.agit.application.port.in.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MuteItemDto {

	private UUID mutedUuid;
}
