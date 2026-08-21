package com.plip.agit.application.port.in.dto;

import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgitDetailResultDto {

	private final UUID agitUuid;
	private final String agitName;
	private final String description;
	private final String thumbnailPath;
	private final String code;
	private final AgitStatus status;
	private final int maximumCapacity;
	private final long currentMemberCount;
	private final String hostNickname;
	private final AgitMemberRole myRole;
	private final List<Member> members;
	private final List<Topic> topics;

	@Getter
	@Builder
	public static class Member {

		private final UUID userUuid;
		private final String nickname;
		private final String profileImagePath;
		private final AgitMemberRole role;
	}

	@Getter
	@Builder
	public static class Topic {

		private final String topicId;
		private final Instant startedAt;
	}
}
