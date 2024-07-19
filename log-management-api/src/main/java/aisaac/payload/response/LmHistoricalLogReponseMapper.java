package aisaac.payload.response;

import aisaac.model.LmHistoricalLog;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LmHistoricalLogReponseMapper {

	public static List<Object> map(List<LmHistoricalLog> data,
			Map<Integer, String> userMap) {
		return data.stream()
				   .map(o-> HistoricalLogResponse.builder()
						    .recId(o.getRecId())
						    .tenantId(o.getTenantId())
						    .queryText(o.getQueryText())
						    .fromDate(o.getFromDate().getTime())
						    .toDate(o.getToDate().getTime())
						    .executedBy(userMap.get(o.getExecutedBy()))
						    .executedOn(o.getExecutedOn().getTime())
						    .queryId(o.getQueryId())
						    .status(o.getStatus())
						   .build())
				   .collect(Collectors.toList());
	}
	
	public static HistoricalLogResponse map(LmHistoricalLog model,
			Map<Integer, String> userMap) {
		return HistoricalLogResponse.builder()
			    .recId(model.getRecId())
			    .tenantId(model.getTenantId())
			    .queryText(model.getQueryText())
			    .fromDate(model.getFromDate().getTime())
			    .toDate(model.getToDate().getTime())
			    .executedBy(userMap.get(model.getExecutedBy()))
			    .executedOn(model.getExecutedOn().getTime())
			    .queryId(model.getQueryId())
			    .status(model.getStatus())
			   .build();
		
	}
}
