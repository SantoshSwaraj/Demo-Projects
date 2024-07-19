package aisaac.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "threatlevel_stats_country_srcip_hour")
@AllArgsConstructor
@NoArgsConstructor
public class ThreatLevelStatsCountrySrcipHour {

	@Id
	@Column(name = "rec_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "country_code")
	private String countryCode;
	
	@Column(name = "source_ip")
	private String sourceIp;
	
	@Formula("COUNT(destination_ip)")
	private BigInteger destinationIpCount;
	
	@Formula("COUNT(DISTINCT source_ip)")
	private Long priorityCount;
	
	@Formula("HOUR(interval_end_time)")
	private Long hourIntervalEndTime;
	
	@Column(name = "interval_end_time")
	private LocalDateTime intervalEndTime;
}
