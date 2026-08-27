package com.plip.agit.application.port.in.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitPreviewResultDto {

	private final UUID agitUuid;
	private final String agitName;
	private final String description;
	private final long currentMemberCount;
	private final int maximumCapacity;
	private final String hostNickname;
	private final String thumbnailPath;
	private final String myStatus;
}
