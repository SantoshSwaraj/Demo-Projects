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
public class AvgResponseTimeForTicketCategoryModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ticket_id")
	private Long ticketId;

	@Column(name = "tenant_id")
	private Long tenantId;

	@Column(name = "category")
	private Long category;

	@Column(name = "status")
	private Integer status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "closed_date")
	private LocalDateTime closedDate;
	
	@Formula("IFNULL(ROUND(SUM(TIMESTAMPDIFF(SECOND,created_date,closed_date))/ COUNT(*)), 0)")
	private Long timeDiffCount;
}
