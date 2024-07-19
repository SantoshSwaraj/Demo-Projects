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
public class AgeOfTicketModel {

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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reopened_date")
	private LocalDateTime reOpenedDate;
	
	@Formula("count(distinct ticket_id)")
	private Long ticketIdCount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Formula("MIN(created_date)")
	private LocalDateTime minCreatedDate;
}
