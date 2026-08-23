package com.plip.agit.adapter.in.web;

import com.plip.agit.global.web.RequestHeaders;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgitAuthWebTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listMyAgits_withoutUserUuidHeader_returns401() throws Exception {
		mockMvc.perform(get("/api/v1/agits/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("ACCESS_TOKEN_INVALID"));
	}

	@Test
	void listMyAgits_withUserUuidHeader_returns200() throws Exception {
		UUID userUuid = UUID.fromString("018f3f6e-8e2a-7b3c-9d4e-5f6a7b8c9d0e");
		mockMvc.perform(get("/api/v1/agits/me")
						.header(RequestHeaders.USER_UUID_HEADER, userUuid))
				.andExpect(status().isOk());
	}

	@Test
	void getLanding_withoutUserUuidHeader_isNotUnauthorized() throws Exception {
		mockMvc.perform(get("/api/v1/agits/ABCDEF/landing"))
				.andExpect(status().isNotFound());
	}
}
