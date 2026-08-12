package com.plip.agit.application.exception;

public class InvalidTransferTargetException extends RuntimeException {

	public InvalidTransferTargetException() {
		super("방장 위임 대상이 유효하지 않습니다. ACTIVE 게스트만 가능합니다.");
	}
}
