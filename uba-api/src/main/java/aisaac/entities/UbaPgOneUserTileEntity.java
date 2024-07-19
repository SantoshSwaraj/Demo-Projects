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

@Data
@Entity
@Table(name = "uba_pg1_user_tile")
@AllArgsConstructor
@NoArgsConstructor
public class UbaPgOneUserTileEntity {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	@Column(name = "rec_id",nullable = false,unique = true)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "account_name")
	private String accountName;
	
	@Column(name = "score")
	private Float score;
	
	@Column(name = "is_watchlisted")
	private Boolean watchlisted;
	
	@Column(name = "score_diff")
	private Float scoreDiff;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
}
