package com.plip.agit.application.port.in;

import com.plip.agit.application.port.in.dto.MuteItemDto;
import java.util.List;
import java.util.UUID;

public interface AgitMuteUseCase {

	void muteMember(UUID agitUuid, UUID mutedUuid, UUID actorUserUuid);

	void unmuteMember(UUID agitUuid, UUID mutedUuid, UUID actorUserUuid);

	List<MuteItemDto> listMyMutes(UUID agitUuid, UUID actorUserUuid);
}
