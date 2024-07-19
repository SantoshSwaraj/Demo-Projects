package aisaac.entities;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "adr_entity_score")
public class AdrEntityScore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id",nullable = false,unique = true)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "entity_id")
	private String entityId;
	
	@Column(name = "entity_type_id")
	private Long entityTypeId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Column(name = "entity_score")
	private Float entityScore;
	
	@Column(name = "entity_profile_score")
	private Float entityProfileScore;
	
	@Column(name = "entity_score_diff")
	private Float entityScoreDiff;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "profile_score_updated_date")
	private LocalDateTime profileScoreUpdatedDate;
	
	@Column(name = "is_watchlisted")
	private Boolean watchlisted;

	@Column(name = "watchlisted_by")
	private Long watchlistedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "watchlisted_date")
	private LocalDateTime watchlistedDate;
}
