package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.AssetMaster;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.TicketAssetMapping;
import aisaac.payload.response.AssetByTicketPriorityLevelResponse;

public class AssetByTicketPriorityLevelMapper {

	public static Object map(List<TicketAssetMapping> ticketAssetMappingsList, List<LogStopageMaster> logStopageMastersList,
			Map<Long, String> priorityMap, Map<Long, String> productIdAndNameMap) {
		List<AssetByTicketPriorityLevelResponse> response = new ArrayList<>();

		Map<Long, List<TicketAssetMapping>> ticketAssetListMap = ticketAssetMappingsList.stream().collect(Collectors
				.groupingBy(o -> Objects.nonNull(o.getProductId()) ? o.getProductId() : 0L, Collectors.toList()));

		logStopageMastersList.forEach((o) -> {
			response.add(new AssetByTicketPriorityLevelResponse()
					.setProductName(Objects.nonNull(productIdAndNameMap.get(o.getProductId()))
							? productIdAndNameMap.get(o.getProductId())
							: "Others")
					.setAssetCount(mapPriorityValues(
							ticketAssetListMap.get(Objects.nonNull(o.getProductId()) ? o.getProductId() : 0L),
							priorityMap, o.getCount())));
		});
		return response;
	}

	private static Map<String, Long> mapPriorityValues(List<TicketAssetMapping> ticketAssetMappingsList,
			Map<Long, String> sysParamPriorityMap, Long totalAssetCountByProductId) {
		Map<String, Long> defaultData = new HashMap<String, Long>(
				Map.of("High", 0L, "Medium", 0L, "Low", 0L, "noTickets", 0L));

		if (Objects.nonNull(ticketAssetMappingsList)) {
			ticketAssetMappingsList.forEach(o -> {
				defaultData.put("noTickets", defaultData.get("noTickets") + o.getCount());
				defaultData.put(sysParamPriorityMap.get(o.getPriority()), o.getCount());
			});
		}

		defaultData.put("noTickets", totalAssetCountByProductId - defaultData.get("noTickets"));
		return defaultData;
	}
}
