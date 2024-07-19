package aisaac.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.xml.bind.ValidationException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import aisaac.model.DownloadContent;
import aisaac.model.DruidRequest;
import aisaac.model.DruidResponse;
import aisaac.model.LmQueryHistoryDTO;
import aisaac.model.LmDruidFields;
import aisaac.model.LmDruidFieldsDTO;
import aisaac.model.LmSearchDTO;
import aisaac.model.LogManagementApiGetSavedQueriesRequest;
import aisaac.model.LogMgmtGetSavedQueryDTO;
import aisaac.model.LogManagementSaveSearchQueryDTO;
import aisaac.model.LogMgmtGetColumnDTO;
import aisaac.model.LogMgmtHistogramDTO;
import aisaac.model.LogMgmtMostUsedFieldsDTO;
import aisaac.model.LogMgmtSaveQueryLogDTO;
import aisaac.model.LogMgmtSearchByFieldsDTO;
import aisaac.model.LogMgmtSearchedQueriesDTO;
import aisaac.model.LogMgmtTopTenRecordsDTO;
import aisaac.model.ValidateQueryDTO;
import aisaac.service.GlobalSettingsService;
import aisaac.service.LogManagementService;
import aisaac.utils.LMConstants;
import aisaac.utils.LMUtils;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@org.springframework.web.bind.annotation.RestController
@Slf4j
@CrossOrigin(origins = "*")
public class RestController {

    @Value("${druid.dataSource}")
	String dataSource;
	
	@Autowired
	private LogManagementService lmService;

	@Autowired
	private GlobalSettingsService globalSettingsService;

	@Autowired
	public RestController(LogManagementService logManagementService) {
		this.lmService = logManagementService;
	}

	@PostMapping(value = "/download")
	public @ResponseBody DownloadContent download(HttpServletRequest httpRequest, @RequestBody LogMgmtSearchByFieldsDTO searchDTO, HttpServletResponse response)
			throws IOException {
		System.out.println("/download= " + searchDTO);
		List<String> stringArrayFields = null;

		DruidRequest druidRequest = new DruidRequest(searchDTO.getDruidRequest());

		String id = String.valueOf(druidRequest.hashCode());  
		String stringArrayFieldsConfig = globalSettingsService.getGlobalSettingsParamValue(LMConstants.LOG_MANAGEMENT_PARAM_TYPE, LMConstants.LOG_MANAGEMENT_STRING_ARRAY_FIELDS);
		if(!StringUtils.isEmpty(stringArrayFieldsConfig)) {
			stringArrayFields = Arrays.asList(stringArrayFieldsConfig.split(","));
		}
		
		String csv = lmService.getCsv(druidRequest, id, searchDTO.columnsToUseForDownload(), searchDTO.getLmDruidColumns() , stringArrayFields, 
				LMConstants.FALSE, null, searchDTO.getDownloadOffset());
			
		return new DownloadContent(csv);

	}
	
	@PostMapping(value = "/search")
	public DruidResponse search(@RequestBody LmSearchDTO lmSearchDTO) {
			DruidResponse druidResponse = lmService.searchQuery(
					new DruidRequest(lmSearchDTO.getRequest()), 
					String.valueOf(lmSearchDTO.getRequest().hashCode()),
					lmSearchDTO.getRequestedPage(),
					lmSearchDTO.isDownload(), 
					lmSearchDTO.getColumnsToUseForDownload(), 
					lmSearchDTO.getLmDruidColumns());
						
			return druidResponse;
	}


	@PostMapping(value = "/histogram")
	public @ResponseBody JsonNode histogram(@RequestBody LogMgmtHistogramDTO searchDTO) {	
		JsonNode response = lmService.getHistogramData(
				new DruidRequest(searchDTO.getDruidRequest()), 
				searchDTO.getFromDateTime(), 
				searchDTO.getToDateTime(), 
				searchDTO.getLmDruidColumns());

		return response;
		
	}
	
	@PostMapping(value = "/getCountForUsedFields")
	public @ResponseBody JsonNode getCountForUsedFields(@RequestBody LogMgmtMostUsedFieldsDTO fieldsDto) {
		JsonNode response = lmService.countForFieldColumns(
				new DruidRequest(fieldsDto.getDruidRequest()), 
				fieldsDto.getLmDruidFieldMostUsed(), 
				fieldsDto.getFieldsMostUsed(), 
				fieldsDto.getLmDruidColumns());
		return response;
	}
	
	@PostMapping(value = "/getTopTenRecordsGroupByEventId")
	public @ResponseBody JsonNode getTopTenRecordsGroupByEventId(@RequestBody LogMgmtTopTenRecordsDTO topTenDTO) {
		JsonNode response = lmService.countByEventIdForTopTenRecords(
				new DruidRequest(topTenDTO.getDruidRequest()), 
				topTenDTO.getLogMgmtSearchByFieldsDTO(), 
				topTenDTO.getLmDruidColumns());

		return response;
	}
	
