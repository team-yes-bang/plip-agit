package com.plip.agit.application.service;

import com.plip.agit.application.exception.AgitAlreadyJoinedException;
import com.plip.agit.application.exception.AgitBannedException;
import com.plip.agit.application.exception.AgitCapacityExceededException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.exception.InvalidInviteCodeException;
import com.plip.agit.application.port.in.AgitUseCase;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.in.dto.JoinAgitRequestDto;
import com.plip.agit.application.port.in.dto.JoinAgitResultDto;
import com.plip.agit.application.port.out.AgitBanPersistencePort;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitBan;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberRole;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.domain.model.AgitStatus;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * 초대 코드로 랜딩 표시용 아지트 정보를 조회한다.
	 *
	 * <p>TODO(read-model):
	 * Document — 랜딩 비정규화 필드(제목·소개·인원·HOST 닉네임·섬네일 등)로 읽기 경로 교체.
	 * Redis — code → landing 캐시(선택) + 무효화.
	 * 동기화 — 입장/퇴장·위임·메타 수정·삭제 시 document(·캐시) 갱신.
	 * TODO(prod): Gateway 미사용. K8s Ingress(+ Service 앞단)에서 이 GET에 IP rate limit
	 * (권장 60/min, burst 10~20/10s). 테스트용 Gateway whitelist와 별개.
	 * TODO(currentMemberCount): 현재는 MySQL ACTIVE COUNT. 이후 NoSQL document 필드로 교체.
	 */
	@Override
	public AgitLandingResultDto getLandingByCode(String code) {
		String normalizedCode = normalizeInviteCode(code);

		Agit agit = agitPersistencePort.findActiveByCode(normalizedCode)
				.orElseThrow(AgitNotFoundException::new);

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
	 *
	 * <p>TODO(auth): userUuid는 Gateway/JWT에서 추출하도록 교체한다.
	 * TODO(side-effect): join 후 document/캐시 갱신 및 MemberJoined 이벤트 발행.
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
			profile.rejoin(requestDto.getNickname(), requestDto.getProfileImagePath());
			savedProfile = agitMemberProfilePersistencePort.save(profile);
		}

		return JoinAgitResultDto.builder()
				.agitUuid(agit.getAgitUuid())
				.ampId(savedProfile.getId())
				.nickname(savedProfile.getNickname())
				.profileImagePath(savedProfile.getProfileImagePath())
				.role(savedProfile.getRole())
				.build();
	}

	/**
	 * 아지트장이 멤버를 내보낸다. ampId로 대상을 찾고, 이후 식별은 userUuid.
	 *
	 * <p>TODO(auth): actorUserUuid는 Gateway/JWT에서 추출하도록 교체한다.
	 * TODO(side-effect): ban 후 document/캐시 갱신 및 MemberBanned 이벤트 발행.
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

		target.ban();
		agitMemberProfilePersistencePort.save(target);

		AgitBan ban = AgitBan.create(
				agit.getId(),
				target.getUserUuid(),
				target.getId(),
				target.getNickname()
		);
		agitBanPersistencePort.save(ban);
	}

	/**
	 * 아지트에서 나간다. HOST는 ACTIVE 인원이 본인 1명일 때만 가능하며, 이때 아지트를 소프트 삭제한다.
	 *
	 * <p>TODO(auth): actorUserUuid는 Gateway/JWT에서 추출하도록 교체한다.
	 * TODO(side-effect): leave/삭제 후 document/캐시 갱신 및 이벤트 발행.
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
				throw new IllegalStateException("방장은 다른 ACTIVE 멤버가 없을 때만 나갈 수 있습니다.");
			}
			profile.leave();
			agitMemberProfilePersistencePort.save(profile);
			agit.delete();
			agitPersistencePort.save(agit);
			return;
		}

		profile.leave();
		agitMemberProfilePersistencePort.save(profile);
	}

	/**
	 * 밴을 해제한다. 성공 시 profile status는 항상 LEFT(멱등).
	 *
	 * <p>TODO(auth): actorUserUuid는 Gateway/JWT에서 추출하도록 교체한다.
	 * TODO(side-effect): unban 후 document/캐시 갱신 및 MemberUnbanned 이벤트 발행.
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
				.orElseThrow(() -> new IllegalStateException("아지트장만 수행할 수 있습니다."));
		if (actor.getRole() != AgitMemberRole.HOST || actor.getStatus() != AgitMemberStatus.ACTIVE) {
			throw new IllegalStateException("아지트장만 수행할 수 있습니다.");
		}
		return actor;
	}

	private void requireActorUserUuid(UUID actorUserUuid) {
		if (actorUserUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
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
