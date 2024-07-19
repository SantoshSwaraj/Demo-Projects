package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TicketCountAndPriorityResponse {

	private Long count;
	
	private String priority;
	
	private Long minCreatedDate;
	
	private Long maxCreatedDate;
	
	private Long minReOpenedDate;
	
	private Long maxReOpenedDate;
	
}
