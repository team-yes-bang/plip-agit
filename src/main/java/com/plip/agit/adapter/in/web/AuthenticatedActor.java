package com.plip.agit.adapter.in.web;

import java.util.UUID;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

final class AuthenticatedActor {

	private AuthenticatedActor() {
	}

	static UUID requireUserUuid() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null
				|| !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			throw new IllegalStateException("인증된 사용자가 없습니다.");
		}
		return UUID.fromString(authentication.getName());
	}
}