	@PostMapping(value = "/validateQuery")
	public @ResponseBody ResponseEntity<String> validateQuery(@RequestBody ValidateQueryDTO dto) {
		ResponseEntity<String> response = null;
		try {
			response = lmService.validateQuery(
					new DruidRequest(dto.getDruidRequestDTO()), 
					dto.getFields(), 
					dto.getLmDruidColumns());
		} catch (JsonProcessingException | ValidationException | ParseException | JSONException e) {
			return new ResponseEntity<String>("Something bad happened", HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	@PostMapping(value = "/saveSearchedQueryLogMgt")
	public @ResponseBody String saveSearchedQueryLogMgt(@RequestBody LogManagementSaveSearchQueryDTO dto) {
		lmService.saveSearchedQueryForTenant(dto);
		return "Success";
	}

	@PostMapping(value = "/getSearchedQueries")
	public @ResponseBody ArrayList<LmQueryHistoryDTO> getSearchedQueries(@RequestBody LogMgmtSearchedQueriesDTO dto) {
		ArrayList<LmQueryHistoryDTO> searchedQueries = lmService.getSearchedQueries(dto.getUserId());
		return searchedQueries;
	}
	
	@PostMapping(value = "/saveQueryLogMgt")
	public @ResponseBody String saveQueryLogMgt(@RequestBody LogMgmtSaveQueryLogDTO dto) {
		try {
			lmService.saveQueryForTenants(
					dto.getUserId(),
					dto.getLogMgmtSaveQueryDTO(),
					dto.getQueryName()
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Success";
	}
	
	@PostMapping(value ="/getSavedQueries")
	public ArrayList<LogMgmtGetSavedQueryDTO> getSavedQuery(@RequestBody LogManagementApiGetSavedQueriesRequest dto) {
		ArrayList<LogMgmtGetSavedQueryDTO> savedQueries = new ArrayList<LogMgmtGetSavedQueryDTO>();

		if(dto.getIsPaladionUser() && (LMConstants.ROLE_GLOBAL_USER.equalsIgnoreCase(dto.getRoleName()) || LMConstants.ROLE_SIEM_ADMIN.equalsIgnoreCase(dto.getRoleName()) || LMConstants.ROLE_GLOBAL_MANAGER.equalsIgnoreCase(dto.getRoleName()))) {
			savedQueries = lmService.getSavedQueries(dto.getTenantIds());
		}else {
			savedQueries = lmService.getSavedQueriesForNonPalUsers(dto.getTenantIds());
		}
		return savedQueries;
	}
	
	@PostMapping(value = "/getColumnChart")
	public @ResponseBody JsonNode getColumnChart(@RequestBody LogMgmtGetColumnDTO dto) {
		return lmService.getColumnChart(new DruidRequest(dto.getDruidRequestDTO()), dto.getLmDruidColumns());
	}
	
	@PostMapping(value = "/cancelQuery")
	public @ResponseBody Boolean cancelQuery(@RequestBody String queryId) {
		return lmService.cancelQuery(queryId);
	}
	
	@GetMapping("/hi")
	public String hi() throws IOException {	
		log.error("Hello there !!!");
		
		return "hi";
	}
	
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleException(Exception ex) {
        return ex.getMessage();
    }
    
    @CrossOrigin(origins = "*")
    @PostMapping("/download/excel")
    public String downloadExcel(@RequestBody String jsonBody)
    		throws IOException {
    	List<String> stringArrayFields = null;
    	final Long start = (new Date()).getTime();
		log.info(String.format("==== LM EXCEL download start time === %d", start));
    	try {
    		log.info("Request received in Export app - {}", jsonBody);
    		
    		String exportPath = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(LMConstants.LOG_MANAGEMENT_PARAM_TYPE, LMConstants.LOG_MANAGEMENT_CSV_EXPORT_PATH).getParamValue();
    		List<LmDruidFields> lmDruidFields = lmService.getLmDruidColumns(LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS);
    		
    		String stringArrayFieldsConfig = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(LMConstants.LOG_MANAGEMENT_PARAM_TYPE, LMConstants.LOG_MANAGEMENT_STRING_ARRAY_FIELDS).getParamValue();
    		
    		if(!StringUtils.isEmpty(stringArrayFieldsConfig)) {
    			stringArrayFields = Arrays.asList(stringArrayFieldsConfig.split(","));
    		}
    		
    		String excelLocation = lmService.getExcel(jsonBody, exportPath, lmDruidFields, stringArrayFields);
    		
    		log.info("excel created at - \n {}", excelLocation);
    		
    		final Long end = (new Date()).getTime();
	        Long ticks = (end - start);
	        
    		log.info(String.format("==== LM EXCEL download end %d", end));
	        log.info(String.format("==== LM EXCEL download time taken %d ms", ticks));
    		
    		return excelLocation;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
	
}
