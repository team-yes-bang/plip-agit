package com.plip.agit.adapter.out.persistence.mongodb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "agit_read_models")
@CompoundIndexes({
		@CompoundIndex(name = "idx_members_userUuid", def = "{'members.userUuid': 1}"),
		@CompoundIndex(name = "idx_status", def = "{'status': 1}")
})
public class AgitReadDocument {

	@Id
	private String id;

	private String agitName;
	private String description;
	private String thumbnailPath;

	@Indexed(unique = true)
	private String code;

	private String status;
	private int maximumCapacity;
	private List<AgitReadMemberDocument> members = new ArrayList<>();
	private Instant updatedAt;

	public AgitReadDocument(
			String id,
			String agitName,
			String description,
			String thumbnailPath,
			String code,
			String status,
			int maximumCapacity,
			List<AgitReadMemberDocument> members,
			Instant updatedAt
	) {
		this.id = id;
		this.agitName = agitName;
		this.description = description;
		this.thumbnailPath = thumbnailPath;
		this.code = code;
		this.status = status;
		this.maximumCapacity = maximumCapacity;
		this.members = members != null ? members : new ArrayList<>();
		this.updatedAt = updatedAt;
	}
}
