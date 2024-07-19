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
@Table(name = "investigation_save")
@AllArgsConstructor
@NoArgsConstructor
public class InvestigationSave {

	@Id
	@Column(name = "investigation_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long investigationId;

	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "closed_date")
	private LocalDateTime closedDate;

	@Formula("count(investigation_id)")
	private Long investigationIdCount;
	
	@Formula("IFNULL(SUM(TIMESTAMPDIFF(SECOND,created_date,closed_date)), 0)")
	private Long timeDiff;

	@Formula("IFNULL(HOUR(closed_date), 0)")
	private Long hourClosedDate;
}
