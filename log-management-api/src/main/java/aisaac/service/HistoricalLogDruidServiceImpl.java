package aisaac.service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.dao.HistoricalLogRepository;
import aisaac.dto.HistoricalLogDto;
import aisaac.dto.HistoricalLogSubmitQueryRequestDto;
import aisaac.utils.HistoricalLogDruidUtils;

@Service
@Transactional
public class HistoricalLogDruidServiceImpl implements HistoricalLogDruidService {
	
	private static final String CONTEXT_EXECUTION_MODE_ASYNC = "ASYNC";
	private static final Integer CONTEXT_PRIORITY_NONE = -1;
	private static final String CONTEXT_SELECT_DESTINATION_DURABLESTORAGE = "DURABLESTORAGE";
	
	private HistoricalLogDruidUtils historicalLogDruidUtils;
	private HistoricalLogRepository historicalLogRepo;

	public HistoricalLogDruidServiceImpl(
			HistoricalLogDruidUtils historicalLogDruidUtils,
			HistoricalLogRepository historicalLogRepo) {
		this.historicalLogDruidUtils = historicalLogDruidUtils;
		this.historicalLogRepo = historicalLogRepo;
	}

	@Override
	public Map<String, String> submitQuery(HistoricalLogDto dto) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		//TODO
		/**
		 * We have convert user query ie. Filter srcIPAddress = 10.10.10.10
		 * into druid sql query ie. SELECT * FROM \"200-input\" WHERE srcIPAddress = '10.10.10.10'
		 * and append from and to dates into that ie. 
		 * SELECT * FROM \"200-input\" WHERE srcIPAddress = '10.10.10.10' 
		 * AND __time >= '2024-05-02T10:23:55.542Z' AND __time < '2024-05-03T10:23:55.542Z'  
		 * **/
		
		//For testing
		HistoricalLogSubmitQueryRequestDto request = new HistoricalLogSubmitQueryRequestDto();
		request.setQuery("SELECT * FROM \"200-input\" WHERE srcIPAddress = '202.130.40.2' AND __time >= '2024-02-02T10:23:55.542Z' AND __time < '2024-05-03T10:23:55.542Z'");
		HistoricalLogSubmitQueryRequestDto.Context context = request
				.new Context(CONTEXT_EXECUTION_MODE_ASYNC,
							 CONTEXT_PRIORITY_NONE,
							 CONTEXT_SELECT_DESTINATION_DURABLESTORAGE);
		
		request.setContext(context);
		
		String submitQueryUri = "";
		String requestBody = mapper.writeValueAsString(request);
		
		ResponseEntity<Object> response = this.historicalLogDruidUtils.executeDruidQuery(submitQueryUri, HttpMethod.POST, requestBody);
		if(response.getStatusCode() != HttpStatus.OK) {
			String errorBody = mapper.writeValueAsString(response.getBody());
			throw new Exception(errorBody);
		}
		
		String jsonData = response.getBody().toString();
		JsonNode node = mapper.readTree(jsonData);
		
		Map<String, String> result = new LinkedHashMap<>();
		
		if(node.has("queryId") && node.has("state")){
			result.put("queryId", node.findValue("queryId").asText());
			result.put("state", node.findValue("state").asText());
			
		}
		
		return result;
		
	}
	
	
	@Override
	public Map<String, String> getStatus(List<HistoricalLogDto> dtos) throws Exception {
		ObjectMapper mapper =  new ObjectMapper();
		Map<String, String> result = new LinkedHashMap<>();
		
		for(HistoricalLogDto dto:dtos) {
			
			StringBuffer getStatusUri = new StringBuffer();
			getStatusUri.append("/").append(dto.getQueryId());
			
			ResponseEntity<Object> response = this.historicalLogDruidUtils.executeDruidQuery(getStatusUri.toString(), HttpMethod.GET);
			if(response.getStatusCode() != HttpStatus.OK) {
				String reponseBody = mapper.writeValueAsString(response.getBody());
				historicalLogRepo.updateStatusMessageByQueryId(dto.getQueryId(), reponseBody);
				continue;
			}else {
				historicalLogRepo.updateStatusMessageByQueryId(dto.getQueryId(), null);
				String jsonData = response.getBody().toString();
				JsonNode node = mapper.readTree(jsonData); 
				if(node.has("queryId") && node.has("state"))
					result.put(node.findValue("queryId").asText(), node.findValue("state").asText());
			}
		}
		return result;
	}
	
	@Override
	public Map<String, Object> getData(String queryId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		StringBuffer getDataUri = new StringBuffer();
		getDataUri.append("/").append(queryId).append("/results");
		
		ResponseEntity<Object> response = this.historicalLogDruidUtils.executeDruidQuery(getDataUri.toString(), HttpMethod.GET);
		if(response.getStatusCode() != HttpStatus.OK) {
			String reponseBody = mapper.writeValueAsString(response.getBody());
			historicalLogRepo.updateStatusMessageByQueryId(queryId, reponseBody);
			throw new Exception(reponseBody);
		}
		String jsonData = response.getBody().toString();
		return jsonToTabularFormat(jsonData);
	}
	
	
	private Map<String, Object> jsonToTabularFormat(String json) throws Exception{
		
		ObjectMapper mapper =  new ObjectMapper();
		Map<String, Object> result = new LinkedHashMap<>();
		JsonNode node = mapper.readTree(json);
		List<String> columns = new LinkedList<>();
		List<JsonNode> rows = new LinkedList<>();
		
		if(node.isEmpty())
			return result;
		
		JsonNode firstRow = node.get(0);
		firstRow.fieldNames().forEachRemaining(columns::add);
		
		// Data from JSON value
		node.forEach(rows::add);
		
	    result.put("columns", columns);
	    result.put("events", rows);
		return result;
	}

	@Override
	public void deleteQuery(String queryId) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		StringBuffer deleteQueryUri = new StringBuffer();
		deleteQueryUri.append("/").append(queryId);
		
		ResponseEntity<Object> response = this.historicalLogDruidUtils
							.executeDruidQuery(deleteQueryUri.toString(), HttpMethod.DELETE);
		
		if(response.getStatusCode() != HttpStatus.OK) {
			String reponseBody = mapper.writeValueAsString(response.getBody());
			historicalLogRepo.updateStatusMessageByQueryId(queryId, reponseBody);
			throw new Exception(reponseBody);
		}
	} 
	
}
