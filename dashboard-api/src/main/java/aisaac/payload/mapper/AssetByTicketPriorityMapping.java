package aisaac.payload.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.TicketAssetMapping;
import aisaac.payload.response.AssetByTicketPriorityResponse;
import aisaac.util.ResponseOrMessageConstants;

public class AssetByTicketPriorityMapping {

	public static Object map(List<TicketAssetMapping> TicketAssetMapping, Map<Long, String> sysParamValueMap, Long totalCount) {
		Map<String, Long> priorityMap = new HashMap<>(Map.of("High", 0L, "Medium", 0L, "Low", 0L, "No Tickets", 0L));

		List<AssetByTicketPriorityResponse> data = TicketAssetMapping.stream().map(o -> {
			priorityMap.remove(sysParamValueMap.get(o.getPriority()));
			priorityMap.put("No Tickets", priorityMap.get("No Tickets") + o.getCount());
			return new AssetByTicketPriorityResponse()
					.setCount(o.getCount())
					.setPriority(sysParamValueMap.get(o.getPriority()));
		}).collect(Collectors.toList());
		
		priorityMap.forEach((key, value) -> {
			if ("No Tickets".equalsIgnoreCase(key)) {
				data.add(new AssetByTicketPriorityResponse(totalCount - value, key));
			} else {
				data.add(new AssetByTicketPriorityResponse(value, key));
			}
		});

		
		return Map.of(ResponseOrMessageConstants.TOTAL_ASSET_COUNT, totalCount, ResponseOrMessageConstants.DATA, data);
	}
}
