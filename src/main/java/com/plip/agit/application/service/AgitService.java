package com.plip.agit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plip.agit.application.exception.AgitAlreadyJoinedException;
import com.plip.agit.application.exception.AgitBannedException;
import com.plip.agit.application.exception.AgitCapacityExceededException;
import com.plip.agit.application.exception.AgitMemberNotActiveException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.exception.JoinRequestAlreadyPendingException;
import com.plip.agit.application.exception.JoinRequestNotPendingException;
import com.plip.agit.application.exception.CannotBanHostException;
import com.plip.agit.application.exception.CapacityBelowCurrentException;
import com.plip.agit.application.exception.HostCannotLeaveException;
import com.plip.agit.application.exception.InvalidInviteCodeException;
import com.plip.agit.application.exception.InvalidTransferTargetException;
import com.plip.agit.application.exception.NotAgitHostException;
import com.plip.agit.application.port.in.AgitUseCase;
import com.plip.agit.application.port.in.RefreshAgitReadModelUseCase;
import com.plip.agit.application.port.in.dto.AgitDetailResultDto;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.AgitPreviewResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.in.dto.JoinAgitRequestDto;
import com.plip.agit.application.port.in.dto.JoinAgitResultDto;
import com.plip.agit.application.port.in.dto.JoinRequestItemDto;
import com.plip.agit.application.port.in.dto.MyAgitItemDto;
import com.plip.agit.application.port.in.dto.ReissueInviteCodeResultDto;
import com.plip.agit.application.port.in.dto.UpdateAgitRequestDto;
import com.plip.agit.application.port.in.dto.UpdateAgitResultDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileRequestDto;
import com.plip.agit.application.port.in.dto.UpdateMyMemberProfileResultDto;
import com.plip.agit.application.port.out.ActiveMembershipAgit;
import com.plip.agit.application.port.out.AgitBanPersistencePort;
import com.plip.agit.application.port.out.AgitEventTopics;
import com.plip.agit.application.port.out.AgitMembershipCachePort;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.application.port.out.AgitReadMemberSnapshot;
import com.plip.agit.application.port.out.AgitReadPersistencePort;
import com.plip.agit.application.port.out.AgitReadSnapshot;
import com.plip.agit.application.port.out.EventPublisherPort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitBan;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.domain.model.AgitStatus;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitService implements AgitUseCase {

	private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int CODE_GENERATE_MAX_ATTEMPTS = 10;
	private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");

	private final AgitPersistencePort agitPersistencePort;
	private final AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;
	private final AgitBanPersistencePort agitBanPersistencePort;
	private final AgitReadPersistencePort agitReadPersistencePort;
	private final RefreshAgitReadModelUseCase refreshAgitReadModelUseCase;
	private final AgitMembershipCachePort agitMembershipCachePort;
	private final EventPublisherPort eventPublisherPort;
	private final ObjectMapper objectMapper;
	private final SecureRandom secureRandom = new SecureRandom();

	@Override
	@Transactional
	public CreateAgitResultDto createAgit(CreateAgitRequestDto requestDto) {
		if (requestDto.getUserUuid() == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}

		String code = generateUniqueCode();
		int allowedMaxCapacity = resolveAllowedMaxCapacity();

		Agit agit = Agit.create(
				requestDto.getAgitName(),
				requestDto.getDescription(),
				requestDto.getMaximumCapacity(),
				code,
				requestDto.getThumbnailPath(),
				allowedMaxCapacity
		);

		Agit savedAgit = agitPersistencePort.save(agit);

		AgitMemberProfile hostProfile = AgitMemberProfile.createHost(
				savedAgit.getId(),
				requestDto.getUserUuid(),
				requestDto.getNickname(),
				requestDto.getProfileImagePath()
		);
		AgitMemberProfile savedProfile = agitMemberProfilePersistencePort.save(hostProfile);

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("agitUuid", savedAgit.getAgitUuid().toString());
		payload.put("agitName", savedAgit.getAgitName());
		payload.put("description", savedAgit.getDescription());
		payload.put("maximumCapacity", savedAgit.getMaximumCapacity());
		payload.put("code", savedAgit.getCode());
		payload.put("thumbnailPath", savedAgit.getThumbnailPath());
		payload.put("hostUserUuid", savedProfile.getUserUuid().toString());
		payload.put("hostNickname", savedProfile.getNickname());
		publishEvent(AgitEventTopics.CREATED, savedAgit.getAgitUuid(), payload);
		scheduleReadModelRefresh(savedAgit.getAgitUuid());
		scheduleMembershipPut(savedAgit.getAgitUuid(), savedProfile.getUserUuid(), savedProfile.getRole());

		return CreateAgitResultDto.builder()
				.agitUuid(savedAgit.getAgitUuid())
				.agitName(savedAgit.getAgitName())
				.description(savedAgit.getDescription())
				.maximumCapacity(savedAgit.getMaximumCapacity())
				.code(savedAgit.getCode())
				.thumbnailPath(savedAgit.getThumbnailPath())
				.ampId(savedProfile.getId())
				.nickname(savedProfile.getNickname())
				.profileImagePath(savedProfile.getProfileImagePath())
				.role(savedProfile.getRole())
				.build();
	}

	/**
	 * 초대 코드로 랜딩 표시용 아지트 정보를 조회한다. Mongo 읽기 문서를 우선하고, miss면 MySQL.
	 *
	 * <p>TODO(prod): Gateway 미사용. K8s Ingress(+ Service 앞단)에서 이 GET에 IP rate limit
	 * (권장 60/min, burst 10~20/10s). 테스트용 Gateway whitelist와 별개.
	 */
	@Override
	public AgitLandingResultDto getLandingByCode(String code) {
		String normalizedCode = normalizeInviteCode(code);

		Optional<AgitLandingResultDto> fromMongo = agitReadPersistencePort.findActiveByCode(normalizedCode)
				.flatMap(this::toLandingFromReadModel);
		if (fromMongo.isPresent()) {
			return fromMongo.get();
		}

		log.info("랜딩 Mongo miss, MySQL 폴백 code={}", normalizedCode);
		return getLandingByCodeFromMysql(normalizedCode);
	}

	/**
	 * 아지트 상세를 Mongo 읽기 모델에서 조회한다. miss면 MySQL로 refresh 후 재조회한다.
	 * ACTIVE 멤버만 허용한다. 응답에 Mongo 읽기 문서의 초대 코드를 포함한다.
	 */
	@Override
	public AgitDetailResultDto getAgit(UUID agitUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		if (agitUuid == null) {
			throw new IllegalArgumentException("아지트 UUID는 필수입니다.");
		}

		Optional<AgitReadSnapshot> found = agitReadPersistencePort.findByAgitUuid(agitUuid);
		if (found.isEmpty() || membersMissingAmpId(found.get())) {
			log.info("상세 Mongo miss 또는 ampId 없음, 읽기 모델 refresh agitUuid={}", agitUuid);
			refreshAgitReadModelUseCase.refresh(agitUuid);
			found = agitReadPersistencePort.findByAgitUuid(agitUuid);
		}

		AgitReadSnapshot snapshot = found.orElseThrow(AgitNotFoundException::new);
		if (snapshot.status() != AgitStatus.ACTIVE) {
			throw new AgitNotFoundException();
		}

		AgitReadMemberSnapshot me = snapshot.members().stream()
				.filter(member -> actorUserUuid.equals(member.userUuid()))
				.findFirst()
				.orElseThrow(AgitMemberNotActiveException::new);

		AgitReadMemberSnapshot host = snapshot.members().stream()
				.filter(member -> member.role() == AgitMemberRole.HOST)
				.findFirst()
				.orElseThrow(AgitNotFoundException::new);

		return toDetailResult(snapshot, host, me);
	}

	private boolean membersMissingAmpId(AgitReadSnapshot snapshot) {
		return snapshot.members().stream().anyMatch(member -> member.ampId() == null);
	}

	private Optional<AgitLandingResultDto> toLandingFromReadModel(AgitReadSnapshot snapshot) {
		return snapshot.members().stream()
				.filter(member -> member.role() == AgitMemberRole.HOST)
				.findFirst()
				.map(host -> toLandingResult(snapshot, host));
	}

	private AgitLandingResultDto toLandingResult(AgitReadSnapshot snapshot, AgitReadMemberSnapshot host) {
		return AgitLandingResultDto.builder()
				.agitName(snapshot.agitName())
				.description(snapshot.description())
				.currentMemberCount(snapshot.members().size())
				.maximumCapacity(snapshot.maximumCapacity())
				.hostNickname(host.nickname())
				.thumbnailPath(snapshot.thumbnailPath())
				.build();
	}

	private AgitDetailResultDto toDetailResult(
			AgitReadSnapshot snapshot,
			AgitReadMemberSnapshot host,
			AgitReadMemberSnapshot me
	) {
		List<AgitDetailResultDto.Member> members = snapshot.members().stream()
				.map(member -> AgitDetailResultDto.Member.builder()
						.ampId(member.ampId())
						.userUuid(member.userUuid())
						.nickname(member.nickname())
						.profileImagePath(member.profileImagePath())
						.role(member.role())
						.build())
				.toList();
		List<AgitDetailResultDto.Topic> topics = snapshot.topics().stream()
				.map(topic -> AgitDetailResultDto.Topic.builder()
						.topicId(topic.topicId())
						.startedAt(topic.startedAt())
						.build())
				.toList();
		return AgitDetailResultDto.builder()
				.agitUuid(snapshot.agitUuid())
				.agitName(snapshot.agitName())
				.description(snapshot.description())
				.thumbnailPath(snapshot.thumbnailPath())
				.code(snapshot.code())
				.status(snapshot.status())
				.maximumCapacity(snapshot.maximumCapacity())
				.currentMemberCount(snapshot.members().size())
				.hostNickname(host.nickname())
				.myRole(me.role())
				.members(members)
				.topics(topics)
				.build();
	}

	private AgitLandingResultDto getLandingByCodeFromMysql(String normalizedCode) {
		Agit agit = agitPersistencePort.findActiveByCode(normalizedCode)
				.orElseThrow(AgitNotFoundException::new);

		refreshAgitReadModelUseCase.refresh(agit.getAgitUuid());

		AgitMemberProfile host = agitMemberProfilePersistencePort
				.findActiveHostByAgitId(agit.getId())
				.orElseThrow(AgitNotFoundException::new);

		long currentMemberCount = agitMemberProfilePersistencePort.countActiveByAgitId(agit.getId());

		return AgitLandingResultDto.builder()
				.agitName(agit.getAgitName())
				.description(agit.getDescription())
				.currentMemberCount(currentMemberCount)
				.maximumCapacity(agit.getMaximumCapacity())
				.hostNickname(host.getNickname())
				.thumbnailPath(agit.getThumbnailPath())
				.build();
	}

	/**
	 * 초대 코드로 아지트에 입장한다. 신규는 GUEST INSERT, LEFT는 닉네임·이미지 갱신 후 ACTIVE.
	 */
	@Override
	@Transactional
	public JoinAgitResultDto joinAgit(String code, JoinAgitRequestDto requestDto) {
		requireActorUserUuid(requestDto.getUserUuid());

		String normalizedCode = normalizeInviteCode(code);
		Agit agit = agitPersistencePort.findActiveByCode(normalizedCode)
				.orElseThrow(AgitNotFoundException::new);

		var existingProfile = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agit.getId(), requestDto.getUserUuid());

		if (existingProfile.isPresent()) {
			AgitMemberProfile profile = existingProfile.get();
			if (profile.getStatus() == AgitMemberStatus.BANNED) {
				throw new AgitBannedException();
			}
			if (profile.getStatus() == AgitMemberStatus.ACTIVE) {
				throw new AgitAlreadyJoinedException(profile.getId());
			}
		}

		long currentMemberCount = agitMemberProfilePersistencePort.countActiveByAgitId(agit.getId());
		if (currentMemberCount >= agit.getMaximumCapacity()) {
			throw new AgitCapacityExceededException();
		}

		AgitMemberProfile savedProfile;
		if (existingProfile.isEmpty()) {
			AgitMemberProfile guest = AgitMemberProfile.createGuest(
					agit.getId(),
					requestDto.getUserUuid(),
					requestDto.getNickname(),
					requestDto.getProfileImagePath()
			);
			savedProfile = agitMemberProfilePersistencePort.save(guest);
		} else {
			AgitMemberProfile profile = existingProfile.get();
			if (profile.getStatus() == AgitMemberStatus.PENDING) {
				profile.approveJoin();
				profile.updateProfile(requestDto.getNickname(), requestDto.getProfileImagePath());
			} else {
				profile.rejoin(requestDto.getNickname(), requestDto.getProfileImagePath());
			}
			savedProfile = agitMemberProfilePersistencePort.save(profile);
		}

		Map<String, Object> joinPayload = new LinkedHashMap<>();
		joinPayload.put("agitUuid", agit.getAgitUuid().toString());
		joinPayload.put("userUuid", savedProfile.getUserUuid().toString());
		joinPayload.put("nickname", savedProfile.getNickname());
		joinPayload.put("profileImagePath", savedProfile.getProfileImagePath());
		joinPayload.put("role", savedProfile.getRole().name());
		publishEvent(AgitEventTopics.MEMBER_JOINED, agit.getAgitUuid(), joinPayload);
		scheduleReadModelRefresh(agit.getAgitUuid());
		scheduleMembershipPut(agit.getAgitUuid(), savedProfile.getUserUuid(), savedProfile.getRole());

		return JoinAgitResultDto.builder()
				.agitUuid(agit.getAgitUuid())
				.ampId(savedProfile.getId())
				.nickname(savedProfile.getNickname())
				.profileImagePath(savedProfile.getProfileImagePath())
				.role(savedProfile.getRole())
				.build();
	}

	@Override
	public AgitPreviewResultDto getPreview(UUID agitUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		Agit agit = requireActiveAgit(agitUuid);
		AgitMemberProfile host = agitMemberProfilePersistencePort
				.findActiveHostByAgitId(agit.getId())
				.orElseThrow(AgitNotFoundException::new);
		String myStatus = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agit.getId(), actorUserUuid)
				.map(profile -> profile.getStatus().name())
				.orElse(null);
		return AgitPreviewResultDto.builder()
				.agitUuid(agit.getAgitUuid())
				.agitName(agit.getAgitName())
				.description(agit.getDescription())
				.currentMemberCount(agitMemberProfilePersistencePort.countActiveByAgitId(agit.getId()))
				.maximumCapacity(agit.getMaximumCapacity())
				.hostNickname(host.getNickname())
				.thumbnailPath(agit.getThumbnailPath())
				.myStatus(myStatus)
				.build();
	}

	@Override
	@Transactional
	public JoinAgitResultDto requestJoin(UUID agitUuid, JoinAgitRequestDto requestDto) {
		requireActorUserUuid(requestDto.getUserUuid());
		Agit agit = requireActiveAgit(agitUuid);
		var existingProfile = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agit.getId(), requestDto.getUserUuid());

		if (existingProfile.isPresent()) {
			AgitMemberProfile profile = existingProfile.get();
			if (profile.getStatus() == AgitMemberStatus.BANNED) {
				throw new AgitBannedException();
			}
			if (profile.getStatus() == AgitMemberStatus.ACTIVE) {
				throw new AgitAlreadyJoinedException(profile.getId());
			}
			if (profile.getStatus() == AgitMemberStatus.PENDING) {
				throw new JoinRequestAlreadyPendingException(profile.getId());
			}
		}

		AgitMemberProfile savedProfile;
		if (existingProfile.isEmpty()) {
			savedProfile = agitMemberProfilePersistencePort.save(
					AgitMemberProfile.createPendingGuest(
							agit.getId(),
							requestDto.getUserUuid(),
							requestDto.getNickname(),
							requestDto.getProfileImagePath()
					)
			);
		} else {
			AgitMemberProfile profile = existingProfile.get();
			profile.requestJoin(requestDto.getNickname(), requestDto.getProfileImagePath());
			savedProfile = agitMemberProfilePersistencePort.save(profile);
		}

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("agitUuid", agit.getAgitUuid().toString());
		payload.put("userUuid", savedProfile.getUserUuid().toString());
		payload.put("nickname", savedProfile.getNickname());
		payload.put("ampId", savedProfile.getId());
		publishEvent(AgitEventTopics.JOIN_REQUESTED, agit.getAgitUuid(), payload);

		return JoinAgitResultDto.builder()
				.agitUuid(agit.getAgitUuid())
				.ampId(savedProfile.getId())
				.nickname(savedProfile.getNickname())
				.profileImagePath(savedProfile.getProfileImagePath())
				.role(savedProfile.getRole())
				.build();
	}

	@Override
	public List<JoinRequestItemDto> listJoinRequests(UUID agitUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), actorUserUuid);
		return agitMemberProfilePersistencePort
				.findByAgitIdAndStatus(agit.getId(), AgitMemberStatus.PENDING)
				.stream()
				.map(profile -> JoinRequestItemDto.builder()
						.ampId(profile.getId())
						.userUuid(profile.getUserUuid())
						.nickname(profile.getNickname())
						.profileImagePath(profile.getProfileImagePath())
						.build())
				.toList();
	}

	@Override
	@Transactional
	public JoinAgitResultDto approveJoinRequest(UUID agitUuid, Long ampId, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		if (ampId == null) {
			throw new IllegalArgumentException("멤버 프로필 ID는 필수입니다.");
		}
		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), actorUserUuid);
		AgitMemberProfile target = agitMemberProfilePersistencePort.findById(ampId)
				.orElseThrow(AgitMemberNotFoundException::new);
		if (!agit.getId().equals(target.getAgitId())) {
			throw new AgitMemberNotFoundException();
		}
		if (target.getStatus() != AgitMemberStatus.PENDING) {
			throw new JoinRequestNotPendingException();
		}
		long currentMemberCount = agitMemberProfilePersistencePort.countActiveByAgitId(agit.getId());
		if (currentMemberCount >= agit.getMaximumCapacity()) {
			throw new AgitCapacityExceededException();
		}
		target.approveJoin();
		AgitMemberProfile saved = agitMemberProfilePersistencePort.save(target);

		Map<String, Object> joinPayload = new LinkedHashMap<>();
		joinPayload.put("agitUuid", agit.getAgitUuid().toString());
		joinPayload.put("userUuid", saved.getUserUuid().toString());
		joinPayload.put("nickname", saved.getNickname());
		joinPayload.put("profileImagePath", saved.getProfileImagePath());
		joinPayload.put("role", saved.getRole().name());
		publishEvent(AgitEventTopics.MEMBER_JOINED, agit.getAgitUuid(), joinPayload);
		scheduleReadModelRefresh(agit.getAgitUuid());
		scheduleMembershipPut(agit.getAgitUuid(), saved.getUserUuid(), saved.getRole());

		return JoinAgitResultDto.builder()
				.agitUuid(agit.getAgitUuid())
				.ampId(saved.getId())
				.nickname(saved.getNickname())
				.profileImagePath(saved.getProfileImagePath())
				.role(saved.getRole())
				.build();
	}

	@Override
	@Transactional
	public void rejectJoinRequest(UUID agitUuid, Long ampId, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		if (ampId == null) {
			throw new IllegalArgumentException("멤버 프로필 ID는 필수입니다.");
		}
		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), actorUserUuid);
		AgitMemberProfile target = agitMemberProfilePersistencePort.findById(ampId)
				.orElseThrow(AgitMemberNotFoundException::new);
		if (!agit.getId().equals(target.getAgitId())) {
			throw new AgitMemberNotFoundException();
		}
		if (target.getStatus() != AgitMemberStatus.PENDING) {
			throw new JoinRequestNotPendingException();
		}
		target.rejectJoin();
		agitMemberProfilePersistencePort.save(target);
		scheduleReadModelRefresh(agit.getAgitUuid());
	}

	/**
	 * 아지트장이 멤버를 내보낸다. ampId로 대상을 찾고, 이후 식별은 userUuid.
	 */
	@Override
	@Transactional
	public void banMember(UUID agitUuid, Long ampId, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		if (ampId == null) {
			throw new IllegalArgumentException("멤버 프로필 ID는 필수입니다.");
		}

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), actorUserUuid);

		AgitMemberProfile target = agitMemberProfilePersistencePort.findById(ampId)
				.orElseThrow(AgitMemberNotFoundException::new);
		if (!agit.getId().equals(target.getAgitId())) {
			throw new AgitMemberNotFoundException();
		}

		if (target.getStatus() == AgitMemberStatus.BANNED) {
			return;
		}

		if (target.getRole() == AgitMemberRole.HOST && target.getStatus() == AgitMemberStatus.ACTIVE) {
			throw new CannotBanHostException();
		}

		target.ban();
		agitMemberProfilePersistencePort.save(target);

		AgitBan ban = AgitBan.create(
				agit.getId(),
				target.getUserUuid(),
				target.getId(),
				target.getNickname()
		);
		agitBanPersistencePort.save(ban);

		Map<String, Object> banPayload = new LinkedHashMap<>();
		banPayload.put("agitUuid", agit.getAgitUuid().toString());
		banPayload.put("userUuid", target.getUserUuid().toString());
		banPayload.put("nickname", target.getNickname());
		publishEvent(AgitEventTopics.MEMBER_BANNED, agit.getAgitUuid(), banPayload);
		scheduleReadModelRefresh(agit.getAgitUuid());
		scheduleMembershipRemove(agit.getAgitUuid(), target.getUserUuid());
	}

	/**
	 * 아지트에서 나간다. HOST는 ACTIVE 인원이 본인 1명일 때만 가능하며, 이때 아지트를 소프트 삭제한다.
	 */
	@Override
	@Transactional
	public void leaveAgit(UUID agitUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);

		Agit agit = requireActiveAgit(agitUuid);
		AgitMemberProfile profile = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agit.getId(), actorUserUuid)
				.orElseThrow(AgitMemberNotFoundException::new);

		if (profile.getStatus() == AgitMemberStatus.LEFT
				|| profile.getStatus() == AgitMemberStatus.BANNED) {
			return;
		}

		if (profile.getRole() == AgitMemberRole.HOST) {
			long activeCount = agitMemberProfilePersistencePort.countActiveByAgitId(agit.getId());
			if (activeCount != 1) {
				throw new HostCannotLeaveException();
			}
			profile.leave();
			agitMemberProfilePersistencePort.save(profile);
			agit.delete();
			agitPersistencePort.save(agit);

			Map<String, Object> deletedPayload = new LinkedHashMap<>();
			deletedPayload.put("agitUuid", agit.getAgitUuid().toString());
			deletedPayload.put("userUuid", actorUserUuid.toString());
			publishEvent(AgitEventTopics.DELETED, agit.getAgitUuid(), deletedPayload);
			scheduleReadModelRefresh(agit.getAgitUuid());
			scheduleMembershipDeleteAll(agit.getAgitUuid());
			return;
		}

		profile.leave();
		agitMemberProfilePersistencePort.save(profile);

		Map<String, Object> leftPayload = new LinkedHashMap<>();
		leftPayload.put("agitUuid", agit.getAgitUuid().toString());
		leftPayload.put("userUuid", actorUserUuid.toString());
		publishEvent(AgitEventTopics.MEMBER_LEFT, agit.getAgitUuid(), leftPayload);
		scheduleReadModelRefresh(agit.getAgitUuid());
		scheduleMembershipRemove(agit.getAgitUuid(), actorUserUuid);
	}

	/**
	 * 밴을 해제한다. 성공 시 profile status는 항상 LEFT(멱등).
	 */
	@Override
	@Transactional
	public void unbanMember(UUID agitUuid, UUID targetUserUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		if (targetUserUuid == null) {
			throw new IllegalArgumentException("대상 사용자 UUID는 필수입니다.");
		}

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), actorUserUuid);

		AgitMemberProfile profile = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agit.getId(), targetUserUuid)
				.orElseThrow(AgitMemberNotFoundException::new);

		if (profile.getStatus() == AgitMemberStatus.LEFT) {
			return;
		}
		if (profile.getStatus() != AgitMemberStatus.BANNED) {
			throw new IllegalStateException("밴 상태가 아닌 멤버는 해제할 수 없습니다.");
		}

		agitBanPersistencePort.findActiveByAgitIdAndUserUuid(agit.getId(), targetUserUuid)
				.ifPresent(ban -> {
					ban.unban(LocalDateTime.now());
					agitBanPersistencePort.save(ban);
				});

		profile.unbanToLeft();
		agitMemberProfilePersistencePort.save(profile);

		Map<String, Object> unbanPayload = new LinkedHashMap<>();
		unbanPayload.put("agitUuid", agit.getAgitUuid().toString());
		unbanPayload.put("userUuid", targetUserUuid.toString());
		publishEvent(AgitEventTopics.MEMBER_UNBANNED, agit.getAgitUuid(), unbanPayload);
		scheduleReadModelRefresh(agit.getAgitUuid());
		scheduleMembershipRemove(agit.getAgitUuid(), targetUserUuid);
	}

	/**
	 * 아지트 메타(제목·소개·정원·섬네일)를 수정한다. 생성 검증과 동일하며, 정원은 현재 ACTIVE 인원 이상이어야 한다.
	 */
	@Override
	@Transactional
	public UpdateAgitResultDto updateAgit(UUID agitUuid, UpdateAgitRequestDto requestDto) {
		requireActorUserUuid(requestDto.getUserUuid());

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), requestDto.getUserUuid());

		long currentMemberCount = agitMemberProfilePersistencePort.countActiveByAgitId(agit.getId());
		if (requestDto.getMaximumCapacity() != null
				&& requestDto.getMaximumCapacity() < currentMemberCount) {
			throw new CapacityBelowCurrentException(currentMemberCount);
		}

		agit.updateMeta(
				requestDto.getAgitName(),
				requestDto.getDescription(),
				requestDto.getMaximumCapacity(),
				requestDto.getThumbnailPath(),
				resolveAllowedMaxCapacity()
		);
		Agit saved = agitPersistencePort.save(agit);

		Map<String, Object> updatedPayload = new LinkedHashMap<>();
		updatedPayload.put("agitUuid", saved.getAgitUuid().toString());
		updatedPayload.put("agitName", saved.getAgitName());
		updatedPayload.put("description", saved.getDescription());
		updatedPayload.put("maximumCapacity", saved.getMaximumCapacity());
		updatedPayload.put("thumbnailPath", saved.getThumbnailPath());
		publishEvent(AgitEventTopics.UPDATED, saved.getAgitUuid(), updatedPayload);
		scheduleReadModelRefresh(saved.getAgitUuid());

		return UpdateAgitResultDto.builder()
				.agitUuid(saved.getAgitUuid())
				.agitName(saved.getAgitName())
				.description(saved.getDescription())
				.maximumCapacity(saved.getMaximumCapacity())
				.thumbnailPath(saved.getThumbnailPath())
				.build();
	}

	/**
	 * 아지트장 권한을 ACTIVE GUEST(ampId)에게 위임한다.
	 */
	@Override
	@Transactional
	public void transferHost(UUID agitUuid, Long ampId, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		if (ampId == null) {
			throw new IllegalArgumentException("멤버 프로필 ID는 필수입니다.");
		}

		Agit agit = requireActiveAgit(agitUuid);
		AgitMemberProfile currentHost = requireActiveHost(agit.getId(), actorUserUuid);

		if (ampId.equals(currentHost.getId())) {
			throw new IllegalArgumentException("자기 자신에게 방장을 위임할 수 없습니다.");
		}

		AgitMemberProfile target = agitMemberProfilePersistencePort.findById(ampId)
				.orElseThrow(AgitMemberNotFoundException::new);
		if (!agit.getId().equals(target.getAgitId())) {
			throw new AgitMemberNotFoundException();
		}
		if (target.getStatus() != AgitMemberStatus.ACTIVE
				|| target.getRole() != AgitMemberRole.GUEST) {
			throw new InvalidTransferTargetException();
		}

		currentHost.demoteToGuest();
		target.promoteToHost();
		agitMemberProfilePersistencePort.save(currentHost);
		agitMemberProfilePersistencePort.save(target);

		Map<String, Object> transferPayload = new LinkedHashMap<>();
		transferPayload.put("agitUuid", agit.getAgitUuid().toString());
		transferPayload.put("previousHostUserUuid", currentHost.getUserUuid().toString());
		transferPayload.put("newHostUserUuid", target.getUserUuid().toString());
		transferPayload.put("newHostNickname", target.getNickname());
		publishEvent(AgitEventTopics.HOST_TRANSFERRED, agit.getAgitUuid(), transferPayload);
		scheduleReadModelRefresh(agit.getAgitUuid());
		scheduleMembershipPut(agit.getAgitUuid(), currentHost.getUserUuid(), AgitMemberRole.GUEST);
		scheduleMembershipPut(agit.getAgitUuid(), target.getUserUuid(), AgitMemberRole.HOST);
	}

	/**
	 * 초대 코드를 재발급한다. 호출마다 새 코드를 발급한다. 연타 방지는 FE에서 처리한다.
	 */
	@Override
	@Transactional
	public ReissueInviteCodeResultDto reissueInviteCode(UUID agitUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveHost(agit.getId(), actorUserUuid);

		String previousCode = agit.getCode();
		String newCode = generateUniqueCode();
		agit.reissueCode(newCode);
		Agit saved = agitPersistencePort.save(agit);

		Map<String, Object> reissuePayload = new LinkedHashMap<>();
		reissuePayload.put("agitUuid", saved.getAgitUuid().toString());
		reissuePayload.put("previousCode", previousCode);
		reissuePayload.put("code", saved.getCode());
		publishEvent(AgitEventTopics.INVITE_CODE_REISSUED, saved.getAgitUuid(), reissuePayload);
		scheduleReadModelRefresh(saved.getAgitUuid());

		return ReissueInviteCodeResultDto.builder()
				.code(saved.getCode())
				.build();
	}

	/**
	 * 접속 유저가 ACTIVE로 속한 아지트 목록을 Mongo 읽기 문서에서 조회한다.
	 *
	 * 정렬: updatedAt DESC (임시). 이후 최근 토픽순으로 교체한다.
	 */
	@Override
	public List<MyAgitItemDto> listMyAgits(UUID userUuid) {
		requireActorUserUuid(userUuid);

		List<ActiveMembershipAgit> rows =
				agitReadPersistencePort.findActiveByMemberUserUuid(userUuid);

		return rows.stream()
				.map(row -> MyAgitItemDto.builder()
						.agitUuid(row.agitUuid())
						.agitName(row.agitName())
						.build())
				.toList();
	}

	/**
	 * 접속 유저의 아지트 멤버 프로필(닉네임·이미지)을 부분 수정한다. ACTIVE만 허용.
	 *
	 */
	@Override
	@Transactional
	public UpdateMyMemberProfileResultDto updateMyMemberProfile(
			UUID agitUuid,
			UpdateMyMemberProfileRequestDto requestDto
	) {
		requireActorUserUuid(requestDto.getUserUuid());

		Agit agit = requireActiveAgit(agitUuid);
		AgitMemberProfile profile = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agit.getId(), requestDto.getUserUuid())
				.orElseThrow(AgitMemberNotFoundException::new);

		if (profile.getStatus() != AgitMemberStatus.ACTIVE) {
			throw new AgitMemberNotActiveException();
		}

		profile.updateProfile(requestDto.getNickname(), requestDto.getProfileImagePath());
		AgitMemberProfile saved = agitMemberProfilePersistencePort.save(profile);

		Map<String, Object> profilePayload = new LinkedHashMap<>();
		profilePayload.put("agitUuid", agit.getAgitUuid().toString());
		profilePayload.put("userUuid", saved.getUserUuid().toString());
		profilePayload.put("nickname", saved.getNickname());
		profilePayload.put("profileImagePath", saved.getProfileImagePath());
		publishEvent(AgitEventTopics.MEMBER_PROFILE_UPDATED, agit.getAgitUuid(), profilePayload);
		scheduleReadModelRefresh(agit.getAgitUuid());

		return UpdateMyMemberProfileResultDto.builder()
				.nickname(saved.getNickname())
				.profileImagePath(saved.getProfileImagePath())
				.build();
	}

	private Agit requireActiveAgit(UUID agitUuid) {
		if (agitUuid == null) {
			throw new IllegalArgumentException("아지트 UUID는 필수입니다.");
		}
		Agit agit = agitPersistencePort.findByAgitUuid(agitUuid)
				.orElseThrow(AgitNotFoundException::new);
		if (agit.getStatus() != AgitStatus.ACTIVE) {
			throw new AgitNotFoundException();
		}
		return agit;
	}

	private AgitMemberProfile requireActiveHost(Long agitId, UUID actorUserUuid) {
		AgitMemberProfile actor = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agitId, actorUserUuid)
				.orElseThrow(NotAgitHostException::new);
		if (actor.getRole() != AgitMemberRole.HOST || actor.getStatus() != AgitMemberStatus.ACTIVE) {
			throw new NotAgitHostException();
		}
		return actor;
	}

	private void requireActorUserUuid(UUID actorUserUuid) {
		if (actorUserUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}
	}

	private void scheduleMembershipPut(UUID agitUuid, UUID userUuid, AgitMemberRole role) {
		runAfterCommit(() -> agitMembershipCachePort.put(agitUuid, userUuid, role));
	}

	private void scheduleMembershipRemove(UUID agitUuid, UUID userUuid) {
		runAfterCommit(() -> agitMembershipCachePort.remove(agitUuid, userUuid));
	}

	private void scheduleMembershipDeleteAll(UUID agitUuid) {
		runAfterCommit(() -> agitMembershipCachePort.deleteAll(agitUuid));
	}

	private void runAfterCommit(Runnable action) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					action.run();
				}
			});
			return;
		}
		action.run();
	}

	private void scheduleReadModelRefresh(UUID agitUuid) {
		Runnable refresh = () -> {
			try {
				refreshAgitReadModelUseCase.refresh(agitUuid);
			} catch (Exception e) {
				log.warn("아지트 읽기 모델 갱신 실패 (쓰기는 완료됨) agitUuid={}: {}", agitUuid, e.getMessage());
			}
		};
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					refresh.run();
				}
			});
			return;
		}
		refresh.run();
	}

	private void publishEvent(String topic, UUID agitUuid, Map<String, Object> payload) {
		payload.put("occurredAt", LocalDateTime.now().toString());
		try {
			eventPublisherPort.publish(topic, agitUuid.toString(), objectMapper.writeValueAsString(payload));
		} catch (JsonProcessingException e) {
			log.warn("{} 이벤트 직렬화 실패 (쓰기는 완료됨): {}", topic, e.getMessage());
		} catch (Exception e) {
			log.warn("{} 이벤트 발행 실패 (쓰기는 완료됨): {}", topic, e.getMessage());
		}
	}

	private String normalizeInviteCode(String code) {
		if (code == null || code.isBlank()) {
			throw new InvalidInviteCodeException("초대 코드는 필수입니다.");
		}
		String normalized = code.trim().toUpperCase(Locale.ROOT);
		if (!CODE_PATTERN.matcher(normalized).matches()) {
			throw new InvalidInviteCodeException("초대 코드는 영문 대문자와 숫자로 구성된 6자여야 합니다.");
		}
		return normalized;
	}

	/**
	 * 아이템 보유·결제 연동 전: 기본 허용 최대 인원(5).
	 * 이후 outbound port로 판정한 값을 반환하도록 교체한다.
	 */
	private int resolveAllowedMaxCapacity() {
		return Agit.DEFAULT_MAX_CAPACITY;
	}

	private String generateUniqueCode() {
		for (int attempt = 0; attempt < CODE_GENERATE_MAX_ATTEMPTS; attempt++) {
			String code = generateCode();
			if (!agitPersistencePort.existsByCode(code)) {
				return code;
			}
		}
		throw new IllegalStateException("초대 코드 생성에 실패했습니다. 다시 시도해 주세요.");
	}

	private String generateCode() {
		StringBuilder builder = new StringBuilder(Agit.CODE_LENGTH);
		for (int i = 0; i < Agit.CODE_LENGTH; i++) {
			int index = secureRandom.nextInt(CODE_ALPHABET.length());
			builder.append(CODE_ALPHABET.charAt(index));
		}
		return builder.toString();
	}
}
