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
@Table(name = "threat_responsespeed_hour")
@AllArgsConstructor
@NoArgsConstructor
public class ThreatResponsespeedHour {

	@Id
	@Column(name = "rec_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "validation_responsetime")
	private Long validationResponsetime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Formula("IFNULL(HOUR(created_date), 0)")
	private Long hourCreatedDate;
}
