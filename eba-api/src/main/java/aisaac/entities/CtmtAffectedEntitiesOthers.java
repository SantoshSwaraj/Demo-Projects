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
@Table(name = "ctmt_affected_entities_others")
@AllArgsConstructor
@NoArgsConstructor
public class CtmtAffectedEntitiesOthers {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rec_id")
	private Long recId;

	@Column(name = "tenant_id")
	private Long tenantId;

	@Column(name = "ticket_id")
	private Long ticketId;

	@Column(name = "resource_type")
	private String resourceType;

	@Column(name = "resource_name")
	private String resourceName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
}
