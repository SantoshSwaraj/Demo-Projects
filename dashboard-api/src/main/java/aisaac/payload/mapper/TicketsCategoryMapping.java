package aisaac.payload.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.TicketWithOutFormula;

public class TicketsCategoryMapping {

	public static Object map(List<TicketWithOutFormula> ticketsList, Map<Long, String> sysParamPriorityMap,
			Map<Long, String> sysParamCategoryMap) {
		Map<String, Long> defaultData = new HashMap<String, Long>(Map.of("High", 0L, "Medium", 0L, "Low", 0L));

		Map<String, Object> defaultCategoryData = new HashMap<String, Object>(
				Map.of("Malware and Ransomware", defaultData, "Application Attacks", defaultData, "Data Exfiltration",
						defaultData, "DoS and Network Attacks", defaultData, "Account Misuse", defaultData,
						"Social Engg", defaultData, "Policy Alerts", defaultData, "Service Request", defaultData));

		Map<String, List<TicketWithOutFormula>> ticketListMap = ticketsList.stream()
				.collect(Collectors.groupingBy(o -> sysParamCategoryMap.get(o.getCategory()), Collectors.toList()));

		ticketListMap.forEach((key, value) -> {
			defaultCategoryData.put(key, mapPriorityValues(value, sysParamPriorityMap));
		});
		return defaultCategoryData;
	}

	private static Map<String, Long> mapPriorityValues(List<TicketWithOutFormula> ticketsList,
			Map<Long, String> sysParamPriorityMap) {
		Map<String, Long> defaultData = new HashMap<String, Long>(Map.of("High", 0L, "Medium", 0L, "Low", 0L));

		ticketsList.forEach(o -> {
			defaultData.put(sysParamPriorityMap.get(o.getPriority()), o.getTicketIdCount());
		});
		return defaultData;
	}

}
