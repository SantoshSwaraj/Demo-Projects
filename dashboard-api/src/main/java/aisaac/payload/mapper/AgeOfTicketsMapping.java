package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.AgeOfTicketModel;
import aisaac.payload.response.AgeOfTicketsResponse;
import aisaac.util.AppConstants;
import aisaac.util.DashboardUtils;

public class AgeOfTicketsMapping {

	public static Object map(List<AgeOfTicketModel> ticketsList, Map<Long, String> priorityMap) {
		List<AgeOfTicketsResponse> response = new ArrayList<>();
		Map<String, Long> defaultData = new HashMap<String, Long>(Map.of("High", 0L, "Medium", 0L, "Low", 0L));

		Map<String, Map<String, Long>> defaultCategoryData = new HashMap<String, Map<String, Long>>(Map.of(
				AppConstants.HOURS_DIFFERENCE_0_TO_24, defaultData, AppConstants.DAYS_DIFFERENCE_2_TO_7, defaultData,
				AppConstants.DAYS_DIFFERENCE_8_TO_15, defaultData, AppConstants.DAYS_DIFFERENCE_16_TO_30, defaultData,
				AppConstants.DAYS_DIFFERENCE_30, defaultData));

		Map<String, List<AgeOfTicketModel>> ticketListMap = ticketsList.stream().collect(Collectors.groupingBy(
				o -> DashboardUtils.getAgeGroupByDate(o.getCreatedDate(), o.getReOpenedDate()), Collectors.toList()));

		defaultCategoryData.forEach((key, value) -> {
			AgeOfTicketsResponse ageOfTicketsResponse = new AgeOfTicketsResponse();
			ageOfTicketsResponse.setAgeGroup(key);
			ageOfTicketsResponse.setTicketCount(Objects.nonNull(ticketListMap.get(key))
					? mapPriorityValues(ticketListMap.get(key), priorityMap, ageOfTicketsResponse)
					: value);
			response.add(ageOfTicketsResponse);
		});
		return response;
	}

	private static Map<String, Long> mapPriorityValues(List<AgeOfTicketModel> Ticket,
			Map<Long, String> sysParamPriorityMap, AgeOfTicketsResponse ageOfTicketsResponse) {
		Map<String, Long> defaultData = new HashMap<String, Long>(Map.of("High", 0L, "Medium", 0L, "Low", 0L));
		Ticket.forEach(o -> {
			if ("High".equalsIgnoreCase(sysParamPriorityMap.get(o.getPriority()))) {
				ageOfTicketsResponse.setMinDateHigh(DashboardUtils.getLocalDateTimeInMilliSec(o.getMinCreatedDate()));
			} else if ("Medium".equalsIgnoreCase(sysParamPriorityMap.get(o.getPriority()))) {
				ageOfTicketsResponse.setMinDateMedium(DashboardUtils.getLocalDateTimeInMilliSec(o.getMinCreatedDate()));
			} else if ("Low".equalsIgnoreCase(sysParamPriorityMap.get(o.getPriority()))) {
				ageOfTicketsResponse.setMinDateLow(DashboardUtils.getLocalDateTimeInMilliSec(o.getMinCreatedDate()));
			}
			defaultData.put(sysParamPriorityMap.get(o.getPriority()), o.getTicketIdCount());
		});
		return defaultData;
	}
}
