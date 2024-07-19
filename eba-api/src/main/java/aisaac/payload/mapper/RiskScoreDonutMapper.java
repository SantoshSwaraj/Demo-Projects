package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.dto.EntityScoreDto;
import aisaac.payload.response.RiskScoreDonutResponse;
import aisaac.util.AppConstants;
import aisaac.util.EbaUtil;

public class RiskScoreDonutMapper {

	public static Object map(List<EntityScoreDto> adrEntityScoresList) {

		Map<String, Long> severityCountMap = adrEntityScoresList.stream().collect(
				Collectors.groupingBy(o -> EbaUtil.getSeverityType(o.getEntityScore()), Collectors.counting()));

		List<RiskScoreDonutResponse> chartData = AppConstants.SEVERITY_MAP
				.entrySet().stream().map(o -> new RiskScoreDonutResponse()
						.setCount(severityCountMap.getOrDefault(o.getKey(), o.getValue()))
						.setSeverity(o.getKey()))
				.collect(Collectors.toList());
		return Map.of(AppConstants.TOTAL_ENTITY_COUNT, adrEntityScoresList.size(), AppConstants.CHART_DATA, chartData);
	}
}
