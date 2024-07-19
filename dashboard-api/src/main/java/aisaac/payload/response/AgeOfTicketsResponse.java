package aisaac.payload.response;

import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AgeOfTicketsResponse {

	private Long minDateHigh;
	private Long minDateMedium;
	private Long minDateLow;
	private String ageGroup;
	private Map<String, Long> ticketCount;
}
