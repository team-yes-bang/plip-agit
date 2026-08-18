package com.plip.agit.adapter.out.persistence;

import com.plip.agit.adapter.out.persistence.mapper.AgitMuteEntityMapper;
import com.plip.agit.application.port.out.AgitMutePersistencePort;
import com.plip.agit.domain.model.AgitMute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgitMutePersistenceAdapter implements AgitMutePersistencePort {

	private final AgitMuteRepository agitMuteRepository;
	private final AgitMuteEntityMapper agitMuteEntityMapper;

	@Override
	@Transactional
	public AgitMute save(AgitMute mute) {
		AgitMuteEntity saved = agitMuteRepository.save(agitMuteEntityMapper.toEntity(mute));
		return agitMuteEntityMapper.toDomain(saved);
	}

	@Override
	public Optional<AgitMute> findByAgitIdAndMuterUuidAndMutedUuid(
			Long agitId,
			UUID muterUuid,
			UUID mutedUuid
	) {
		return agitMuteRepository
				.findByAgitIdAndMuterUuidAndMutedUuid(agitId, muterUuid, mutedUuid)
				.map(agitMuteEntityMapper::toDomain);
	}

	@Override
	public List<AgitMute> findAllByAgitIdAndMuterUuid(Long agitId, UUID muterUuid) {
		return agitMuteRepository.findAllByAgitIdAndMuterUuid(agitId, muterUuid).stream()
				.map(agitMuteEntityMapper::toDomain)
				.toList();
	}

	@Override
	@Transactional
	public void delete(AgitMute mute) {
		if (mute.getId() == null) {
			return;
		}
		agitMuteRepository.deleteById(mute.getId());
	}
}
