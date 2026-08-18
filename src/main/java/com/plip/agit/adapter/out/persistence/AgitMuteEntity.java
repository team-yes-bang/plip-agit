package com.plip.agit.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
		name = "agit_mutes",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_agit_mutes_pair",
				columnNames = {"agit_id", "muter_uuid", "muted_uuid"}
		)
)
public class AgitMuteEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agit_id", nullable = false)
	private Long agitId;

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "muter_uuid", nullable = false, length = 16)
	private UUID muterUuid;

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "muted_uuid", nullable = false, length = 16)
	private UUID mutedUuid;

	@Builder
	private AgitMuteEntity(Long agitId, UUID muterUuid, UUID mutedUuid) {
		this.agitId = agitId;
		this.muterUuid = muterUuid;
		this.mutedUuid = mutedUuid;
	}
}
