package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DashboardXcountResponse {

	private String countStr;
	private Long count;
	private String deltaCountStr;
	private Long deltaCount;
	private String color;
	
	
}
