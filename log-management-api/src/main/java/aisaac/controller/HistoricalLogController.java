package aisaac.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import aisaac.dto.HistoricalLogDto;
import aisaac.dto.LmDruidFieldsDTO;
import aisaac.exception.ValidationException;
import aisaac.model.LMCustomizedFieldsDTO;
import aisaac.payload.mapper.HistoricalLogDtoMapper;
import aisaac.payload.request.HistoricalLogRequest;
import aisaac.payload.request.HistoricalLogsDetailsRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.payload.response.HistoricalLogResponse;
import aisaac.service.GlobalSettingsService;
import aisaac.service.HistoricalLogDruidService;
import aisaac.service.HistoricalLogService;
import aisaac.service.LmUserFieldCustomizationService;
import aisaac.utils.LMConstants;
import aisaac.utils.LmHistoricalStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/historical-log")
public class HistoricalLogController {
	
	private HistoricalLogService historicalLogService;
	
	private HistoricalLogDruidService historicalLogDruidService;
	
	private LmUserFieldCustomizationService lmUserFieldCustomizationService;
	
	private GlobalSettingsService globalSettingsService;

	public HistoricalLogController(
			HistoricalLogService historicalLogService,
			HistoricalLogDruidService historicalLogDruidService,
			LmUserFieldCustomizationService lmUserFieldCustomizationService,
			GlobalSettingsService globalSettingsService) {
		this.historicalLogService = historicalLogService;
		this.historicalLogDruidService = historicalLogDruidService;
		this.lmUserFieldCustomizationService = lmUserFieldCustomizationService;
		this.globalSettingsService = globalSettingsService;
	}
	
