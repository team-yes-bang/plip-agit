package com.plip.agit.adapter.out.persistence;

import java.util.UUID;

/**
 * ACTIVE 멤버십 기준 내 아지트 목록 조회 투영.
 */
public record MyAgitListRow(UUID agitUuid, String agitName) {
}
