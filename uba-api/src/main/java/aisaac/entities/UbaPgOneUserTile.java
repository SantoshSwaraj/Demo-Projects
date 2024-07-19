package aisaac.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

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
@Data
@Entity
@Table(name = "uba_pg1_user_tile")
@AllArgsConstructor
@NoArgsConstructor
public class UbaPgOneUserTile {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name = "rec_id",nullable = false,unique = true)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "ad_user_id")
	private Integer adUserId;
	
	@Column(name = "account_name")
	private String accountName;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "department_name")
	private String departmentName;
	
	@Column(name = "sources")
	private String sources;
	
	@Column(name = "score")
	private Float score;
	
	@Column(name = "is_watchlisted")
	private Boolean watchlisted;
	
	@Column(name = "watchlisted_by")
	private Integer watchlistedBy;

	@Column(name = "score_diff")
	private Float scoreDiff;
	
	@Column(name = "user_profile_score")
	private Float userProfileScore;
	
	@Column(name = "anomaly_count")
	private Integer anomalyCount;
	
	@Column(name = "alert_count")
	private Integer alertCount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "watchlisted_date")
	private LocalDateTime watchlistedDate;
	
	@Formula("group_concat(distinct sources)")
	private String groupSources;
}
