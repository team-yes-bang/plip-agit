package com.plip.agit.application.exception;

public class HostCannotLeaveException extends RuntimeException {

	public HostCannotLeaveException() {
		super("방장은 다른 ACTIVE 멤버가 없을 때만 나갈 수 있습니다.");
	}
}
