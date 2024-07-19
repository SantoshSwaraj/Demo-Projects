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
import lombok.Data;

@Entity
@Table(name = "ticket")
@Data
public class TicketWithOutFormula {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ticket_id")
	private Long ticketId;

	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "status")
	private Integer status;

	@Column(name = "priority")
	private Long priority;

	@Column(name = "category")
	private Long category;

	@Column(name = "is_incident")
	private boolean incident;
	
	@Column(name = "is_relevant")
	private boolean relevant;
	
	@Column(name = "is_whitelist")
	private boolean whitelist;
	
	@Column(name = "is_info")
	private boolean info;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reopened_date")
	private LocalDateTime reOpenedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "closed_date")
	private LocalDateTime closedDate;
	
	@Formula("min(created_date)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime minCreatedDate;
	
	@Formula("max(created_date)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime maxCreatedDate;

	@Formula("min(reopened_date)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime minReOpenedDate;
	
	@Formula("max(reopened_date)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime maxReOpenedDate;
	
	@Formula("min(closed_date)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime minClosedDate;
	
	@Formula("max(closed_date)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime maxClosedDate;

	@Formula("count(distinct ticket_id)")
	private Long ticketIdCount;
}
