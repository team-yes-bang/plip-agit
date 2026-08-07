package com.plip.agit.application.port.in;

import com.plip.agit.application.port.in.dto.CreateAgitRequestDto;
import com.plip.agit.application.port.in.dto.CreateAgitResultDto;

public interface AgitUseCase {

	CreateAgitResultDto createAgit(CreateAgitRequestDto requestDto);
}
