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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "audit_trail_master")
public class AuditTrailMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	private Long recId;
	
	@Column(name = "module_name", nullable = false)
	private String moduleName;
	
	@Column(name = "action_type", nullable = false)
	private String actionType;
	
	@Column(name = "action_desc", nullable = false)
	private String actionDesc;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date", nullable = true)
	private LocalDateTime updatedDate;

}
