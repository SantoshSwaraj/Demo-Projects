package aisaac.entities;

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
@Table(name = "geo_country_master")
@AllArgsConstructor
@NoArgsConstructor
public class GeoCountryMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rec_id")
	private Long recId;

	@Column(name = "country_code")
	private String countryCode; 
	
	@Column(name = "country_name")
	private String countryName; 
	
	@Column(name = "latitude")
	private Double latitude; 
	
	@Column(name = "longitude")
	private Double longitude; 
}
