package com.plip.agit.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgitMemberProfileUpdateProfileTest {

	@Test
	void updateProfile_updatesOnlyProvidedFields() {
		AgitMemberProfile profile = activeGuest("보드왕", "profiles/old.png");

		profile.updateProfile("새닉네임", null);

		assertEquals("새닉네임", profile.getNickname());
		assertEquals("profiles/old.png", profile.getProfileImagePath());
	}

	@Test
	void updateProfile_clearsImageWhenBlankProvided() {
		AgitMemberProfile profile = activeGuest("보드왕", "profiles/old.png");

		profile.updateProfile(null, "  ");

		assertEquals("보드왕", profile.getNickname());
		assertNull(profile.getProfileImagePath());
	}

	@Test
	void updateProfile_rejectsNonActive() {
		AgitMemberProfile profile = AgitMemberProfile.reconstitute(
				1L,
				10L,
				UUID.randomUUID(),
				"보드왕",
				null,
				AgitMemberStatus.LEFT,
				AgitMemberRole.GUEST,
				null
		);

		assertThrows(IllegalStateException.class, () -> profile.updateProfile("새닉네임", null));
	}

	@Test
	void updateProfile_rejectsInvalidNickname() {
		AgitMemberProfile profile = activeGuest("보드왕", null);

		assertThrows(IllegalArgumentException.class, () -> profile.updateProfile("!", null));
	}

	private static AgitMemberProfile activeGuest(String nickname, String imagePath) {
		return AgitMemberProfile.reconstitute(
				1L,
				10L,
				UUID.randomUUID(),
				nickname,
				imagePath,
				AgitMemberStatus.ACTIVE,
				AgitMemberRole.GUEST,
				null
		);
	}
}
