package com.plip.agit.application.exception;

public class NotAgitHostException extends RuntimeException {

	public NotAgitHostException() {
		super("아지트장만 수행할 수 있습니다.");
	}
}
