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
import lombok.Data;

@Entity
@Table(name = "ticket")
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

	@Column(name = "threat_count")
	private Long threatCount;

	@Column(name = "status")
	private Integer status;

	@Column(name = "priority")
	private Long priority;

	@Column(name = "category")
	private Long category;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reopened_date")
	private LocalDateTime reOpenedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "closed_date")
	private LocalDateTime closedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
}
