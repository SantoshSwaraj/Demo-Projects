package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.Ticket;
import aisaac.payload.response.TicketListResponse;
import aisaac.util.DashboardUtils;
import aisaac.util.ResponseOrMessageConstants;

public class TicketsListMapping {

	public static Object map(List<Ticket> ticketList, Map<Long, String> sysParamPriorityMap,
			Map<Long, String> sysParamCategoryMap,Map<Long, String> userMap) {
		return ticketList.stream().map(o->new TicketListResponse()
				.setTicketStatus(
						Objects.nonNull(o.getReOpenedDate()) ? ResponseOrMessageConstants.TICKET_STATUS_STR_REOPENED
								: ResponseOrMessageConstants.TICKET_STATUS_STR_OPEN)
				.setTicketId(o.getTicketId())
				.setTicketName(o.getTicketName())
				.setAssignedTo(userMap.get(o.getAssigneeUserId()))
				.setPriority(sysParamPriorityMap.get(o.getPriority()))
				.setTicketCategory(sysParamCategoryMap.get(o.getCategory()))
				.setNoOfThreats(o.getThreatCount())
				.setLastUpdated(DashboardUtils.getLocalDateTimeInMilliSec(o.getUpdatedDate()))
				).collect(Collectors.toList());
	}
}
