package com.plip.agit.application.port.in;

import java.util.UUID;

public interface RefreshAgitReadModelUseCase {

	void refresh(UUID agitUuid);
}
