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

@Entity
@Table(name = "ticket")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ticket_id")
	private Long ticketId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "ticket_name")
	private String ticketName;
	
	@Column(name = "assignee_user_id")
	private Long assigneeUserId;
	
	@Column(name = "category")
	private Long category;
	
	@Column(name = "priority")
	private Long priority;
	
	@Column(name = "status")
	private Long status;

	@Column(name = "description")
	private String description;
	
	@Column(name = "created_user_id")
	private Long createdUserId;
	
	@Column(name = "created_rule_id")
	private Long createdRuleId;

	@Column(name = "updated_by")
	private Long updatedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reopened_date")
	private LocalDateTime reopenedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	
}
