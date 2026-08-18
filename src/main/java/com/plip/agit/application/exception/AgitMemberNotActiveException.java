package com.plip.agit.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AgitMemberNotActiveException extends RuntimeException {

	public AgitMemberNotActiveException() {
		this("ACTIVE 멤버만 이 작업을 수행할 수 있습니다.");
	}

	public AgitMemberNotActiveException(String message) {
		super(message);
	}
}
