package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import aisaac.entities.CtmtAffectedEntitiesOthers;
import aisaac.entities.Ticket;
import aisaac.payload.response.DescriptionResponse;
import aisaac.payload.response.RiskyEntitiesResponse;
import aisaac.payload.response.TicketListResponse;
import aisaac.util.AppConstants;
import aisaac.util.EbaUtil;

public class TicketListMapper {

	public static Object map(List<Ticket> ticketsList, Map<Long, String> priorityMap, Map<Long, String> categoryMap,
			Map<Long, List<CtmtAffectedEntitiesOthers>> ticketIdEntitiesMap, Map<Long, String> usersMap) {
		return ticketsList.stream().map(o -> new TicketListResponse()
				.setTicketId(o.getTicketId())
				.setTicketname(o.getTicketName())
				.setAssignedTo(usersMap.get(o.getAssigneeUserId()))
				.setLastUpdated(EbaUtil.getLocalDateTimeInMilliSec(o.getUpdatedDate()))
				.setPriority(priorityMap.get(o.getPriority()))
				.setTicketCategory(categoryMap.get(o.getCategory()))
				.setRiskyEntities(getRiskEntities(ticketIdEntitiesMap.get(o.getTicketId())))
				.setTicketStatus(Objects.nonNull(o.getReopenedDate()) ? AppConstants.TICKET_STATUS_STR_REOPENED
						: AppConstants.TICKET_STATUS_STR_OPEN)
				.setDescriptionData(new DescriptionResponse()
						.setCreatedBy(Objects.nonNull(o.getCreatedUserId()) ? usersMap.get(o.getCreatedUserId())
								: usersMap.get(o.getCreatedRuleId()))
						.setCreatedOn(EbaUtil.getLocalDateTimeInMilliSec(o.getCreatedDate()))
						.setUpdatedBy(usersMap.get(o.getUpdatedBy()))
						.setUpdatedOn(EbaUtil.getLocalDateTimeInMilliSec(o.getUpdatedDate()))
						.setDescription(o.getDescription())
						)
				).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	private static List<Object> getRiskEntities(List<CtmtAffectedEntitiesOthers> ctmtAffectedEntitiesOthersList){
		if (Objects.isNull(ctmtAffectedEntitiesOthersList)) {
			return (List<Object>) CollectionUtils.EMPTY_COLLECTION;
		}
		return ctmtAffectedEntitiesOthersList.stream().map(o -> new RiskyEntitiesResponse()
				.setEntityName(EbaUtil.getEntityNameWithType(o.getResourceName(), o.getResourceType()))
				.setEntityType(EbaUtil.getEntityType(o.getResourceType()))
				).collect(Collectors.toList());
	}
}
