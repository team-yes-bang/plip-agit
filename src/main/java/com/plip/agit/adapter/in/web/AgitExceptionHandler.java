package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.in.web.dto.ErrorResponse;
import com.plip.agit.application.exception.AgitAlreadyJoinedException;
import com.plip.agit.application.exception.AgitBannedException;
import com.plip.agit.application.exception.AgitCapacityExceededException;
import com.plip.agit.application.exception.AgitMemberNotActiveException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.exception.CannotBanHostException;
import com.plip.agit.application.exception.CannotMuteSelfException;
import com.plip.agit.application.exception.CapacityBelowCurrentException;
import com.plip.agit.application.exception.HostCannotLeaveException;
import com.plip.agit.application.exception.InvalidInviteCodeException;
import com.plip.agit.application.exception.JoinRequestAlreadyPendingException;
import com.plip.agit.application.exception.JoinRequestNotPendingException;
import com.plip.agit.application.exception.InvalidTransferTargetException;
import com.plip.agit.application.exception.NotAgitHostException;
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

	@ExceptionHandler(AgitMemberNotActiveException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleAgitMemberNotActive(AgitMemberNotActiveException ex) {
		return ErrorResponse.builder()
				.code("MEMBER_NOT_ACTIVE")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(NotAgitHostException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleNotAgitHost(NotAgitHostException ex) {
		return ErrorResponse.builder()
				.code("NOT_HOST")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(JoinRequestAlreadyPendingException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleJoinRequestAlreadyPending(JoinRequestAlreadyPendingException ex) {
		return ErrorResponse.builder()
				.code("JOIN_REQUEST_PENDING")
				.message(ex.getMessage())
				.ampId(ex.getAmpId())
				.build();
	}

	@ExceptionHandler(JoinRequestNotPendingException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleJoinRequestNotPending(JoinRequestNotPendingException ex) {
		return ErrorResponse.builder()
				.code("JOIN_REQUEST_NOT_PENDING")
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

	@ExceptionHandler(CapacityBelowCurrentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleCapacityBelowCurrent(CapacityBelowCurrentException ex) {
		return ErrorResponse.builder()
				.code("CAPACITY_BELOW_CURRENT")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(InvalidTransferTargetException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleInvalidTransferTarget(InvalidTransferTargetException ex) {
		return ErrorResponse.builder()
				.code("INVALID_TRANSFER_TARGET")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(CannotBanHostException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleCannotBanHost(CannotBanHostException ex) {
		return ErrorResponse.builder()
				.code("CANNOT_BAN_HOST")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(CannotMuteSelfException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleCannotMuteSelf(CannotMuteSelfException ex) {
		return ErrorResponse.builder()
				.code("CANNOT_MUTE_SELF")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(HostCannotLeaveException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleHostCannotLeave(HostCannotLeaveException ex) {
		return ErrorResponse.builder()
				.code("HOST_CANNOT_LEAVE")
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
