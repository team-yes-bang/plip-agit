package com.plip.agit.application.port.out;

import java.util.UUID;

/**
 * ACTIVE 멤버십으로 속한 아지트 요약 (읽기 문서 목록 조회용).
 */
public record ActiveMembershipAgit(UUID agitUuid, String agitName) {
}
