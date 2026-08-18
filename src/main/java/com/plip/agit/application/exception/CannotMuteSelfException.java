package com.plip.agit.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CannotMuteSelfException extends RuntimeException {

	public CannotMuteSelfException() {
		super("자기 자신은 뮤트할 수 없습니다.");
	}
}
