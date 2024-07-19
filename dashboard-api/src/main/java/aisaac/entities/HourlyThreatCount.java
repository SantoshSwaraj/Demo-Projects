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
@Table(name = "hourly_threat_count")
@AllArgsConstructor
@NoArgsConstructor
public class HourlyThreatCount {

	@Id
	@Column(name = "rec_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "product_id")
	private Long productId;
	
	@Column(name = "product_vendor")
	private String productVendor;
	
	@Column(name = "product_name")
	private String productName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "interval_date")
	private LocalDateTime intervalDate;

	@Formula("IFNULL(SUM(count),0)")
	private Long sumCount;
}
