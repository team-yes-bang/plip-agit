package com.plip.agit.application.exception;

public class AgitBannedException extends RuntimeException {

	public AgitBannedException() {
		super("내보내진 아지트에는 입장할 수 없습니다.");
	}
}
