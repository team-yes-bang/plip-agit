package com.plip.agit.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
		name = "agit_bans",
		indexes = @Index(name = "idx_agit_bans_agit_user", columnList = "agit_id, user_uuid")
)
public class AgitBanEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agit_id", nullable = false)
	private Long agitId;

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "user_uuid", nullable = false, length = 16)
	private UUID userUuid;

	@Column(name = "amp_id", nullable = false)
	private Long ampId;

	@Column(name = "banned_nickname", nullable = false, length = 100)
	private String bannedNickname;

	@Column(name = "banned_at", nullable = false)
	private LocalDateTime bannedAt;

	@Column(name = "unbanned_at")
	private LocalDateTime unbannedAt;

	@Builder
	private AgitBanEntity(
			Long agitId,
			UUID userUuid,
			Long ampId,
			String bannedNickname,
			LocalDateTime bannedAt,
			LocalDateTime unbannedAt
	) {
		this.agitId = agitId;
		this.userUuid = userUuid;
		this.ampId = ampId;
		this.bannedNickname = bannedNickname;
		this.bannedAt = bannedAt;
		this.unbannedAt = unbannedAt;
	}

	void applyUnbannedAt(LocalDateTime unbannedAt) {
		this.unbannedAt = unbannedAt;
	}
}
