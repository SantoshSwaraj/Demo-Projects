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
@Table(name = "audit_trail")
public class AuditTrail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	private Long recId;
	
	@Column(name = "audit_trail_master_id")
	private Long auditTrailMasterId;
	
	@Column(name = "module_rec_id")
	private Long moduleRecId;
	
	@Column(name = "details")
	private String details;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "created_by")
	private Long createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

}
