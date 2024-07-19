package aisaac.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import aisaac.model.DruidRequest;
import aisaac.model.DruidRequestDTO;
import aisaac.model.DruidResponse;
import aisaac.model.LmDruidFields;
import aisaac.model.LmDruidFieldsDTO;
import aisaac.model.LmQueryHistoryDTO;
import aisaac.model.LogManagementSaveSearchQueryDTO;
import aisaac.model.LogMgmtGetSavedQueryDTO;
import aisaac.model.LogMgmtSaveQueryDTO;
import aisaac.model.LogMgmtSearchByFieldsDTO;
import aisaac.model.LogMgmtSearchDTO;

@Service
public interface LogManagementService {

	String getCsv(DruidRequest paramRequest, String paramString, List<String> columnsToUseForDownload, List<LmDruidFieldsDTO> lmDruidColumns, List<String> stringArrayFields, String downloadViaMicroapp, String downloadURL, Integer offset);

	String getExcel(String json, String csvExportPath, List<LmDruidFields> lmDruidFields, List<String> stringArrayFields) throws IOException;

	ResponseEntity<String> validateQuery(DruidRequest request, List<String> fields, List<LmDruidFieldsDTO> lmDruidColumns) throws JsonProcessingException, ParseException, JSONException, ValidationException;

	public List<LmDruidFields> getLmDruidColumns(String logManagementDatasourceType);
	
	public List<LmDruidFieldsDTO> getLmDruidColumnsDTO(String logManagementDatasourceType);
	
	public List<LmDruidFieldsDTO> getLmDruidDTOColumns(String logManagementDatasourceType);
	
	JsonNode countByEventIdForTopTenRecords(DruidRequest request, LogMgmtSearchByFieldsDTO searchGroupByDTO, List<LmDruidFieldsDTO> lmDruidColumns);
	
	JsonNode countForFieldColumns(DruidRequest request, List<LmDruidFieldsDTO> lmDruidFieldMostUsed, String[] fields, List<LmDruidFieldsDTO> lmDruidColumns);

	DruidResponse searchQuery(DruidRequest request, String id, int requestedPage, boolean isDownload, List<String> columnsToUseForDownload, List<LmDruidFieldsDTO> lmDruidColumns);

	JsonNode getHistogramData(DruidRequest request, Date fromDateTime, Date toDateTime, List<LmDruidFieldsDTO> lmDruidColumns);
	
	public void saveSearchedQueryForTenant(LogManagementSaveSearchQueryDTO searchDTO);

	public ArrayList<LmQueryHistoryDTO> getSearchedQueries(Integer userId);
	
	public void saveQueryForTenants(Integer userId , LogMgmtSaveQueryDTO saveQueryDTO, String queryName) throws Exception;
	
	ArrayList<LogMgmtGetSavedQueryDTO> getSavedQueries(List<Integer> tenantIds);

	ArrayList<LogMgmtGetSavedQueryDTO> getSavedQueriesForNonPalUsers(List<Integer> tenantIds);
	
	JsonNode getColumnChart(DruidRequest request, List<LmDruidFieldsDTO> lmDruidColumns);
	
	Boolean cancelQuery(String queryId);

}
