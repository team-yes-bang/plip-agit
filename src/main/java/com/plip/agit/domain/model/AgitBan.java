package com.plip.agit.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgitBan {

	private Long id;
	private Long agitId;
	private UUID userUuid;
	private Long ampId;
	private String bannedNickname;
	private LocalDateTime bannedAt;
	private LocalDateTime unbannedAt;

	public static AgitBan create(Long agitId, UUID userUuid, Long ampId, String bannedNickname) {
		if (agitId == null) {
			throw new IllegalArgumentException("아지트 ID는 필수입니다.");
		}
		if (userUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}
		if (ampId == null) {
			throw new IllegalArgumentException("멤버 프로필 ID는 필수입니다.");
		}
		if (bannedNickname == null || bannedNickname.isBlank()) {
			throw new IllegalArgumentException("밴 닉네임 스냅샷은 필수입니다.");
		}

		LocalDateTime now = LocalDateTime.now();
		return AgitBan.builder()
				.agitId(agitId)
				.userUuid(userUuid)
				.ampId(ampId)
				.bannedNickname(bannedNickname.trim())
				.bannedAt(now)
				.unbannedAt(null)
				.build();
	}

	public static AgitBan reconstitute(
			Long id,
			Long agitId,
			UUID userUuid,
			Long ampId,
			String bannedNickname,
			LocalDateTime bannedAt,
			LocalDateTime unbannedAt
	) {
		return AgitBan.builder()
				.id(id)
				.agitId(agitId)
				.userUuid(userUuid)
				.ampId(ampId)
				.bannedNickname(bannedNickname)
				.bannedAt(bannedAt)
				.unbannedAt(unbannedAt)
				.build();
	}

	public void unban(LocalDateTime now) {
		if (this.unbannedAt != null) {
			return;
		}
		if (now == null) {
			throw new IllegalArgumentException("해제 시각은 필수입니다.");
		}
		this.unbannedAt = now;
	}

	public boolean isActive() {
		return this.unbannedAt == null;
	}

	@Builder(access = AccessLevel.PRIVATE)
	private AgitBan(
			Long id,
			Long agitId,
			UUID userUuid,
			Long ampId,
			String bannedNickname,
			LocalDateTime bannedAt,
			LocalDateTime unbannedAt
	) {
		this.id = id;
		this.agitId = agitId;
		this.userUuid = userUuid;
		this.ampId = ampId;
		this.bannedNickname = bannedNickname;
		this.bannedAt = bannedAt;
		this.unbannedAt = unbannedAt;
	}
}
