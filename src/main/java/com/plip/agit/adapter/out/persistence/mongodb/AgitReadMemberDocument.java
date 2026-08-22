package com.plip.agit.adapter.out.persistence.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgitReadMemberDocument {

	private Long ampId;
	private String userUuid;
	private String nickname;
	private String profileImagePath;
	private String role;

	public AgitReadMemberDocument(
			Long ampId,
			String userUuid,
			String nickname,
			String profileImagePath,
			String role
	) {
		this.ampId = ampId;
		this.userUuid = userUuid;
		this.nickname = nickname;
		this.profileImagePath = profileImagePath;
		this.role = role;
	}
}
