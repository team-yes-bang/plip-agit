package com.plip.agit.application.port.out;

public final class AgitEventTopics {

	public static final String CREATED = "agit.created";
	public static final String MEMBER_JOINED = "agit.member-joined";
	public static final String MEMBER_LEFT = "agit.member-left";
	public static final String DELETED = "agit.deleted";
	public static final String MEMBER_BANNED = "agit.member-banned";
	public static final String MEMBER_UNBANNED = "agit.member-unbanned";
	public static final String UPDATED = "agit.updated";
	public static final String HOST_TRANSFERRED = "agit.host-transferred";
	public static final String INVITE_CODE_REISSUED = "agit.invite-code-reissued";
	public static final String MEMBER_PROFILE_UPDATED = "agit.member-profile-updated";

	private AgitEventTopics() {
	}
}
