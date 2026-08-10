package com.plip.agit.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
		name = "agit_member_profiles",
		uniqueConstraints = @UniqueConstraint(name = "uq_agit_user", columnNames = {"agit_id", "user_uuid"})
)
public class AgitMemberProfileEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "agit_id", nullable = false)
	private Long agitId;

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "user_uuid", nullable = false, length = 16)
	private UUID userUuid;

	@Column(name = "nickname", nullable = false, length = 100)
	private String nickname;

	@Column(name = "profile_image_path", length = 255)
	private String profileImagePath;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private AgitMemberStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private AgitMemberRole role;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "apply_items", columnDefinition = "json")
	private String applyItems;

	@Builder
	private AgitMemberProfileEntity(
			Long agitId,
			UUID userUuid,
			String nickname,
			String profileImagePath,
			AgitMemberStatus status,
			AgitMemberRole role,
			String applyItems
	) {
		this.agitId = agitId;
		this.userUuid = userUuid;
		this.nickname = nickname;
		this.profileImagePath = profileImagePath;
		this.status = status != null ? status : AgitMemberStatus.ACTIVE;
		this.role = role != null ? role : AgitMemberRole.GUEST;
		this.applyItems = applyItems;
	}
}
