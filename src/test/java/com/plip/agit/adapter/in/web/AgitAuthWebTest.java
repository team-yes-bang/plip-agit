package com.plip.agit.adapter.in.web;

import com.plip.agit.adapter.out.security.JwtSigningKeys;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgitAuthWebTest {

	private static final String SECRET = "test-jwt-secret-key-for-unit-tests-only";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listMyAgits_withoutToken_returns401() throws Exception {
		mockMvc.perform(get("/api/v1/agits/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("ACCESS_TOKEN_INVALID"));
	}

	@Test
	void listMyAgits_withAccessToken_returns200() throws Exception {
		UUID userUuid = UUID.fromString("018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e");
		mockMvc.perform(get("/api/v1/agits/me")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken(userUuid)))
				.andExpect(status().isOk());
	}

	@Test
	void getLanding_withoutToken_isNotUnauthorized() throws Exception {
		mockMvc.perform(get("/api/v1/agits/ABCDEF/landing"))
				.andExpect(status().isNotFound());
	}

	private static String accessToken(UUID userUuid) {
		Date now = new Date();
		return Jwts.builder()
				.subject(userUuid.toString())
				.claim("user_uuid", userUuid.toString())
				.claim("tokenType", "access")
				.issuedAt(now)
				.expiration(new Date(now.getTime() + 3_600_000L))
				.signWith(JwtSigningKeys.hmacSha256(SECRET))
				.compact();
	}
}
