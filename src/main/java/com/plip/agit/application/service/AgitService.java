package com.plip.agit.application.service;

import com.plip.agit.application.port.in.AgitUseCase;
import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitService implements AgitUseCase {

	private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int CODE_GENERATE_MAX_ATTEMPTS = 10;

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
