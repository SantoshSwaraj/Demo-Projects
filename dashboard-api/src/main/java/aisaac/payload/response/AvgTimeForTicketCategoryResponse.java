package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AvgTimeForTicketCategoryResponse {

	private String categoryName;
	private Long responseTime;
	private Long responseTimeSec;
	private Long responseTimeMin;
}