	@PostMapping("/list")
	public ResponseEntity<Object> listHistoricalLog(
			@RequestBody HistoricalLogsDetailsRequest request,
			@RequestParam Integer userId)throws Exception{
		
		Map<String, Object> result = new LinkedHashMap<>();
		
		updateQueryStatus(request.getTenantId());
		
		String historical_logs_max_days_search_str = globalSettingsService.getGlobalSettingsParamValue(
				LMConstants.LOG_MANAGEMENT_PARAM_TYPE, LMConstants.HISTORICAL_LOGS_MAX_DAYS_SEARCH_PARAM_NAME);
		
		if(StringUtils.isBlank(historical_logs_max_days_search_str) 
				|| !StringUtils.isNumeric(historical_logs_max_days_search_str))
			historical_logs_max_days_search_str = LMConstants.HISTORICAL_LOGS_MAX_DAYS_SEARCH_DEFAULT;
		
		long historical_logs_max_days_search = Long.parseLong(historical_logs_max_days_search_str);
		
		Object data = historicalLogService.getHistoricalLogList(request);
		
		result.put("historical_logs_max_days_search", historical_logs_max_days_search);
		result.put("data", data);
		
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
	
	private void updateQueryStatus(Integer tenantId) throws Exception{
		
		List<LmHistoricalStatus> statuses = List.of(LmHistoricalStatus.SUCCESS);
		List<HistoricalLogDto> dtos = historicalLogService.findByTenantIdAndStatusNotIn(tenantId, statuses);
		
		Map<String, String> getStatusResponse = historicalLogDruidService.getStatus(dtos);
		
		historicalLogService.updateHistoricalLogStatuses(getStatusResponse);
	}
	
	@PostMapping("/query")
	public ResponseEntity<Object> saveHistoricalLog(
			@RequestBody @Valid HistoricalLogRequest request,
			@RequestParam Integer userId,
			Errors errors)throws Exception{
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(LMConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		
		String historical_logs_max_queries_run_str = globalSettingsService.getGlobalSettingsParamValue(
				LMConstants.LOG_MANAGEMENT_PARAM_TYPE, LMConstants.HISTORICAL_LOGS_MAX_QUERIES_RUN_PARAM_NAME);
		
		if(StringUtils.isBlank(historical_logs_max_queries_run_str) 
				|| !StringUtils.isNumeric(historical_logs_max_queries_run_str))
			historical_logs_max_queries_run_str = LMConstants.HISTORICAL_LOGS_MAX_QUERIES_RUN_DEFAULT;
		
		long historical_logs_max_queries_run = Long.parseLong(historical_logs_max_queries_run_str);
		
		if(historicalLogService.isMoreThanMaxQueryRunning(historical_logs_max_queries_run))
		    throw new ValidationException(String.format("More the %d queries can't run simultenously.", historical_logs_max_queries_run));
		
		HistoricalLogDto dto = HistoricalLogDtoMapper.mapper(request, userId);
		try {
			Map<String, String> submitQueryResponse = historicalLogDruidService.submitQuery(dto);
			dto.setQueryId(submitQueryResponse.getOrDefault("queryId", null));
			dto.setStatus(LmHistoricalStatus.fromString(submitQueryResponse.get("state")));
		}catch(Exception e) {
			log.error(String.format("Error in submit query due to ", e.getMessage()));
			dto.setStatusMessage(e.getMessage());
		}
		
		HistoricalLogDto savedHistoricalLog = historicalLogService.saveHistoricalLog(dto);
		
		ApiResponse response = new ApiResponse();
		response.setData(savedHistoricalLog);
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(String.format("Historical Logs saved successfully for tenantId: %d ", request.getTenantId()));
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	
	@GetMapping("/query/{queryId}/data")
	public ResponseEntity<Object> getQueryData(
			@PathVariable(name="queryId") String queryId) throws Exception{
		Map<String, Object> result = new LinkedHashMap<>();
		
		Map<String, Object> getStatusResponse = historicalLogDruidService.getData(queryId);
		HistoricalLogResponse dto = historicalLogService.findByQueryId(queryId);
		int totalCount = (int)((List<JsonNode>) getStatusResponse.get("events")).size();
		
		result.put("info", dto);
		result.put("data", getStatusResponse);
		result.put("recordsFiltered", totalCount);
		result.put("recordsTotal", totalCount);
		result.put("draw", 0);
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
	
	@DeleteMapping("/query/{queryId}")
	public ResponseEntity<Object> deleteQuery(
			@PathVariable(name="queryId") String queryId) throws Exception{
		
		historicalLogDruidService.deleteQuery(queryId);
		
		historicalLogService.deleteQuery(queryId);
		
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(String.format("Successfully deleted queryId : %s ", queryId));
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@GetMapping("/main-table-columns")
	public ResponseEntity<Object> getDruidColumnsForMainTable(
			@RequestParam Integer userId) throws Exception{
		
		Map<String, Object> result = new HashMap<>();
		List<LmDruidFieldsDTO> lmDruidColumns =
				lmUserFieldCustomizationService
					.getLmUserFieldsCustomizationMainTable(userId,
							LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_HISTORICAL_LOGS);
		
		List<LmDruidFieldsDTO> lmDruidDefaultColumns = 
				lmUserFieldCustomizationService
					.getLmUserFieldsCustomizationMainTable(LMConstants.DEFAULT_USER_ID,
							LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_HISTORICAL_LOGS);
		
		result.put("mainTableColumnsForDefault", lmDruidDefaultColumns);
		result.put("mainTableColumnsForUser", lmDruidColumns);
		
		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(String.format("Successfully got druid Columns For Main Table for userId : %d ", userId));
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@PostMapping("/main-table-columns")
	public ResponseEntity<Object> saveMainTableColumns(
			@RequestParam Integer userId,
			@RequestBody LMCustomizedFieldsDTO fieldsDto) throws Exception{
		
		List<LmDruidFieldsDTO> lmDruidAllColumns =
				lmUserFieldCustomizationService
					.saveCustomizeColumnsForMainTable(userId, fieldsDto.getCustomizedFields(),
							LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_HISTORICAL_LOGS);
		
		ApiResponse response = new ApiResponse();
		response.setData(lmDruidAllColumns);
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(String.format("Successfully saved druid Columns For Main Table for userId : %d ", userId));
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
}
