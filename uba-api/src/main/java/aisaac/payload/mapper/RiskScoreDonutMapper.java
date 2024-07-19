package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.UbaPgOneUserTileEntity;
import aisaac.payload.response.RiskScoreDonutResponse;
import aisaac.util.AppConstants;
import aisaac.util.UbaUtil;

public class RiskScoreDonutMapper {

	public static Object map(List<UbaPgOneUserTileEntity> ubaPgOneUserTileEntitiesList) {

		Map<String, Long> severityCountMap = ubaPgOneUserTileEntitiesList.stream()
				.collect(Collectors.groupingBy(o -> UbaUtil.getSeverityType(o.getScore()), Collectors.counting()));

		List<RiskScoreDonutResponse> chartData = AppConstants.SEVERITY_MAP
				.entrySet().stream().map(o -> new RiskScoreDonutResponse()
						.setCount(severityCountMap.getOrDefault(o.getKey(), o.getValue()))
						.setSeverity(o.getKey())
						)
				.collect(Collectors.toList());
		return Map.of(AppConstants.TOTAL_USERS_COUNT, ubaPgOneUserTileEntitiesList.size(), AppConstants.CHART_DATA,
				chartData);
	}
}
