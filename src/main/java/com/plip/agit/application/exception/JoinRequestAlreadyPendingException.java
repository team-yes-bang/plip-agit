package com.plip.agit.application.exception;

import lombok.Getter;

@Getter
public class JoinRequestAlreadyPendingException extends RuntimeException {

	private final Long ampId;

	public JoinRequestAlreadyPendingException(Long ampId) {
		super("이미 입장 요청이 대기 중입니다.");
		this.ampId = ampId;
	}
}
