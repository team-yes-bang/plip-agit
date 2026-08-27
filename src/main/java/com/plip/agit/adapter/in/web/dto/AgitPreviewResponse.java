package com.plip.agit.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "아지트 미리보기 (초대코드 없음)")
public class AgitPreviewResponse {

	private UUID agitUuid;
	private String agitName;
	private String description;
	private long currentMemberCount;
	private int maximumCapacity;
	private String hostNickname;
	private String thumbnailPath;
	private String myStatus;
}
