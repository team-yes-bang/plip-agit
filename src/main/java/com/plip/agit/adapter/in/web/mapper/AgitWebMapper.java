package com.plip.agit.adapter.in.web.mapper;

import com.plip.agit.adapter.in.web.dto.AgitDetailResponse;
import com.plip.agit.adapter.in.web.dto.AgitLandingResponse;
import com.plip.agit.adapter.in.web.dto.CreateAgitRequest;
import com.plip.agit.adapter.in.web.dto.CreateAgitResponse;
import com.plip.agit.adapter.in.web.dto.JoinAgitRequest;
import com.plip.agit.adapter.in.web.dto.JoinAgitResponse;
import com.plip.agit.adapter.in.web.dto.MyAgitItemResponse;
import com.plip.agit.adapter.in.web.dto.ReissueInviteCodeResponse;
import com.plip.agit.adapter.in.web.dto.UpdateAgitRequest;
import com.plip.agit.adapter.in.web.dto.UpdateAgitResponse;
import com.plip.agit.adapter.in.web.dto.UpdateMyMemberProfileRequest;
import com.plip.agit.adapter.in.web.dto.UpdateMyMemberProfileResponse;
import com.plip.agit.application.port.in.dto.AgitDetailResultDto;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.in.dto.JoinAgitRequestDto;
import com.plip.agit.application.port.in.dto.JoinAgitResultDto;
import com.plip.agit.application.port.in.dto.MyAgitItemDto;
import com.plip.agit.application.port.in.dto.ReissueInviteCodeResultDto;
import com.plip.agit.application.port.in.dto.UpdateAgitRequestDto;
import com.plip.agit.application.port.in.dto.UpdateAgitResultDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileRequestDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileResultDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AgitWebMapper {

	public CreateAgitRequestDto toDto(CreateAgitRequest request, UUID actorUserUuid) {
		return CreateAgitRequestDto.builder()
				.agitName(request.getAgitName())
				.description(request.getDescription())
				.maximumCapacity(request.getMaximumCapacity())
				.thumbnailPath(request.getThumbnailPath())
				.userUuid(actorUserUuid)
				.nickname(request.getNickname())
				.profileImagePath(request.getProfileImagePath())
				.build();
	}

	public CreateAgitResponse toResponse(CreateAgitResultDto resultDto) {
		return CreateAgitResponse.builder()
				.agitUuid(resultDto.getAgitUuid())
				.agitName(resultDto.getAgitName())
				.description(resultDto.getDescription())
				.maximumCapacity(resultDto.getMaximumCapacity())
				.code(resultDto.getCode())
				.thumbnailPath(resultDto.getThumbnailPath())
				.ampId(resultDto.getAmpId())
				.nickname(resultDto.getNickname())
				.profileImagePath(resultDto.getProfileImagePath())
				.role(resultDto.getRole())
				.build();
	}

	public AgitDetailResponse toDetailResponse(AgitDetailResultDto resultDto) {
		return AgitDetailResponse.builder()
				.agitUuid(resultDto.getAgitUuid())
				.agitName(resultDto.getAgitName())
				.description(resultDto.getDescription())
				.thumbnailPath(resultDto.getThumbnailPath())
				.code(resultDto.getCode())
				.status(resultDto.getStatus())
				.maximumCapacity(resultDto.getMaximumCapacity())
				.currentMemberCount(resultDto.getCurrentMemberCount())
				.hostNickname(resultDto.getHostNickname())
				.myRole(resultDto.getMyRole())
				.members(resultDto.getMembers().stream()
						.map(member -> AgitDetailResponse.Member.builder()
								.ampId(member.getAmpId())
								.userUuid(member.getUserUuid())
								.nickname(member.getNickname())
								.profileImagePath(member.getProfileImagePath())
								.role(member.getRole())
								.build())
						.toList())
				.topics(resultDto.getTopics().stream()
						.map(topic -> AgitDetailResponse.Topic.builder()
								.topicId(topic.getTopicId())
								.startedAt(topic.getStartedAt())
								.build())
						.toList())
				.build();
	}

	public AgitLandingResponse toLandingResponse(AgitLandingResultDto resultDto) {
		return AgitLandingResponse.builder()
				.agitName(resultDto.getAgitName())
				.description(resultDto.getDescription())
				.currentMemberCount(resultDto.getCurrentMemberCount())
				.maximumCapacity(resultDto.getMaximumCapacity())
				.hostNickname(resultDto.getHostNickname())
				.thumbnailPath(resultDto.getThumbnailPath())
				.build();
	}

	public JoinAgitRequestDto toJoinDto(JoinAgitRequest request, UUID actorUserUuid) {
		return JoinAgitRequestDto.builder()
				.userUuid(actorUserUuid)
				.nickname(request.getNickname())
				.profileImagePath(request.getProfileImagePath())
				.build();
	}

	public JoinAgitResponse toJoinResponse(JoinAgitResultDto resultDto) {
		return JoinAgitResponse.builder()
				.agitUuid(resultDto.getAgitUuid())
				.ampId(resultDto.getAmpId())
				.nickname(resultDto.getNickname())
				.profileImagePath(resultDto.getProfileImagePath())
				.role(resultDto.getRole())
				.build();
	}

	public UpdateAgitRequestDto toUpdateDto(UpdateAgitRequest request, UUID actorUserUuid) {
		return UpdateAgitRequestDto.builder()
				.userUuid(actorUserUuid)
				.agitName(request.getAgitName())
				.description(request.getDescription())
				.maximumCapacity(request.getMaximumCapacity())
				.thumbnailPath(request.getThumbnailPath())
				.build();
	}

	public UpdateAgitResponse toUpdateResponse(UpdateAgitResultDto resultDto) {
		return UpdateAgitResponse.builder()
				.agitUuid(resultDto.getAgitUuid())
				.agitName(resultDto.getAgitName())
				.description(resultDto.getDescription())
				.maximumCapacity(resultDto.getMaximumCapacity())
				.thumbnailPath(resultDto.getThumbnailPath())
				.build();
	}

	public ReissueInviteCodeResponse toReissueResponse(ReissueInviteCodeResultDto resultDto) {
		return ReissueInviteCodeResponse.builder()
				.code(resultDto.getCode())
				.build();
	}

	public List<MyAgitItemResponse> toMyAgitResponses(List<MyAgitItemDto> resultDtos) {
		return resultDtos.stream()
				.map(dto -> MyAgitItemResponse.builder()
						.agitUuid(dto.getAgitUuid())
						.agitName(dto.getAgitName())
						.build())
				.toList();
	}

	public UpdateMyMemberProfileRequestDto toUpdateMyProfileDto(
			UpdateMyMemberProfileRequest request,
			UUID actorUserUuid
	) {
		return UpdateMyMemberProfileRequestDto.builder()
				.userUuid(actorUserUuid)
				.nickname(request.getNickname())
				.profileImagePath(request.getProfileImagePath())
				.build();
	}

	public UpdateMyMemberProfileResponse toUpdateMyProfileResponse(
			UpdateMyMemberProfileResultDto resultDto
	) {
		return UpdateMyMemberProfileResponse.builder()
				.nickname(resultDto.getNickname())
				.profileImagePath(resultDto.getProfileImagePath())
				.build();
	}
}
