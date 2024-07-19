package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.dto.SpeedOfResponseForValidationTrendDataDto;
import aisaac.entities.AvgResponseTimeModel;
import aisaac.entities.InvestigationSave;
import aisaac.payload.response.AvgTimeResponse;

public class AvgResponseTimeMapper {

	public static Object map(List<AvgResponseTimeModel> ticketsList, List<InvestigationSave> investigationSavesList,
			List<SpeedOfResponseForValidationTrendDataDto> threatResponsespeedHoursList) {
		List<Object> responseTrend = new ArrayList<>();
		List<Object> investigationTrend = new ArrayList<>();
		List<Object> validationTrend = new ArrayList<>();
		Long avgResponseTime = 0L;
		Long avgValidationTime = 0L;
		Long avgInvestigationTime = 0L;

		Map<Long, Long> responseTrendMap = ticketsList.stream()
				.collect(Collectors.toMap(AvgResponseTimeModel::getHourClosedDate, o -> o.getTimeDiff() / o.getTicketIdCount()));

		Map<Long, Long> investigationTrendMap = investigationSavesList.stream().collect(
				Collectors.toMap(InvestigationSave::getHourClosedDate, o -> o.getTimeDiff() / o.getInvestigationIdCount()));

		Map<Long, Long> validationTrendMap = threatResponsespeedHoursList.stream()
				.collect(Collectors.toMap(SpeedOfResponseForValidationTrendDataDto::getHourCreatedDate,
						SpeedOfResponseForValidationTrendDataDto::getValidationResponsetime,
						(existing, newValue) -> newValue));

		for (Long i = 0L; i < 24; i++) {

			avgResponseTime += responseTrendMap.getOrDefault(i, 0L);
			avgValidationTime += validationTrendMap.getOrDefault(i, 0L);
			avgInvestigationTime += investigationTrendMap.getOrDefault(i, 0L);

			responseTrend.add(Map.of("hour", i, "responseTime", responseTrendMap.getOrDefault(i, 0L)));
			investigationTrend.add(Map.of("hour", i, "responseTime", investigationTrendMap.getOrDefault(i, 0L)));
			validationTrend.add(Map.of("hour", i, "responseTime", validationTrendMap.getOrDefault(i, 0L)));
		}

		return new AvgTimeResponse()
				.setAvgResponseTime(avgResponseTime)
				.setAvgInvestigationTime(avgInvestigationTime)
				.setAvgValidationTime(avgValidationTime)
				.setData(responseTrend)
				.setAvgInvestigationData(investigationTrend)
				.setAvgValidationData(validationTrend);
	}

}
