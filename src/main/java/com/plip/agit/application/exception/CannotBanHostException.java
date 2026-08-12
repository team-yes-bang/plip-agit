package com.plip.agit.application.exception;

public class CannotBanHostException extends RuntimeException {

	public CannotBanHostException() {
		super("아지트장은 내보낼 수 없습니다. 먼저 방장 위임을 하세요.");
	}
}
