package com.plip.agit.application.exception;

public class JoinRequestNotPendingException extends RuntimeException {

	public JoinRequestNotPendingException() {
		super("대기 중인 입장 요청이 아닙니다.");
	}
}
