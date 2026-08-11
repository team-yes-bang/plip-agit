package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.ErrorResponse;
import com.plip.agit.application.exception.AgitAlreadyJoinedException;
import com.plip.agit.application.exception.AgitBannedException;
import com.plip.agit.application.exception.AgitCapacityExceededException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.exception.InvalidInviteCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AgitExceptionHandler {

	@ExceptionHandler(AgitNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleAgitNotFound(AgitNotFoundException ex) {
		return ErrorResponse.builder()
				.code("AGIT_NOT_FOUND")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(AgitMemberNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleAgitMemberNotFound(AgitMemberNotFoundException ex) {
		return ErrorResponse.builder()
				.code("AGIT_MEMBER_NOT_FOUND")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(InvalidInviteCodeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidInviteCode(InvalidInviteCodeException ex) {
		return ErrorResponse.builder()
				.code("INVALID_INVITE_CODE")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(AgitBannedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleAgitBanned(AgitBannedException ex) {
		return ErrorResponse.builder()
				.code("MEMBER_BANNED")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(AgitAlreadyJoinedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleAgitAlreadyJoined(AgitAlreadyJoinedException ex) {
		return ErrorResponse.builder()
				.code("ALREADY_JOINED")
				.message(ex.getMessage())
				.ampId(ex.getAmpId())
				.build();
	}

	@ExceptionHandler(AgitCapacityExceededException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleAgitCapacityExceeded(AgitCapacityExceededException ex) {
		return ErrorResponse.builder()
				.code("CAPACITY_FULL")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
		return ErrorResponse.builder()
				.code("INVALID_ARGUMENT")
				.message(ex.getMessage())
				.build();
	}
}
