package aisaac.service;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import aisaac.dao.LMSavedQueryRepository;
import aisaac.dao.LmDruidFieldsRepository;
import aisaac.dao.LmQueryHistoryRepository;
import aisaac.model.DruidRequest;
import aisaac.model.DruidResponse;
import aisaac.model.LmDruidFields;
import aisaac.model.LmDruidFieldsDTO;
import aisaac.model.LmQueryHistory;
import aisaac.model.LmQueryHistoryDTO;
import aisaac.model.LmSavedQuery;
import aisaac.model.LogManagementSaveSearchQueryDTO;
import aisaac.model.LogMgmtGetSavedQueryDTO;
import aisaac.model.LogMgmtSaveQueryDTO;
import aisaac.model.LogMgmtSearchByFieldsDTO;
import aisaac.model.User;
import aisaac.utils.LMConstants;
import aisaac.utils.LMUtils;
import in.zapr.druid.druidry.client.exception.QueryException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogManagementServiceImpl implements LogManagementService {

	private final DruidService druidService;
	private final AppConfig config;
	
	@Autowired
	private GlobalSettingsService globalSettings;
	
	@Autowired
	private LmDruidFieldsRepository lmDruidFieldsRepository;
	
	@Autowired
	private LMSavedQueryRepository lmSavedQueryRepository;
	
	@Autowired
	private LmQueryHistoryRepository lmQueryHistoryRepository;
	
	@Autowired
	public LogManagementServiceImpl(DruidService database, AppConfig config) {
		this.druidService = database;
		this.config = config;
	}
	
	public String getCsv(DruidRequest request, String id, List<String> columnsToUseForDownload, List<LmDruidFieldsDTO> lmDruidColumns, List<String> stringArrayFields, String downloadViaMicroapp, String downloadURL, Integer offset) {
		
		List<String> columns;
        List<JsonNode> rows;
        
		log.info("Getting data from Druid.");
		columns = new LinkedList<>();
        rows = new LinkedList<>();
        boolean isFreeSearch = request.getIsFreeSearch();
		
		if (isFreeSearch) {
			updateDruidRequestForFreeSearch(request, lmDruidColumns);
		}
		
		if (StringUtils.isEmpty(downloadViaMicroapp) || 
				(!StringUtils.isEmpty(downloadViaMicroapp) && downloadViaMicroapp.equalsIgnoreCase(LMConstants.FALSE))) {
	        Map<String, String> mapByfield = lmDruidColumns.stream().collect(Collectors.toMap(LmDruidFieldsDTO::getField, LmDruidFieldsDTO::getDisplayName));
	        JsonNode json = druidService.queryTable(request, offset, true, columnsToUseForDownload, lmDruidColumns);
	        log.info("processing result set" + id);
	        if (json.isArray()) {
	            for (JsonNode obj : json) {
	                if (columns.isEmpty())
	                    obj.get("columns").forEach(name -> columns.add(mapByfield.get(name.asText()) != null ? mapByfield.get(name.asText()) : name.asText()));
	                obj.get("events").forEach(rows::add);
	            }
	        } else {
	        	if(json.get("columns") != null) json.get("columns").forEach(name -> columns.add(mapByfield.get(name.asText()) != null ? mapByfield.get(name.asText()) : name.asText()));
	        	json.get("events").forEach(rows::add);
	        }
	        log.info("processing result set end" + id);
			log.info("Download result size - {}", (rows != null) ? rows.size() : 0);
			
			return renderCsv(rows, columns, lmDruidColumns, request.getIsUtcTime(), request.getTimezoneOffset(), stringArrayFields);
		}else {
			return druidService.invokeDownload(request, columnsToUseForDownload, lmDruidColumns, downloadURL);
		}
		 
	}


	private String renderCsv(List<JsonNode> rows, List<String> columns, List<LmDruidFieldsDTO> lmDruidColumns, Boolean isUtcTimezone, Integer timezoneOffset, List<String> stringArrayFields) {
		log.info("Data receieved. Forming string for the csv data.");
		if (columns == null || rows == null || (rows != null && rows.size() == 0))
			return "";
		String separator = ",";
		StringBuilder builder = (new StringBuilder()).append(String.join(separator, (Iterable) columns)).append("\n");
		List<String> dateFields = lmDruidColumns.stream().filter(field -> LMConstants.LOG_MANAGEMENT_DATE_FIELDS_DATATYPE.equals(field.getDataType())).map(LmDruidFieldsDTO::getField).collect(Collectors.toList());

		Date dateValue = null;
		String concatValue = null;
		
		if (!isUtcTimezone) {
			concatValue = LMUtils.getTimeZoneName(timezoneOffset);
		}else {
			concatValue = " UTC";
		}
		
		for (JsonNode row : rows) {
			List<String> parsedRow = new LinkedList<>();
			Iterator<Map.Entry<String, JsonNode>> fields = row.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> entry = fields.next();

				if (dateFields.contains(entry.getKey())) {
					dateValue = new Date(entry.getValue().asLong());
					if (!isUtcTimezone) {
						dateValue = LMUtils.convertServertoLocal(dateValue, timezoneOffset);
					}
					parsedRow.add(entry.getValue().asLong() != 0 ? 
							LMUtils.convertDateToStr(dateValue, "dd-MMM-yyyy HH:mm:ss.SSS").concat(concatValue)
									: null);
				} else {
 					if(entry.getValue().toString().indexOf("\\\"") > -1) {
						StringBuilder finalVal = new StringBuilder();
						String[] words = StringEscapeUtils.unescapeJava(entry.getValue().toString()).split("\"");
						for(String word : words) {
							if(StringUtils.isEmpty(finalVal)){
								finalVal.append('"');
							}else if(!StringUtils.isEmpty(finalVal) && !finalVal.toString().equals("\"")) {
								finalVal.append('"').append('"');
							}
							finalVal.append(word);
						}
						finalVal.append('"');
						parsedRow.add(finalVal.toString());
					}else {
						if(stringArrayFields != null && stringArrayFields.size() > 0 && stringArrayFields.contains(entry.getKey()) 
								&& entry.getValue() != null && entry.getValue().isArray() ) {
							StringBuilder columnValue = new StringBuilder();
							List<String> words = new ObjectMapper().convertValue(entry.getValue(), ArrayList.class);
							for(String word : words) {
								if(StringUtils.isEmpty(columnValue)){
									columnValue.append("\"[");
								}else {
									columnValue.append(",");
								}
								if(word == null) {
									columnValue.append(word);
								}else {
									columnValue.append("\"\"").append(word).append("\"\"");
								}
							}
							columnValue.append("]\"");
							parsedRow.add(columnValue.toString());
						}else {
							parsedRow.add(StringEscapeUtils.unescapeJava(entry.getValue().toString()));
						}
					}
				}
			}
			builder.append(String.join(separator, (Iterable) parsedRow)).append("\n");
		}
		return builder.toString();
	}
	
	
	@Override
	public String getExcel(String jsonBody, String exportPath, List<LmDruidFields> lmDruidFields, List<String> stringArrayFields) throws IOException {

        List<JsonNode> rows = new LinkedList<>();
        List<String> columns = new ArrayList<>();
        
        Map<String, String> mapByfield = lmDruidFields.stream().collect(Collectors.toMap(LmDruidFields::getField, LmDruidFields::getDisplayName));
        
		log.info("Getting data from Druid.");
        JsonNode json = druidService.queryTable(jsonBody); 
        
        if (json.isArray()) {
            for (JsonNode obj : json) {
                if(obj.get("events") != null) {
                	obj.get("events").forEach(rows::add);
                }
                if (columns.size() == 0 && obj.get("columns") != null) {
                	obj.get("columns").forEach(name -> columns.add(mapByfield.get(name.asText()) != null ? mapByfield.get(name.asText()) : name.asText()));
                }
            }
        } else {
        	if(json.get("events") != null) {
        		json.get("events").forEach(rows::add);
            }
        	if(columns.size() == 0 && json.get("columns") != null) {
        		json.get("columns").forEach(name -> columns.add(mapByfield.get(name.asText()) != null ? mapByfield.get(name.asText()) : name.asText()));
        	}
          }
		log.info("Download result size - {}", (rows != null) ? rows.size() : 0);
		
		return renderExcel(rows, exportPath, columns, lmDruidFields, stringArrayFields);
	}
	
	private String renderExcel(List<JsonNode> rows, String exportPath, List<String> columns, List<LmDruidFields> lmDruidColumns, List<String> stringArrayFields) throws IOException {
		log.info(String.format("=== renderExcel starts===="));
	    SXSSFWorkbook workbook = new SXSSFWorkbook(100);
	    SXSSFSheet sheet = workbook.createSheet("Sheet1");

	    AtomicInteger rowNum = new AtomicInteger(0);
	    Row headerRow = sheet.createRow(rowNum.getAndIncrement());
	    columns.forEach(col -> headerRow.createCell(columns.indexOf(col)).setCellValue(col));
	    
	    List<String> dateFields = lmDruidColumns.stream()
	            .filter(field -> LMConstants.LOG_MANAGEMENT_DATE_FIELDS_DATATYPE.equals(field.getDataType()))
	            .map(LmDruidFields::getField)
	            .collect(Collectors.toList());
	    
	    rows.forEach(row -> {
	    		    	
	        Row excelRow = sheet.createRow(rowNum.getAndIncrement());
	        AtomicInteger cellNum = new AtomicInteger(0);
	        row.fields().forEachRemaining(entry -> {
	        	
	            String key = entry.getKey();
	            JsonNode value = entry.getValue();

	            if (dateFields.contains(key)) {
	                Date dateValue = new Date(value.asLong());
	                Cell cell = excelRow.createCell(cellNum.getAndIncrement());
	                cell.setCellValue(value.asLong() != 0 ?
	                        LMUtils.convertDateToStr(dateValue, "dd-MMM-yyyy HH:mm:ss.SSS").concat(" UTC")
	                        : null);
	            } else {
	                if (value.toString().contains("\\\"")) {
	                } else {
	                    if (stringArrayFields != null && !stringArrayFields.isEmpty() && stringArrayFields.contains(key)
	                            && value != null && value.isArray()) {
	                    } else {
	                        Cell cell = excelRow.createCell(cellNum.getAndIncrement());
	                        String data = StringUtils.isNotBlank(value.asText())? value.asText() : LMConstants.BLANK_SPACE;
	                        String truncatedData = data.substring(0, Math.min(data.length(), LMConstants.CELL_MAX_CONTENT_LENGHT_LIMIT));
	                        cell.setCellValue(truncatedData);
	                        
	                    }
	                }
	            }
	        });

	        if (rowNum.get() % 100 == 0) {
	            try {
					((SXSSFSheet) sheet).flushRows(100);
				} catch (IOException e) {
					log.error(String.format("== Error is flushRows due to %s==", e.getLocalizedMessage()));
					e.printStackTrace();
				}
	        }
	    });

	    Date date = new Date();
	    String fileName = "LM_events" + date.getTime() + ".xlsx";
	    String fileLocation = exportPath + fileName;

	    try (FileOutputStream fileOutStream = new FileOutputStream(fileLocation)) {
	        workbook.write(fileOutStream);
	    }

	    workbook.close();
	    log.info(String.format("=== renderExcel ends===="));
	    return fileLocation;
	}

	@Transactional
	public List<LmDruidFields> getLmDruidColumns(String datasourceType) {
		return lmDruidFieldsRepository.findByDatasourceTypeAndIsDeleted(datasourceType, false);
	}

	@Transactional
	public List<LmDruidFieldsDTO> getLmDruidDTOColumns(String logManagementDatasourceType) {
//		return lmDruidFieldsRepository.findByDatasourceType(logManagementDatasourceType);
		return null;
	}
	
	public static String convertDateToStr(Date since, String dateFormate) {
		String dateStr = "";
		if (since != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
		    dateStr = sdf.format(since);
		}
		return dateStr;
	}
	
	@Override
	public DruidResponse searchQuery(DruidRequest request, String id, int requestedPage, boolean isDownload, List<String> columnsToUseForDownload, List<LmDruidFieldsDTO> lmDruidColumns) {
		boolean isFreeSearch = request.getIsFreeSearch();
		if (isFreeSearch) {
			updateDruidRequestForFreeSearch(request, lmDruidColumns);
		}
		
		JsonNode json = druidService.queryTable(request, requestedPage, isDownload, columnsToUseForDownload, lmDruidColumns);
		List<String> columns = new LinkedList<>();
		List<JsonNode> rows = new LinkedList<>();
		log.info("processing result set");
		if (json.isArray()) {
			for (JsonNode obj : json) {
				if (columns.isEmpty()) {
					if(obj.get("columns") != null) obj.get("columns").forEach(name -> columns.add(name.asText()));
				}				
				obj.get("events").forEach(rows::add);
			}
		} else {
			if(json.get("columns") != null) json.get("columns").forEach(name -> columns.add(name.asText()));
			json.get("events").forEach(rows::add);
		}
		log.info("processing result set done");
		Long count = (long) rows.size();
		if(LMConstants.ONE.equals(requestedPage) && !StringUtils.contains(request.getQueryString(), LMConstants.GROUP_SPACE)) {
			count = druidService.queryCount(request, requestedPage, isDownload, lmDruidColumns);
		}
		if(!LMConstants.ONE.equals(requestedPage)){ //Call count query only for first time search, not for pagination click
			count = -1L;
		}
		
		String newId = String.valueOf(request.hashCode());
		
		DruidResponse response = new DruidResponse(newId, columns, rows, count);
		
		log.info("fetching of data done from druid " + id);
		
		return response;
	}
	
	private void updateDruidRequestForFreeSearch(DruidRequest request, List<LmDruidFieldsDTO> lmDruidColumns) {
		boolean isDeepSearch = request.getIsDeepSearch();
		List<String> deepSearchFields = request.getDeepSearchFields();
		String value = request.getQueryString();
		StringBuilder query = new StringBuilder("");
		
		List<LmDruidFieldsDTO> fieldsToSearch = lmDruidColumns.stream()
				.filter(field -> !LMConstants.LOG_MANAGEMENT_DATE_FIELDS_DATATYPE.equals(field.getDataType()))
				.collect(Collectors.toList());
		
		//If its not deep search, then remove deep search columns from list.
		if (!isDeepSearch && deepSearchFields.size() > 0) {
			fieldsToSearch = fieldsToSearch.stream().filter(field -> !deepSearchFields.contains(field.getField()))
					.collect(Collectors.toList());
		}
		
		for (LmDruidFieldsDTO column : fieldsToSearch) {
			//When query is not empty, only then add OR condition
			if (!StringUtils.isEmpty(query)) {
				query.append(LMConstants.LOG_MANAGEMENT_FREE_SEARCH_CONDITION);
			}
			if (column.getDataType().equalsIgnoreCase(LMConstants.LOG_MANAGEMENT_STRING_FIELDS_DATATYPE)
					&& !value.startsWith(LMConstants.SINGLE_QUOTE)
					&& !value.endsWith(LMConstants.SINGLE_QUOTE)) {
				value = LMConstants.SINGLE_QUOTE + value + LMConstants.SINGLE_QUOTE;
			}
			query.append(column.getField()).append(LMConstants.LOG_MANAGEMENT_FREE_SEARCH_OPERATOR)
					.append(value);
		}
	
		request.setQueryString("FILTERS " + query.toString());
	}
	
	@Override
	public JsonNode countByEventIdForTopTenRecords(DruidRequest request, LogMgmtSearchByFieldsDTO searchGroupByDTO, List<LmDruidFieldsDTO> lmDruidColumns) {
		return druidService.queryCountByEventIdForTopTenRecords(request, searchGroupByDTO, lmDruidColumns);
	}
	
	@Override
	@Transactional
	public void saveSearchedQueryForTenant(LogManagementSaveSearchQueryDTO searchDTO) {
		String query = searchDTO.getQuery();
		if(!StringUtils.isEmpty(searchDTO.getIsFreeSearch()) && LMConstants.TRUE.equalsIgnoreCase(searchDTO.getIsFreeSearch())) {
			query = query.replace("'","");
			if(!StringUtils.isEmpty(searchDTO.getIsDeepSearch()) && LMConstants.TRUE.equalsIgnoreCase(searchDTO.getIsDeepSearch())) {
				query = "\"" + query + "\", Deep_Search";
			} else {
				query = "\"" + query + "\"";
			}
		}
			
		LmQueryHistory lmSaveQueryHistory = new LmQueryHistory();
		lmSaveQueryHistory.setCreatedDate(new Date());
		lmSaveQueryHistory.setQueryText(query);
		User user = new User();
		user.setUserId(searchDTO.getUserId());
		lmSaveQueryHistory.setUser(user);
		lmSaveQueryHistory.setApplicableTenantIds(searchDTO.getTenantIds());
		lmSaveQueryHistory.setDatasourceType(LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS);
		lmQueryHistoryRepository.save(lmSaveQueryHistory);
	}
	
	@Override
	@Transactional
	public ArrayList<LmQueryHistoryDTO> getSearchedQueries(Integer userId) {
		ArrayList<LmQueryHistoryDTO> searchHistory = new ArrayList<LmQueryHistoryDTO>();
		Long maxLimit = Long
				.valueOf(globalSettings.getGlobalSettingsParamValue(LMConstants.LOG_MANAGEMENT_PARAM_TYPE,
						LMConstants.LOG_MANAGEMENT_MAX_SEARCH_HISTORY_PARAM_NAME));
		lmQueryHistoryRepository.getQueryHistory(userId, LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS, maxLimit)
			.stream().forEach(s ->{
				Object[] obj = (Object[])s;
				searchHistory.add(new LmQueryHistoryDTO(
					(BigInteger)(obj[0]), // redId
					(Integer)(obj[3]), // executed_by
					(String)(obj[2]), // tenantd_ids
					(String)(obj[4]), // query_text
					(Date)(obj[5]), // created_date_value
					(String)(obj[1]) // tenant_names
			));
		}); 
		return searchHistory;
	}
	
	@Override
	@Transactional
	public void saveQueryForTenants(Integer userId , LogMgmtSaveQueryDTO saveQueryDTO, String queryName) throws Exception {
		Integer result = lmSavedQueryRepository.countByQueryName(queryName);
		if (result != 0) {
			throw new ValidationException("Query with this name already exists. Please use a different name.");
		} else {
			String query = saveQueryDTO.getQuery();
			if(!StringUtils.isEmpty(saveQueryDTO.getIsFreeSearch()) && LMConstants.TRUE.equalsIgnoreCase(saveQueryDTO.getIsFreeSearch())) {
				query = query.replace("'","");
				if(!StringUtils.isEmpty(saveQueryDTO.getIsDeepSearch()) && LMConstants.TRUE.equalsIgnoreCase(saveQueryDTO.getIsDeepSearch())) {
					query = "\"" + query + "\", Deep_Search";
				} else {
					query = "\"" + query + "\"";
				}
			} 
				
			LmSavedQuery lmSavedQuery = new LmSavedQuery();
			lmSavedQuery.setCreatedDate(new Date());
			lmSavedQuery.setQueryName(saveQueryDTO.getQueryName());
			lmSavedQuery.setQueryText(query);
			lmSavedQuery.setDatasourceType(saveQueryDTO.getDataSourceType());
			User user = new User();
			user.setUserId(userId);
			lmSavedQuery.setUser(user);
			if (saveQueryDTO.getTenantIds() != null && !saveQueryDTO.getTenantIds().isEmpty()
					&& !saveQueryDTO.getTenantIds().equals("all")) {
				lmSavedQuery.setTenantId(new Integer(saveQueryDTO.getTenantIds().split(",")[0]));
			} else {
				lmSavedQuery.setTenantId(null);
			}
			lmSavedQueryRepository.save(lmSavedQuery);
		}
	}


	@Override
	public ResponseEntity<String> validateQuery(DruidRequest request, List<String> fields, List<LmDruidFieldsDTO> lmDruidColumns) throws JsonProcessingException, ParseException, JSONException, ValidationException {
		return druidService.validateQuery(request, fields, lmDruidColumns);
	}
	
	@Override
	public Boolean cancelQuery(String queryId){
		try {
			druidService.cancelQuery(queryId);
			return true;
		} catch (QueryException e) {
			e.getStackTrace();
			return false;
		}
	}
	
	@Override
	public JsonNode getHistogramData(DruidRequest request, Date fromDateTime, Date toDateTime, List<LmDruidFieldsDTO> lmDruidColumns) {
		log.info("Request received in Histogram - {}");
		return druidService.getHistogramData(request, fromDateTime, toDateTime, lmDruidColumns, false);
	}
	
	@Override
	public JsonNode countForFieldColumns(DruidRequest request, List<LmDruidFieldsDTO> lmDruidFieldMostUsed, String[] fields, List<LmDruidFieldsDTO> lmDruidColumns) {
		return druidService.queryCountForFieldColumns(request, lmDruidFieldMostUsed, fields, lmDruidColumns);
	}
	
	@Override
	@Transactional
	public ArrayList<LogMgmtGetSavedQueryDTO> getSavedQueriesForNonPalUsers(List<Integer> tenantIds) {
		ArrayList<LogMgmtGetSavedQueryDTO> savedQueries = new ArrayList<LogMgmtGetSavedQueryDTO>();
		Long maxLimit = Long
				.valueOf(globalSettings.getGlobalSettingsParamValue(LMConstants.LOG_MANAGEMENT_PARAM_TYPE,
						LMConstants.LOG_MANAGEMENT_MAX_SAVED_QUERIES_PARAM_NAME));
		lmSavedQueryRepository.getSavedQueriesForNonPalUsers(LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS, tenantIds, maxLimit)
			.stream().forEach(s ->{
				Object[] obj = ((Object[])s);
				savedQueries.add(new LogMgmtGetSavedQueryDTO(
					(Integer)(obj[0]), //redId
					(Integer)(obj[1]), //createdBy
					(Integer)(obj[2]), //tenantId
					(String)(obj[3]), // queryName
					(String)(obj[4]), // queryText
					(Date)(obj[5]), // createdDate
					(String)(obj[6]), // tenantName
					(String)(obj[7])) // displayName
			);
		}); 
	
		return savedQueries;
	}
	
	@Override
	@Transactional
	public ArrayList<LogMgmtGetSavedQueryDTO> getSavedQueries(List<Integer> tenantIds) {
		ArrayList<LogMgmtGetSavedQueryDTO> savedQueries = new ArrayList<LogMgmtGetSavedQueryDTO>();
		Long maxLimit = Long
				.valueOf(globalSettings.getGlobalSettingsParamValue(LMConstants.LOG_MANAGEMENT_PARAM_TYPE,
						LMConstants.LOG_MANAGEMENT_MAX_SAVED_QUERIES_PARAM_NAME));
		lmSavedQueryRepository.getSavedQueries(LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS, tenantIds, maxLimit)
			.stream().forEach(s ->{
				Object[] obj = ((Object[])s);
				savedQueries.add(new LogMgmtGetSavedQueryDTO(
					(Integer)(obj[0]), //redId
					(Integer)(obj[1]), //createdBy
					(Integer)(obj[2]), //tenantId
					(String)(obj[3]), // queryName
					(String)(obj[4]), // queryText
					(Date)(obj[5]), // createdDate
					(String)(obj[6]), // tenantName
					(String)(obj[7])) // displayName
			);
		}); 
		return savedQueries;
	}
	
	@Override
	public JsonNode getColumnChart(DruidRequest request, List<LmDruidFieldsDTO> lmDruidColumns) {
		return druidService.getColumnChart(request, lmDruidColumns);
	}
	
	@Override
	public List<LmDruidFieldsDTO> getLmDruidColumnsDTO(String datasourceType){
		List<LmDruidFieldsDTO> list = new ArrayList<LmDruidFieldsDTO>();
		
		lmDruidFieldsRepository.findByDatasourceType(datasourceType).stream().forEach(s -> {
			
			list.add(new LmDruidFieldsDTO((LmDruidFields)s));
		});
		
		return list;
	}

}
