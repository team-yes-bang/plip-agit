package com.plip.agit.application.service;

import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.exception.InvalidInviteCodeException;
import com.plip.agit.application.port.in.AgitUseCase;
import com.plip.agit.application.port.in.dto.AgitLandingResultDto;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import java.security.SecureRandom;
import java.util.Locale;
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
