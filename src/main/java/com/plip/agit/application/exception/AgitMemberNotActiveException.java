package com.plip.agit.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AgitMemberNotActiveException extends RuntimeException {

	public AgitMemberNotActiveException() {
		super("ACTIVE 멤버만 프로필을 수정할 수 있습니다.");
	}
}
