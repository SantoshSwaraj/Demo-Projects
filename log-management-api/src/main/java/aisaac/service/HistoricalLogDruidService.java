package aisaac.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import aisaac.dto.HistoricalLogDto;
import aisaac.utils.LmHistoricalStatus;

public interface HistoricalLogDruidService {
	
	public Map<String, String> submitQuery(HistoricalLogDto dto) throws Exception;

	public Map<String, String> getStatus(List<HistoricalLogDto> dtos) throws Exception;

	public Map<String, Object> getData(String queryId) throws Exception;

	public void deleteQuery(String queryId) throws Exception;
}
