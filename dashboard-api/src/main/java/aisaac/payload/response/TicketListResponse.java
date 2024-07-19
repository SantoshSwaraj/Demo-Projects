package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TicketListResponse {

	private String ticketStatus;
	private Long ticketId;
	private String ticketName;
	private String assignedTo;
	private String priority;
	private String ticketCategory;
	private Long noOfThreats;
	private Long lastUpdated;
	
}
