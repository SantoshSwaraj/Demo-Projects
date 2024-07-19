package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ThreatLevelFromAttackCountriesMapResponse {

	private Double Long;
	private Double lat;
	private String threatLevel;
	private Long threatCount;
	private String cityName;
	private String geoCountrycode;
}
