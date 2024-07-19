package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.AvgResponseTimeForTicketCategoryModel;
import aisaac.payload.response.AvgTimeForTicketCategoryResponse;

public class AverageResponseTimeForTicketCategoryMapper {

	public static Object map(List<AvgResponseTimeForTicketCategoryModel> ticketsList,
			Map<Long, String> sysParamValueCategoryMap) {

		List<AvgTimeForTicketCategoryResponse> response = new ArrayList<>();

		List<String> defaultCategoryData = List.of("Malware and Ransomware", "Application Attacks", "Data Exfiltration",
				"DoS and Network Attacks", "Account Misuse", "Social Engg", "Policy Alerts", "Service Request");

		Map<String, Long> categoryCountMap = ticketsList.stream()
				.collect(Collectors.toMap(o -> sysParamValueCategoryMap.get(o.getCategory()),
						AvgResponseTimeForTicketCategoryModel::getTimeDiffCount));

		defaultCategoryData.forEach((key) -> {
			if (categoryCountMap.containsKey(key))
				response.add(new AvgTimeForTicketCategoryResponse().setCategoryName(key)
						.setResponseTimeSec(categoryCountMap.getOrDefault(key, 0L))
						.setResponseTimeMin(categoryCountMap.getOrDefault(key, 0L) / 60)
						.setResponseTime(categoryCountMap.getOrDefault(key, 0L) / 3600));
			else
				response.add(new AvgTimeForTicketCategoryResponse().setCategoryName(key).setResponseTimeSec(0L)
						.setResponseTimeMin(0L).setResponseTime(0L));
		});
		return response;
	}
}
