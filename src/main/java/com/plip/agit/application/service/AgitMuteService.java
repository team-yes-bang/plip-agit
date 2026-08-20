package com.plip.agit.application.service;

import com.plip.agit.application.exception.AgitMemberNotActiveException;
import com.plip.agit.application.exception.AgitMemberNotFoundException;
import com.plip.agit.application.exception.AgitNotFoundException;
import com.plip.agit.application.exception.CannotMuteSelfException;
import com.plip.agit.application.port.in.AgitMuteUseCase;
import com.plip.agit.application.port.in.dto.MuteItemDto;
import com.plip.agit.application.port.out.AgitMemberProfilePersistencePort;
import com.plip.agit.application.port.out.AgitMutePersistencePort;
import com.plip.agit.application.port.out.AgitPersistencePort;
import com.plip.agit.domain.model.Agit;
import com.plip.agit.domain.model.AgitMemberProfile;
import com.plip.agit.domain.model.AgitMemberStatus;
import com.plip.agit.domain.model.AgitMute;
import com.plip.agit.domain.model.AgitStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitMuteService implements AgitMuteUseCase {

	private final AgitPersistencePort agitPersistencePort;
	private final AgitMemberProfilePersistencePort agitMemberProfilePersistencePort;
	private final AgitMutePersistencePort agitMutePersistencePort;

	/**
	 * 아지트 ACTIVE 멤버가 같은 아지트의 다른 ACTIVE 멤버를 뮤트한다. 이미 뮤트된 경우 멱등.
	 *
	 * <p>TODO(event): 뮤트 등록 후 agit.user-muted 발행 (payload: agitUuid, muterUuid, mutedUuid).
	 */
	@Override
	@Transactional
	public void muteMember(UUID agitUuid, UUID mutedUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		requireMutedUuid(mutedUuid);
		if (actorUserUuid.equals(mutedUuid)) {
			throw new CannotMuteSelfException();
		}

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveMember(agit.getId(), actorUserUuid);
		requireActiveMember(agit.getId(), mutedUuid);

		boolean alreadyMuted = agitMutePersistencePort
				.findByAgitIdAndMuterUuidAndMutedUuid(agit.getId(), actorUserUuid, mutedUuid)
				.isPresent();
		if (alreadyMuted) {
			return;
		}

		agitMutePersistencePort.save(AgitMute.create(agit.getId(), actorUserUuid, mutedUuid));
	}

	/**
	 * 본인이 건 뮤트를 해제한다. 뮤트 행이 없으면 멱등.
	 *
	 * <p>TODO(event): 뮤트 해제 후 agit.user-unmuted 발행 (payload: agitUuid, muterUuid, mutedUuid).
	 */
	@Override
	@Transactional
	public void unmuteMember(UUID agitUuid, UUID mutedUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);
		requireMutedUuid(mutedUuid);

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveMember(agit.getId(), actorUserUuid);

		agitMutePersistencePort
				.findByAgitIdAndMuterUuidAndMutedUuid(agit.getId(), actorUserUuid, mutedUuid)
				.ifPresent(agitMutePersistencePort::delete);
	}

	/**
	 * 접속 유저가 해당 아지트에서 뮤트한 사용자 UUID 목록을 반환한다.
	 */
	@Override
	public List<MuteItemDto> listMyMutes(UUID agitUuid, UUID actorUserUuid) {
		requireActorUserUuid(actorUserUuid);

		Agit agit = requireActiveAgit(agitUuid);
		requireActiveMember(agit.getId(), actorUserUuid);

		return agitMutePersistencePort.findAllByAgitIdAndMuterUuid(agit.getId(), actorUserUuid).stream()
				.map(mute -> MuteItemDto.builder().mutedUuid(mute.getMutedUuid()).build())
				.toList();
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

	private AgitMemberProfile requireActiveMember(Long agitId, UUID userUuid) {
		AgitMemberProfile profile = agitMemberProfilePersistencePort
				.findByAgitIdAndUserUuid(agitId, userUuid)
				.orElseThrow(AgitMemberNotFoundException::new);
		if (profile.getStatus() != AgitMemberStatus.ACTIVE) {
			throw new AgitMemberNotActiveException("ACTIVE 멤버만 뮤트할 수 있습니다.");
		}
		return profile;
	}

	private void requireActorUserUuid(UUID actorUserUuid) {
		if (actorUserUuid == null) {
			throw new IllegalArgumentException("사용자 UUID는 필수입니다.");
		}
	}

	private void requireMutedUuid(UUID mutedUuid) {
		if (mutedUuid == null) {
			throw new IllegalArgumentException("뮤트 대상 사용자 UUID는 필수입니다.");
		}
	}
}
