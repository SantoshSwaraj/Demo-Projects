package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.Ticket;
import aisaac.payload.response.DescriptionResponse;
import aisaac.payload.response.TicketListResponse;
import aisaac.util.AppConstants;
import aisaac.util.UbaUtil;

public class TicketListMapper {

	public static Object map(List<Ticket> ticketsList, Map<Long, String> priorityMap, Map<Long, String> categoryMap,
			Map<Long, List<String>> ticketIdUsersMap, Map<Long, String> usersMap) {
		return ticketsList.stream().map(o -> new TicketListResponse()
				.setTicketId(o.getTicketId())
				.setTicketname(o.getTicketName())
				.setAssignedTo(usersMap.get(o.getAssigneeUserId()))
				.setLastUpdated(UbaUtil.getLocalDateTimeInMilliSec(o.getUpdatedDate()))
				.setPriority(priorityMap.get(o.getPriority()))
				.setTicketCategory(categoryMap.get(o.getCategory()))
				.setRiskyUsers(ticketIdUsersMap.get(o.getTicketId()))
				.setTicketStatus(Objects.nonNull(o.getReopenedDate()) ? AppConstants.TICKET_STATUS_STR_REOPENED
						: AppConstants.TICKET_STATUS_STR_OPEN)
				.setDescriptionData(new DescriptionResponse()
						.setCreatedBy(Objects.nonNull(o.getCreatedUserId()) ? usersMap.get(o.getCreatedUserId())
								: usersMap.get(o.getCreatedRuleId()))
						.setCreatedOn(UbaUtil.getLocalDateTimeInMilliSec(o.getCreatedDate()))
						.setUpdatedBy(usersMap.get(o.getUpdatedBy()))
						.setUpdatedOn(UbaUtil.getLocalDateTimeInMilliSec(o.getUpdatedDate()))
						.setDescription(o.getDescription())
						)
				).collect(Collectors.toList());
	}
}
