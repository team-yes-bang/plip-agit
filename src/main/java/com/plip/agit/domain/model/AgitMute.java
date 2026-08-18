package com.plip.agit.domain.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgitMute {

	private Long id;
	private Long agitId;
	private UUID muterUuid;
	private UUID mutedUuid;

	public static AgitMute create(Long agitId, UUID muterUuid, UUID mutedUuid) {
		if (agitId == null) {
			throw new IllegalArgumentException("아지트 ID는 필수입니다.");
		}
		if (muterUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}
		if (mutedUuid == null) {
			throw new IllegalArgumentException("뮤트 대상 사용자 UUID는 필수입니다.");
		}
		if (muterUuid.equals(mutedUuid)) {
			throw new IllegalArgumentException("자기 자신은 뮤트할 수 없습니다.");
		}

		return AgitMute.builder()
				.agitId(agitId)
				.muterUuid(muterUuid)
				.mutedUuid(mutedUuid)
				.build();
	}

	public static AgitMute reconstitute(Long id, Long agitId, UUID muterUuid, UUID mutedUuid) {
		return AgitMute.builder()
				.id(id)
				.agitId(agitId)
				.muterUuid(muterUuid)
				.mutedUuid(mutedUuid)
				.build();
	}

	@Builder(access = AccessLevel.PRIVATE)
	private AgitMute(Long id, Long agitId, UUID muterUuid, UUID mutedUuid) {
		this.id = id;
		this.agitId = agitId;
		this.muterUuid = muterUuid;
		this.mutedUuid = mutedUuid;
	}
}
