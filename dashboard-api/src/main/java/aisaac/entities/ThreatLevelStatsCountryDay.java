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
@Table(name = "threatlevel_stats_country_day")
@AllArgsConstructor
@NoArgsConstructor
public class ThreatLevelStatsCountryDay {

	@Id
	@Column(name = "rec_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "geo_countrycode")
	private String geoCountryCode;
	
	@Column(name = "threatlevel_code")
	private Long threatLevelCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
}
