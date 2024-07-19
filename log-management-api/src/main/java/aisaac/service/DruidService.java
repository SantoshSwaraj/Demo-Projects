package aisaac.service;

import java.util.Date;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import aisaac.model.DruidRequest;
import aisaac.model.LmDruidFieldsDTO;
import aisaac.model.LogMgmtSearchByFieldsDTO;
import in.zapr.druid.druidry.client.exception.QueryException;

@Service
public interface DruidService {

	JsonNode queryTable(String jsonBody);
	
	JsonNode queryTable(DruidRequest paramRequest, int pageNum, boolean isDownload, List<String> columnsForQuery, List<LmDruidFieldsDTO> lmDruidColumns);

	JsonNode queryTopN(DruidRequest paramRequest);

	Long queryCount(DruidRequest paramRequest, int requestedPage, boolean isDownload, List<LmDruidFieldsDTO> lmDruidColumns);

	JsonNode queryCountByDate(DruidRequest paramRequest);

	public int getMaxPages();

	public long getMaxResults();

	JsonNode queryCountByDuration(DruidRequest request, Date fromDateTime, Date toDateTime);

	JsonNode queryCountByEventIdForTopTenRecords(DruidRequest request, LogMgmtSearchByFieldsDTO searchGroupByDTO, List<LmDruidFieldsDTO> lmDruidColumns);

	JsonNode queryCountForFieldColumns(DruidRequest request, List<LmDruidFieldsDTO> lmDruidFieldMostUsed, String[] fields, List<LmDruidFieldsDTO> lmDruidColumns);

	void cancelQuery(String queryId) throws QueryException;

	JsonNode getHistogramData(DruidRequest request, Date fromDateTime, Date toDateTime, List<LmDruidFieldsDTO> lmDruidColumns, boolean isTH);

	JsonNode getColumnChart(DruidRequest request, List<LmDruidFieldsDTO> lmDruidColumns);

	ResponseEntity<String> validateQuery(DruidRequest request, List<String> fields, List<LmDruidFieldsDTO> lmDruidColumns) throws JsonProcessingException, ParseException, JSONException, ValidationException;

	String invokeDownload(DruidRequest request, List<String> columnsToUseForDownload, List<LmDruidFieldsDTO> lmDruidColumns, String downloadURL);
}
