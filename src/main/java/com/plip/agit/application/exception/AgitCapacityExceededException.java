package com.plip.agit.application.exception;

public class AgitCapacityExceededException extends RuntimeException {

	public AgitCapacityExceededException() {
		super("아지트 정원이 가득 찼습니다.");
	}
}
