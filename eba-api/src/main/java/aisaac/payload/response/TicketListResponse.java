package aisaac.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TicketListResponse {

	private Long ticketId;
	private String ticketname;
	private String assignedTo;
	private String ticketCategory;
	private List<Object> riskyEntities;
	private String priority;
	private Long lastUpdated;
	private String ticketStatus;
	private DescriptionResponse descriptionData;
}
