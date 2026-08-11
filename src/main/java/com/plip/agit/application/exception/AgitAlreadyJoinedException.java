package com.plip.agit.application.exception;

import lombok.Getter;

@Getter
public class AgitAlreadyJoinedException extends RuntimeException {

	private final Long ampId;

	public AgitAlreadyJoinedException(Long ampId) {
		super("이미 참여 중인 아지트입니다.");
		this.ampId = ampId;
	}
}
