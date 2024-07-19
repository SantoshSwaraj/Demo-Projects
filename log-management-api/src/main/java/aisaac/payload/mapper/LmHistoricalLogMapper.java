package aisaac.payload.mapper;

import java.util.List;
import java.util.stream.Collectors;

import aisaac.dto.HistoricalLogDto;
import aisaac.model.LmHistoricalLog;

public class LmHistoricalLogMapper {
	
	public static LmHistoricalLog mapper(HistoricalLogDto dto) {
		return LmHistoricalLog.builder()
					 			   .tenantId(dto.getTenantId())
					 			   .fromDate(dto.getFromDate())
					 			   .toDate(dto.getToDate())
					 			   .queryText(dto.getQueryText())
					 			   .executedBy(dto.getUserId())
					 			   .queryId(dto.getQueryId())
					 			   .status(dto.getStatus())
					 			   .statusMessage(dto.getStatusMessage())
				 			   .build();
	}
	
	public static HistoricalLogDto mapper(LmHistoricalLog model) {
		return HistoricalLogDto.builder()
					 			   .tenantId(model.getTenantId())
					 			   .fromDate(model.getFromDate())
					 			   .toDate(model.getToDate())
					 			   .queryText(model.getQueryText())
					 			   .queryId(model.getQueryId())
					 			   .status(model.getStatus())
					 			   .statusMessage(model.getStatusMessage())
				 			   .build();
	}
	
	public static List<HistoricalLogDto> mapper(List<LmHistoricalLog> models) {
		return models.stream().map(LmHistoricalLogMapper::mapper).collect(Collectors.toList());
	}
}
