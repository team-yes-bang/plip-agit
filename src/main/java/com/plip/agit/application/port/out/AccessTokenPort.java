package com.plip.agit.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface AccessTokenPort {

	Optional<UUID> parseAccessToken(String token);
}
