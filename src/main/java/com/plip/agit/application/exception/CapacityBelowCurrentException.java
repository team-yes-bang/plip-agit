package com.plip.agit.application.exception;

public class CapacityBelowCurrentException extends RuntimeException {

	public CapacityBelowCurrentException(long currentMemberCount) {
		super("인원수는 현재 인원(" + currentMemberCount + ")보다 작을 수 없습니다.");
	}
}
