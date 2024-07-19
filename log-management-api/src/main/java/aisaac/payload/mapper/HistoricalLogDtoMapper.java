package aisaac.payload.mapper;

import aisaac.dto.HistoricalLogDto;
import aisaac.payload.request.HistoricalLogRequest;

public class HistoricalLogDtoMapper {
	
	public static HistoricalLogDto mapper(HistoricalLogRequest request, Integer userId) {
		return HistoricalLogDto.builder()
				                   .userId(userId)
					 			   .tenantId(request.getTenantId())
					 			   .fromDate(request.getFromDate())
					 			   .toDate(request.getToDate())
					 			   .queryText(request.getQueryText())
				 			   .build();
	}

}
