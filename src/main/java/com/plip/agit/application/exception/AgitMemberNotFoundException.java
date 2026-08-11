package com.plip.agit.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AgitMemberNotFoundException extends RuntimeException {

	public AgitMemberNotFoundException() {
		super("아지트 멤버를 찾을 수 없습니다.");
	}
}
