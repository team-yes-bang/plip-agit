package com.plip.agit.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "agits")
public class AgitEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "agit_uuid", nullable = false, unique = true, length = 16)
	private UUID agitUuid;

	@Column(name = "agit_name", nullable = false, length = 20)
	private String agitName;

	@Column(name = "description", nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(name = "maximum_capacity", nullable = false)
	private Integer maximumCapacity;

	@Column(name = "code", nullable = false, unique = true, length = 6, columnDefinition = "CHAR(6)")
	private String code;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private AgitStatus status;

	@Builder
	private AgitEntity(
			UUID agitUuid,
			String agitName,
			String description,
			Integer maximumCapacity,
			String code,
			AgitStatus status
	) {
		this.agitUuid = agitUuid;
		this.agitName = agitName;
		this.description = description;
		this.maximumCapacity = maximumCapacity;
		this.code = code;
		this.status = status != null ? status : AgitStatus.ACTIVE;
	}
}
