package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CountryMapAlertResponse {

	private Integer recId;
	private String geoCountrycode;
	private String sourceIp;
	private String destinationIpCount;
	private String destinationIpCountTxt;
}
