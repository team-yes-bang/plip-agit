package com.plip.agit.domain.model;

import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgitMemberProfile {

	public static final int NICKNAME_MIN_LENGTH = 2;
	public static final int NICKNAME_MAX_LENGTH = 12;

	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[0-9A-Za-z가-힣]{2,12}$");

	private Long id;
	private Long agitId;
	private UUID userUuid;
	private String nickname;
	private String profileImagePath;
	private AgitMemberStatus status;
	private AgitMemberRole role;
	private String applyItems;

	public static AgitMemberProfile createHost(
			Long agitId,
			UUID userUuid,
			String nickname,
			String profileImagePath
	) {
		if (agitId == null) {
			throw new IllegalArgumentException("아지트 ID는 필수입니다.");
		}
		if (userUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}

		return AgitMemberProfile.builder()
				.agitId(agitId)
				.userUuid(userUuid)
				.nickname(requireNickname(nickname))
				.profileImagePath(normalizeProfileImagePath(profileImagePath))
				.status(AgitMemberStatus.ACTIVE)
				.role(AgitMemberRole.HOST)
				.applyItems(null)
				.build();
	}

	public static AgitMemberProfile createGuest(
			Long agitId,
			UUID userUuid,
			String nickname,
			String profileImagePath
	) {
		if (agitId == null) {
			throw new IllegalArgumentException("아지트 ID는 필수입니다.");
		}
		if (userUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}

		return AgitMemberProfile.builder()
				.agitId(agitId)
				.userUuid(userUuid)
				.nickname(requireNickname(nickname))
				.profileImagePath(normalizeProfileImagePath(profileImagePath))
				.status(AgitMemberStatus.ACTIVE)
				.role(AgitMemberRole.GUEST)
				.applyItems(null)
				.build();
	}

	public static AgitMemberProfile reconstitute(
			Long id,
			Long agitId,
			UUID userUuid,
			String nickname,
			String profileImagePath,
			AgitMemberStatus status,
			AgitMemberRole role,
			String applyItems
	) {
		return AgitMemberProfile.builder()
				.id(id)
				.agitId(agitId)
				.userUuid(userUuid)
				.nickname(nickname)
				.profileImagePath(profileImagePath)
				.status(status != null ? status : AgitMemberStatus.ACTIVE)
				.role(role != null ? role : AgitMemberRole.GUEST)
				.applyItems(applyItems)
				.build();
	}

	/**
	 * 아지트에서 내보내기. 이미 BANNED면 멱등 no-op.
	 */
	public void ban() {
		if (this.status == AgitMemberStatus.BANNED) {
			return;
		}
		this.status = AgitMemberStatus.BANNED;
	}

	/**
	 * 아지트 나가기. 이미 LEFT면 멱등 no-op. 이미 BANNED면 멱등 no-op.
	 */
	public void leave() {
		if (this.status == AgitMemberStatus.LEFT || this.status == AgitMemberStatus.BANNED) {
			return;
		}
		this.status = AgitMemberStatus.LEFT;
	}

	/**
	 * 밴 해제 후 LEFT로 수렴. 이미 LEFT면 멱등 no-op.
	 */
	public void unbanToLeft() {
		if (this.status == AgitMemberStatus.LEFT) {
			return;
		}
		if (this.status != AgitMemberStatus.BANNED) {
			throw new IllegalStateException("밴 상태가 아닌 멤버는 해제할 수 없습니다.");
		}
		this.status = AgitMemberStatus.LEFT;
	}

	/**
	 * LEFT 상태에서 재입장. 닉네임·프로필 이미지를 요청값으로 갱신하고 ACTIVE로 전환한다.
	 */
	public void rejoin(String nickname, String profileImagePath) {
		if (this.status != AgitMemberStatus.LEFT) {
			throw new IllegalStateException("LEFT 상태가 아닌 멤버는 재입장할 수 없습니다.");
		}
		this.nickname = requireNickname(nickname);
		this.profileImagePath = normalizeProfileImagePath(profileImagePath);
		this.status = AgitMemberStatus.ACTIVE;
	}

	/**
	 * ACTIVE 본인 프로필 부분 수정. null인 필드는 유지한다.
	 * profileImagePath에 빈 문자열을 보내면 이미지를 제거한다.
	 */
	public void updateProfile(String nickname, String profileImagePath) {
		if (this.status != AgitMemberStatus.ACTIVE) {
			throw new IllegalStateException("ACTIVE 멤버만 프로필을 수정할 수 있습니다.");
		}
		if (nickname != null) {
			this.nickname = requireNickname(nickname);
		}
		if (profileImagePath != null) {
			this.profileImagePath = normalizeProfileImagePath(profileImagePath);
		}
	}

	/**
	 * 방장 위임: ACTIVE HOST만 GUEST로 강등한다.
	 */
	public void demoteToGuest() {
		if (this.status != AgitMemberStatus.ACTIVE || this.role != AgitMemberRole.HOST) {
			throw new IllegalStateException("ACTIVE 아지트장만 역할을 넘길 수 있습니다.");
		}
		this.role = AgitMemberRole.GUEST;
	}

	/**
	 * 방장 위임: ACTIVE GUEST만 HOST로 승격한다.
	 */
	public void promoteToHost() {
		if (this.status != AgitMemberStatus.ACTIVE || this.role != AgitMemberRole.GUEST) {
			throw new IllegalStateException("ACTIVE 게스트만 아지트장이 될 수 있습니다.");
		}
		this.role = AgitMemberRole.HOST;
	}

	private static String requireNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			throw new IllegalArgumentException("닉네임은 필수입니다.");
		}
		String trimmed = nickname.trim();
		if (!NICKNAME_PATTERN.matcher(trimmed).matches()) {
			throw new IllegalArgumentException(
					"닉네임은 영문·숫자·한글 " + NICKNAME_MIN_LENGTH + "~" + NICKNAME_MAX_LENGTH
							+ "자이며, 특수문자와 공백을 사용할 수 없습니다."
			);
		}
		return trimmed;
	}

	private static String normalizeProfileImagePath(String profileImagePath) {
		if (profileImagePath == null || profileImagePath.isBlank()) {
			return null;
		}
		return profileImagePath.trim();
	}

	@Builder(access = AccessLevel.PRIVATE)
	private AgitMemberProfile(
			Long id,
			Long agitId,
			UUID userUuid,
			String nickname,
			String profileImagePath,
			AgitMemberStatus status,
			AgitMemberRole role,
			String applyItems
	) {
		this.id = id;
		this.agitId = agitId;
		this.userUuid = userUuid;
		this.nickname = nickname;
		this.profileImagePath = profileImagePath;
		this.status = status;
		this.role = role;
		this.applyItems = applyItems;
	}
}
