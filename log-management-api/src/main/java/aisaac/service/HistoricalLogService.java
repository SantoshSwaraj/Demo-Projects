package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.dto.HistoricalLogDto;
import aisaac.payload.request.HistoricalLogsDetailsRequest;
import aisaac.payload.response.HistoricalLogResponse;
import aisaac.utils.LmHistoricalStatus;

public interface HistoricalLogService {
	
	public HistoricalLogDto saveHistoricalLog(HistoricalLogDto dto) throws Exception;

	public boolean isMoreThanMaxQueryRunning(long historical_logs_max_queries_run);

	public Object getHistoricalLogList(HistoricalLogsDetailsRequest request);
	
	public List<HistoricalLogDto> findByTenantIdAndStatusNotIn(Integer tenantId, List<LmHistoricalStatus> statuses);
	
	public void updateHistoricalLogStatuses(Map<String, String> queryStatusMap);

	public void deleteQuery(String queryId);

	public HistoricalLogResponse findByQueryId(String queryId);

}
