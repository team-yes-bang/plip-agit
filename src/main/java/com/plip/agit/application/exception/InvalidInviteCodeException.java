package com.plip.agit.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInviteCodeException extends RuntimeException {

	public InvalidInviteCodeException(String message) {
		super(message);
	}
}
