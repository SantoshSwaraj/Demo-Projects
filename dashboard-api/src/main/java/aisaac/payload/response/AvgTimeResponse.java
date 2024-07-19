package aisaac.payload.response;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AvgTimeResponse {

	private Long avgResponseTime;
	private Long avgValidationTime;
	private Long avgInvestigationTime;
	private List<Object> data;
	private List<Object> avgValidationData;
	private List<Object> avgInvestigationData;
}
