package com.plip.agit.adapter.in.web.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "아지트 상세 조회 응답")
public class AgitDetailResponse {

	@Schema(description = "아지트 UUID", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
	private UUID agitUuid;

	@Schema(description = "아지트 제목", example = "주말 보드게임")
	private String agitName;

	@Schema(description = "소개글", example = "가볍게 즐겨요")
	private String description;

	@Schema(description = "섬네일 경로", example = "agits/thumbnails/sample.png")
	private String thumbnailPath;

	@Schema(description = "초대 코드", example = "AB12CD")
	private String code;

	@Schema(description = "아지트 상태", example = "ACTIVE")
	private AgitStatus status;

	@Schema(description = "최대 인원", example = "5")
	private int maximumCapacity;

	@Schema(description = "현재 인원 (ACTIVE 멤버 수)", example = "2")
	private long currentMemberCount;

	@Schema(description = "아지트장 닉네임", example = "보드왕")
	private String hostNickname;

	@Schema(description = "요청 유저의 역할", example = "GUEST")
	private AgitMemberRole myRole;

	@Schema(description = "ACTIVE 멤버 목록")
	private List<Member> members;

	@Schema(description = "묶인 토픽 목록")
	private List<Topic> topics;

	@Getter
	@Builder
	@Schema(description = "아지트 멤버")
	public static class Member {

		@Schema(description = "유저 UUID", example = "018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e")
		private UUID userUuid;

		@Schema(description = "닉네임", example = "보드왕")
		private String nickname;

		@Schema(description = "프로필 이미지 경로", example = "profiles/host.png")
		private String profileImagePath;

		@Schema(description = "역할", example = "HOST")
		private AgitMemberRole role;
	}

	@Getter
	@Builder
	@Schema(description = "묶인 토픽")
	public static class Topic {

		@Schema(description = "토픽 ID", example = "topic-1")
		private String topicId;

		@Schema(description = "토픽 시작 시각")
		private Instant startedAt;
	}
}
