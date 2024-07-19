package aisaac.payload.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.TicketWithOutFormula;
import aisaac.payload.response.TicketCountAndPriorityResponse;
import aisaac.util.DashboardUtils;
import aisaac.util.ResponseOrMessageConstants;

public class TicketCountAndPriorityMapping {

	public static Object map(List<TicketWithOutFormula> ticketsList, Map<Long, String> sysParamValueMap, Long totalCount) {
		Map<String, Long> priorityMap = new HashMap<>(Map.of("High", 0L, "Medium", 0L, "Low", 0L));
		
		List<TicketCountAndPriorityResponse> data = ticketsList.stream().map(o -> {
			priorityMap.remove(sysParamValueMap.get(o.getPriority()));
			return new TicketCountAndPriorityResponse()
					.setCount(o.getTicketIdCount())
					.setPriority(sysParamValueMap.get(o.getPriority()))
					.setMinCreatedDate(DashboardUtils.getLocalDateTimeInMilliSec(o.getMinCreatedDate()))
					.setMaxCreatedDate(DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxCreatedDate()))
					.setMinReOpenedDate(DashboardUtils.getLocalDateTimeInMilliSec(o.getMinReOpenedDate()))
					.setMaxReOpenedDate(DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxReOpenedDate()));
		}).collect(Collectors.toList());
		
		priorityMap.forEach((key, value) -> {
			data.add(new TicketCountAndPriorityResponse(value,key,null,null,null,null));
		});

		
		return Map.of(ResponseOrMessageConstants.TOTAL_TICKET_COUNT, totalCount, ResponseOrMessageConstants.DATA, data);
	}
}
