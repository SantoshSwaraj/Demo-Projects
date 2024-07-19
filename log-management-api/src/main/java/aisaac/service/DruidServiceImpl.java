package aisaac.service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import aisaac.model.DruidRequest;
import aisaac.model.LmDruidFieldsDTO;
import aisaac.model.LMDeepBiQueryContext;
import aisaac.model.LMDeepBiQuery;
import aisaac.model.LogMgmtSearchByFieldsDTO;
import aisaac.util.QueryUtils;
import aisaac.util.SecurityUtils;
import aisaac.utils.LMConstants;
import in.zapr.druid.druidry.aggregator.CardinalityAggregator;
import in.zapr.druid.druidry.aggregator.CountAggregator;
import in.zapr.druid.druidry.aggregator.DruidAggregator;
import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.exception.QueryException;
import in.zapr.druid.druidry.dataSource.UnionDataSource;
import in.zapr.druid.druidry.dimension.DefaultDimension;
import in.zapr.druid.druidry.dimension.SimpleDimension;
import in.zapr.druid.druidry.dimension.enums.OutputType;
import in.zapr.druid.druidry.granularity.DurationGranularity;
import in.zapr.druid.druidry.granularity.Granularity;
import in.zapr.druid.druidry.granularity.PeriodGranularity;
import in.zapr.druid.druidry.granularity.PredefinedGranularity;
import in.zapr.druid.druidry.granularity.SimpleGranularity;
import in.zapr.druid.druidry.query.DruidQuery;
import in.zapr.druid.druidry.query.aggregation.DruidTimeSeriesQuery;
import in.zapr.druid.druidry.query.aggregation.DruidTopNQuery;
import in.zapr.druid.druidry.query.aggregation.DruidTopNQuery.DruidTopNQueryBuilder;
import in.zapr.druid.druidry.query.config.Context;
import in.zapr.druid.druidry.query.config.Interval;
import in.zapr.druid.druidry.topNMetric.NumericMetric;
import in.zapr.druid.druidry.topNMetric.SimpleMetric;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DruidServiceImpl implements DruidService {
	
	private final DruidConfig config;
    private final ObjectMapper parser;
    private final DruidClient client;
    private AppConfig appConfig;
    private GlobalSettingsService globalSettings;
    
    @Autowired
    DruidServiceImpl(AppConfig appConfig, GlobalSettingsService globalSettings){
        this.parser = new ObjectMapper();
        this.config = appConfig.getDruidConfig();
        this.appConfig = appConfig;
        this.globalSettings = globalSettings;
        
        try{
            this.client = new DruidHttpClient(appConfig.getDruidConfig());
            this.client.connect();
        }
        catch (Exception ex){
        	log.error("Exception when connecting to Druid", ex);
            throw new RuntimeException("Exception when connecting to Druid", ex);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        client.close();
    }
    
    public JsonNode queryTable(String jsonBody) {

        try {
        	log.info("Calling SCAN query.");
        	String result = query(jsonBody);
        	
            log.info("Got result from druid for SCAN query.");
            return parser.readTree(result);
        }
        catch (Exception ex){
        	ex.printStackTrace();
        	log.error("Error in queryTable method.", ex);
            throw new RuntimeException("Error in queryTable method.", ex);
        }
    }
    
    @Override
    public JsonNode queryTable(DruidRequest request, int pageNum, boolean isDownload, List<String> columnsForQuery, List<LmDruidFieldsDTO> lmDruidColumns) {

        try {
        	log.info("Calling SCAN query.");
        	// Start : to consume query API
    		long offset = (pageNum - 1) * appConfig.getPageSize();
    		long queryLimit = appConfig.getPageSize();
    		StringBuilder queryString = new StringBuilder(request.getQueryString());
    		
    		if (isDownload) {
    			queryLimit = request.getIsLargeFileDownload() ? getMaxResults() : appConfig.getMaxUIResultSize();
    			offset = pageNum;
    		}else {
    			if(StringUtils.contains(request.getQueryString(), LMConstants.GROUP_SPACE)) {
    				queryLimit = appConfig.getInitialResultSize();
    				offset = LMConstants.DEFAULT_OFFSET;
    				if (pageNum * appConfig.getPageSize() > appConfig.getInitialResultSize() && pageNum * appConfig.getPageSize() <= appConfig.getMidResultSize()) {
    	    			queryLimit = appConfig.getMidResultSize();
    				}else if (pageNum * appConfig.getPageSize() > appConfig.getMidResultSize()) {
    	        			queryLimit = appConfig.getMaxUIResultSize();
    	        	}
    			}
    			
    		}
    		
    		if(!(StringUtils.contains(request.getQueryString(), LMConstants.GROUP_SPACE) || StringUtils.contains(request.getQueryString(), LMConstants.TOP_SPACE))) {
				queryString.append(LMConstants.HISTOGRAM_SELECT_CLAUSE);
    			for(String column : columnsForQuery) {
    				queryString.append(column);
    				if(!column.equals(columnsForQuery.get(columnsForQuery.size()-1))) {
    					queryString.append(",");
    				}
    			}
			}
    		
    		LMDeepBiQuery queryObj = formatQueryToJson(queryString.toString(), queryLimit, offset, lmDruidColumns, request); 
    		request.setQueryType("Scan");
    		
    		LMDeepBiQueryContext queryContext = new LMDeepBiQueryContext();
    		queryObj.setContext(queryContext);
    		
        	if(!StringUtils.isEmpty(queryObj.getGroupBy())) {
        		request.setQueryType("GroupBy");
        	}
        	String __time_displayName = lmDruidColumns.stream().filter(field -> "__time".equals(field.getField())).map(LmDruidFieldsDTO::getDisplayName).findFirst().orElse(null);
        	if(request.getQueryType().equalsIgnoreCase("Scan") && columnsForQuery.contains(__time_displayName) && request.getOrderByEventTimeDesc()) {
        		queryObj.setOrder("descending");
        	}    	
        	request.setQuery(queryObj);
        	log.info("Query type - "+request.getQueryType());
        	String jsonBody = getDruidJSONQuery(request);
        	String result = query(jsonBody);
        	
        	Map<String, String> mapByfield = lmDruidColumns.stream().collect(Collectors.toMap(LmDruidFieldsDTO::getField, LmDruidFieldsDTO::getDisplayName));
        	
        	if(!StringUtils.isEmpty(request.getQuery().getGroupBy())) {
        		ObjectNode outNode = parser.createObjectNode();
        		ArrayNode eventsArray = parser.createArrayNode();
        		ArrayNode colsArray = parser.createArrayNode();
        		JsonNode nodes = parser.readTree(result);
        		List<String> aggrCols = getGroupTopAggregationAlias(request.getQuery().getAggregators());
        		String[] groupTopCols = getGroupTopColumnName(request.getQuery().getGroupBy());
        		ObjectNode object = parser.createObjectNode();
				for (JsonNode node : nodes) {
					object = (ObjectNode) node.get("event");
					if (!isDownload) {				
						for(String groupTopCol : groupTopCols) {
							object.set(mapByfield.get(groupTopCol), node.get("event").get(groupTopCol));
						}
						for(int i =0;i<aggrCols.size();i++) {
							object.set("countVal"+i, node.get("event").get(aggrCols.get(i)));
							object.put("aggrCol"+i, aggrCols.get(i));
						}
						
					}
					if (isDownload && StringUtils.isEmpty(request.getQuery().getOrderBy())) {				
						for(int i =0;i<aggrCols.size();i++) {
							if(!columnsForQuery.contains(aggrCols.get(i))) {
								object.remove(aggrCols.get(i));
							}
						}
					}
					
					eventsArray.add(object);
				}
				if(eventsArray.size()>0) {
					Iterator<Entry<String, JsonNode>> it = eventsArray.get(0).fields();
			        while (it.hasNext()) {
			            Entry<String, JsonNode> field = it.next();     
						colsArray.add(field.getKey());					
			        }
				}            
                outNode.set("events", eventsArray);
                outNode.put("count",eventsArray.size());
                outNode.set("columns", colsArray);
                return outNode;
        	}
        	// End : to consume query API
            log.info("Got result from druid for SCAN query.");
            return parser.readTree(result);
        }
        catch (Exception ex){
        	ex.printStackTrace();
        	log.error("Error in queryTable method.", ex);
            throw new RuntimeException("Error in queryTable method.", ex);
        }
    }
    
    @Override
    public String invokeDownload(DruidRequest request, List<String> columnsForQuery, List<LmDruidFieldsDTO> lmDruidColumns, String downloadURL) {

        	log.info("Calling SCAN query.");
        	// Start : to consume query API
    		long queryLimit = request.getIsLargeFileDownload() ? getMaxResults() : appConfig.getMaxUIResultSize();
    		StringBuilder queryString = new StringBuilder(request.getQueryString());
    		
    		if(!(StringUtils.contains(request.getQueryString(), LMConstants.GROUP_SPACE) || StringUtils.contains(request.getQueryString(), LMConstants.TOP_SPACE))) {
				queryString.append(LMConstants.HISTOGRAM_SELECT_CLAUSE);
    			for(String column : columnsForQuery) {
    				queryString.append(column);
    				if(!column.equals(columnsForQuery.get(columnsForQuery.size()-1))) {
    					queryString.append(",");
    				}
    			}
			}
    		
    		LMDeepBiQuery queryObj = formatQueryToJson(queryString.toString(), queryLimit, LMConstants.DEFAULT_OFFSET, lmDruidColumns, request); 
    		request.setQueryType("Scan");
        	if(!StringUtils.isEmpty(queryObj.getGroupBy())) {
        		request.setQueryType("GroupBy");
        	}
        	String __time_displayName = lmDruidColumns.stream().filter(field -> "__time".equals(field.getField())).map(LmDruidFieldsDTO::getDisplayName).findFirst().orElse(null);
        	if(request.getQueryType().equalsIgnoreCase("Scan") && columnsForQuery.contains(__time_displayName) && request.getOrderByEventTimeDesc()) {
        		queryObj.setOrder("descending");
        	}    	
        	request.setQuery(queryObj);
        	log.info("Query type - "+request.getQueryType());
        	String jsonBody = getDruidJSONQuery(request);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			
			HttpHeaders requestHeader = new HttpHeaders();
			requestHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, requestHeader);
			ResponseEntity<String> response =  restTemplate.postForEntity(downloadURL, httpEntity, String.class);

			log.info("Response from Query Builder - {}", response);
			return Optional.ofNullable(response.getBody()).orElse(null);
			
    }
    
	private String query(String jsonBody) throws RuntimeException {
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			log.error("Druid JSON request - {}", jsonBody);

			HttpHeaders requestHeader = new HttpHeaders();
			requestHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
			requestHeader.add("Authorization", "Basic " + 
		    		Base64.getUrlEncoder().encodeToString((config.getUser()+":"+SecurityUtils.decrypt256Enc(config.getPwd())).getBytes(Charset.forName("UTF-8"))));
			HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody, requestHeader);
			ResponseEntity<String> response = restTemplate.postForEntity(config.getUrl(), httpEntity, String.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				if (response.hasBody())
					throw new HttpException(Optional.ofNullable(response.getBody()).orElse(""));
				else
					throw new HttpException("Status code: " + response.getStatusCodeValue());
			}

			log.info("Got response from druid.");
			return Optional.ofNullable(response.getBody()).orElse(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error during Druid call.", ex);
			throw new RuntimeException("Error during Druid call.", ex);
		}
	}

	@Override
    public JsonNode queryTopN(DruidRequest request) {
        List<JsonNode> responses = Arrays.stream(appConfig.getTopFields()).map(field -> {
            DruidQuery q = requestToTopNQuery(request, field);
            try {
                String result = client.query(q);
                ObjectNode obj = parser.createObjectNode();
                obj.set(field, parser.readTree(result));
                return obj;
            }
            catch (Exception ex) {
            	ex.printStackTrace();
            	log.error("Error in topN.", ex);
            	throw new RuntimeException("Error in topN.", ex);
            }
        }).collect(Collectors.toList());

        ArrayNode array = parser.createArrayNode();
        array.addAll(responses);

        return array;
    }

    @Override
    public Long queryCount(DruidRequest request, int pageNum, boolean isDownload, List<LmDruidFieldsDTO> lmDruidColumns) {
    	
    	log.info("Querying for main datatable count.");

        try {
    		
        	StringBuilder query = new StringBuilder(request.getQueryString()).append(LMConstants.HISTOGRAM_SELECT_CLAUSE);
        	if(!(StringUtils.contains(request.getQueryString(), LMConstants.GROUP_SPACE) || StringUtils.contains(request.getQueryString(), LMConstants.TOP_SPACE)) ) {
        		query.append(LMConstants.COUNT_VAL_FIELD);
        	}        	
    		
    		LMDeepBiQuery queryObj = formatQueryToJson(query.toString(), null, null, lmDruidColumns, request);
    		if(!StringUtils.isEmpty(queryObj.getGroupBy())) {
    			String[] column = getGroupTopColumnName(queryObj.getGroupBy());
    			queryObj.setAggregators(LMConstants.EXACTDISTINCTCOUNT.concat(StringUtils.join(column,",")).concat(")) AS ").concat(LMConstants.COUNT_VAL_COLUMN));
        	}
    		queryObj.setGroupBy("");
    		queryObj.setHaving("");
    		queryObj.setOrderBy("");
    		queryObj.setFields("");
    		request.setGranularity("all");
    		request.setQueryType("Timeseries");
        	request.setQuery(queryObj);
        	String jsonBody = getDruidJSONQuery(request);
        	String result = query(jsonBody);
        	
            log.info("get response for count" + result);
            JsonNode nodes = parser.readTree(result);
            long output = 0L;
            for (JsonNode node : nodes){
                JsonNode value = node.get("result").get("countVal");
                if(value.canConvertToLong()) {
                    long unpacked = value.longValue();
                    output += unpacked;
                }
            }
            log.info("done processing count" + output);
            return output;
        }
        catch (Exception ex){
        	ex.printStackTrace();
            return 0L;
        }
    }

    @Override
    public JsonNode queryCountByDate(DruidRequest request) {
        DruidQuery q = requestToCountByDateQuery(request);
        ArrayNode output = parser.createArrayNode();

        try {
            String result = client.query(q);
            JsonNode nodes = parser.readTree(result);

            for (JsonNode node : nodes){
                ObjectNode outNode = parser.createObjectNode();

                outNode.set("timestamp", node.get("timestamp"));
                outNode.set("rows", node.get("result").get("count"));

                output.add(outNode);
            }

            return output;
        }
        catch (Exception ex){
        	ex.printStackTrace();
            return output;
        }
    }

    private DruidQuery requestToCountByDateQuery(DruidRequest request) {
        Interval interval = QueryUtils.extractInterval(request);

        CountAggregator aggregator = new CountAggregator("count");
        
        Granularity granularity = PeriodGranularity.builder()
                .period("P1D")
                .timeZone(DateTimeZone.UTC)
                .build();

        Context context = Context.builder().skipEmptyBuckets(true).build();

        DruidTimeSeriesQuery.DruidTimeSeriesQueryBuilder builder = DruidTimeSeriesQuery.builder()
                .dataSource(new UnionDataSource(request.getDatasource()))
                .granularity(granularity)
                .context(context)
                .intervals(Collections.singletonList(interval))
                .aggregators(Collections.singletonList(aggregator));

        QueryUtils.buildFilter(request).map(builder::filter);

        return builder.build();
    }


    private DruidQuery requestToTimeSeriesQuery(DruidRequest request) {
        Interval interval = QueryUtils.extractInterval(request);

        CountAggregator aggregator = new CountAggregator("count");

        DruidTimeSeriesQuery.DruidTimeSeriesQueryBuilder builder = DruidTimeSeriesQuery.builder()
                .dataSource(new UnionDataSource(request.getDatasource()))
                .granularity(new SimpleGranularity(PredefinedGranularity.ALL))
                .intervals(Collections.singletonList(interval))
                .aggregators(Collections.singletonList(aggregator));

        QueryUtils.buildFilter(request).map(builder::filter);

        return builder.build();
    }

    private DruidQuery requestToTopNQuery(DruidRequest request, String dimension) {
        Interval interval = QueryUtils.extractInterval(request);

        CountAggregator aggregator = new CountAggregator("count");

        DruidTopNQueryBuilder builder = DruidTopNQuery.builder()
                .dataSource(new UnionDataSource(request.getDatasource()))
                .granularity(new SimpleGranularity(PredefinedGranularity.ALL))
                .dimension(new SimpleDimension(dimension))
                .intervals(Collections.singletonList(interval))
                .aggregators(Collections.singletonList(aggregator))
                .topNMetric(new SimpleMetric("count"));

        return builder.build();
    }
    
    public long getMaxResults() {
    	return Long.valueOf(globalSettings.getGlobalSettingsParamValue(LMConstants.LOG_MANAGEMENT_PARAM_TYPE, LMConstants.LOG_MANAGEMENT_MAX_RESULTS_PARAM_NAME));
    }
    
    public int getMaxPages() {
    	return (int) (getMaxResults() / appConfig.getPageSize());
    }

	@Override
	public JsonNode queryCountByDuration(DruidRequest request, Date fromDate, Date toDate) {
		log.info("start : calling druid for histogram.");
        DruidQuery q = null;
        ObjectNode object = null;
        long totalCount = 0L;
        try {
        	long duration = getDurationValue(fromDate, toDate);
        	q = requestToCountByDuration(request, duration);
        	log.info("calling druid for scan");
            String result = client.query(q);
            log.info("got result from druid");
            JsonNode values = parser.readTree(result);
            for(JsonNode node: values) {
            	object = (ObjectNode) node;
            	object.set("count", node.get("result").get("count"));
            	object.put("duration", duration);
            	object.remove("result");
            	totalCount += object.get("count").asLong();
            }
            if(totalCount == 0L) return null;
            return values;
        }
        catch (Exception ex){
        	ex.printStackTrace();
        	log.error("Error during Druid call for queryCountByDuration", ex);
            throw new RuntimeException("Error during Druid call for queryCountByDuration", ex);
        }
	}

	private DruidQuery requestToCountByDuration(DruidRequest request, long duration) {
		Interval interval = QueryUtils.extractInterval(request);

        CountAggregator aggregator = new CountAggregator("count");
        
        Granularity granularity = new DurationGranularity(duration);

        Context context = Context.builder().skipEmptyBuckets(false).build();

        DruidTimeSeriesQuery.DruidTimeSeriesQueryBuilder builder = DruidTimeSeriesQuery.builder()
                .dataSource(new UnionDataSource(request.getDatasource()))
                .granularity(granularity)
                .context(context)
                .intervals(Collections.singletonList(interval))
                .aggregators(Collections.singletonList(aggregator));

        QueryUtils.buildFilter(request).map(builder::filter);

        return builder.build();
	}

	private long getDurationValue(Date fromDate, Date toDate) {
		long timePerBar = (toDate.getTime() - fromDate.getTime()) / (appConfig.getHistogramBarsLimit());
		if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_0_SECOND, LMConstants.MILLISECONDS_IN_1_SECOND)) {
			return LMConstants.MILLISECONDS_IN_1_SECOND;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_1_SECOND, LMConstants.MILLISECONDS_IN_5_SECONDS)) {
			return LMConstants.MILLISECONDS_IN_5_SECONDS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_5_SECONDS, LMConstants.MILLISECONDS_IN_10_SECONDS)) {
			return LMConstants.MILLISECONDS_IN_10_SECONDS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_10_SECONDS, LMConstants.MILLISECONDS_IN_30_SECONDS)) {
			return LMConstants.MILLISECONDS_IN_30_SECONDS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_30_SECONDS, LMConstants.MILLISECONDS_IN_1_MINUTE)) {
			return LMConstants.MILLISECONDS_IN_1_MINUTE;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_1_MINUTE, LMConstants.MILLISECONDS_IN_2_MINUTES)) {
			return LMConstants.MILLISECONDS_IN_2_MINUTES;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_2_MINUTES, LMConstants.MILLISECONDS_IN_5_MINUTES)) {
			return LMConstants.MILLISECONDS_IN_5_MINUTES;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_5_MINUTES, LMConstants.MILLISECONDS_IN_10_MINUTES)) {
			return LMConstants.MILLISECONDS_IN_10_MINUTES;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_10_MINUTES, LMConstants.MILLISECONDS_IN_30_MINUTES)) {
			return LMConstants.MILLISECONDS_IN_30_MINUTES;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_30_MINUTES, LMConstants.MILLISECONDS_IN_1_HOUR)) {
			return LMConstants.MILLISECONDS_IN_1_HOUR;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_1_HOUR, LMConstants.MILLISECONDS_IN_2_HOURS)) {
			return LMConstants.MILLISECONDS_IN_2_HOURS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_2_HOURS, LMConstants.MILLISECONDS_IN_6_HOURS)) {
			return LMConstants.MILLISECONDS_IN_6_HOURS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_6_HOURS, LMConstants.MILLISECONDS_IN_12_HOURS)) {
			return LMConstants.MILLISECONDS_IN_12_HOURS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_12_HOURS, LMConstants.MILLISECONDS_IN_1_DAY)) {
			return LMConstants.MILLISECONDS_IN_1_DAY;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_1_DAY, LMConstants.MILLISECONDS_IN_2_DAYS)) {
			return LMConstants.MILLISECONDS_IN_2_DAYS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_2_DAYS, LMConstants.MILLISECONDS_IN_5_DAYS)) {
			return LMConstants.MILLISECONDS_IN_5_DAYS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_5_DAYS, LMConstants.MILLISECONDS_IN_10_DAYS)) {
			return LMConstants.MILLISECONDS_IN_10_DAYS;
		}else {
			return LMConstants.MILLISECONDS_IN_15_DAYS;
		}
		
	}

	private boolean timeValueBetween(long timePerBar, Long val1, Long val2) {
		return timePerBar > val1 && timePerBar <= val2;
	}
	
	
	private long getDurationValueForTH(Date fromDate, Date toDate) {
		long timePerBar = (toDate.getTime() - fromDate.getTime()) / (appConfig.getHistogramBarsLimit());
		
		if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_0_SECOND, LMConstants.MILLISECONDS_IN_1_DAY)) {
			return LMConstants.MILLISECONDS_IN_1_DAY;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_1_DAY, LMConstants.MILLISECONDS_IN_2_DAYS)) {
			return LMConstants.MILLISECONDS_IN_2_DAYS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_2_DAYS, LMConstants.MILLISECONDS_IN_5_DAYS)) {
			return LMConstants.MILLISECONDS_IN_5_DAYS;
		}else if(timeValueBetween(timePerBar, LMConstants.MILLISECONDS_IN_5_DAYS, LMConstants.MILLISECONDS_IN_10_DAYS)) {
			return LMConstants.MILLISECONDS_IN_10_DAYS;
		}else {
			return LMConstants.MILLISECONDS_IN_15_DAYS;
		}
	}
	
	@Override
	public JsonNode queryCountByEventIdForTopTenRecords(DruidRequest request, LogMgmtSearchByFieldsDTO logMgmtSearchGroupByDTO, List<LmDruidFieldsDTO> lmDruidColumns) {
		
		JsonNode outputNode=null;
		Map<String, String> displayNameFieldNameMap = new HashMap<>();
		try {
			
			log.info("start : calling druid for queryCountByEventIdForTopTenRecords");
        	StringBuilder query = new StringBuilder(request.getQueryString()).append(
        			LMConstants.TOP_TEN_QUERY.replaceAll(LMConstants.GROUP_BY_COLUMN, logMgmtSearchGroupByDTO.getFieldColumn()));
        	LMDeepBiQuery inputQuery = formatQueryToJson(query.toString(), LMConstants.TOP_TEN_QUERY_LIMIT, LMConstants.DEFAULT_OFFSET, lmDruidColumns, request);
        	if(!inputQuery.getLimit().equalsIgnoreCase(String.valueOf(LMConstants.TOP_TEN_QUERY_LIMIT))){
        		inputQuery.setLimit(String.valueOf(LMConstants.TOP_TEN_QUERY_LIMIT));
        	}
        	inputQuery.setTopNMetric("numeric");
        	inputQuery.setThreshold("10");
        	request.setQuery(inputQuery);
        	request.setQueryType("TopN");
        	String jsonBody = getDruidJSONQuery(request);
        	String result = query(jsonBody);
            log.info("got result from druid queryCountByEventIdForTopTenRecords"+result);
            JsonNode nodes = parser.readTree(result);
            ObjectNode outNode = parser.createObjectNode();
            
            for (LmDruidFieldsDTO dto : lmDruidColumns) {
            	displayNameFieldNameMap.put(dto.getDisplayName(), dto.getField());
    		}
            
            for(JsonNode node : nodes) {
            	outputNode = node.get("result");
            	for(JsonNode resultnode : node.get("result")) {
            		outNode = (ObjectNode) resultnode;
            		outNode.put("columnValue", resultnode.get(displayNameFieldNameMap.get(logMgmtSearchGroupByDTO.getFieldColumn())));
            		outNode.put("count", resultnode.get("countVal"));            	
            	}    
            }
           return outputNode;
        }
        catch (Exception ex){
        	ex.printStackTrace();
        	log.error("Exception while querying queryCountByEventIdForTopTenRecords", ex);
            throw new RuntimeException("Exception while querying queryCountByEventIdForTopTenRecords", ex);
        }
	}
	
/**
 * SELECT evId,COUNT(*) as counter
FROM "1031-input"
WHERE "__time" >= CURRENT_TIMESTAMP - INTERVAL '31' DAY 
GROUP BY evId ORDER BY counter DESC LIMIT 10
 * @param request
 * @param fieldType
 * @param fieldColumn
 * @return
 */
	private DruidQuery requestToCountGroupByEventIdForTopTen(DruidRequest request, String fieldType, String fieldColumn) {
		Interval interval = QueryUtils.extractInterval(request);

        CountAggregator aggregator = new CountAggregator("count");
      
        DruidTopNQueryBuilder builder = DruidTopNQuery.builder()
                .dataSource(new UnionDataSource(request.getDatasource()))
                .granularity(new SimpleGranularity(PredefinedGranularity.ALL))
                .dimension(new DefaultDimension(fieldColumn,"eventId",fieldType.equalsIgnoreCase("STRING")? OutputType.STRING : OutputType.LONG))
                .intervals(Collections.singletonList(interval))
                .aggregators(Collections.singletonList(aggregator))
                .topNMetric(new NumericMetric("count"))
                .threshold(10);
        		
        QueryUtils.buildFilter(request).map(builder::filter);

        return builder.build();
	}
	
    @Override
	public JsonNode queryCountForFieldColumns(DruidRequest request, List<LmDruidFieldsDTO> lmDruidFieldMostUsed, String[] fields, List<LmDruidFieldsDTO> lmDruidColumns) {
		log.info("querying for queryCountForFieldColumns");

		JsonNode outputNode = null;
		try {
			//DruidQuery q = requestToTimeSeriesQueryForFieldColumns(request, config.columnsMostUsed());
			StringBuilder query = new StringBuilder(request.getQueryString()).append(LMConstants.HISTOGRAM_SELECT_CLAUSE);
			for(String column: fields) {
				query.append(LMConstants.EXACTDISTINCTCOUNT).append(column).append(")) AS ").append(column).append(LMConstants.COUNT_VAL_COLUMN);
				if(!column.equals(fields[fields.length-1])) {
					query.append(",");
				}
			}
			
        	request.setQuery(formatQueryToJson(query.toString(), appConfig.getPageSize(), LMConstants.DEFAULT_OFFSET, lmDruidColumns, request));
        	request.setQueryType("Timeseries");
        	
			String jsonBody = getDruidJSONQuery(request);
        	String result = query(jsonBody);
			log.info("get response for queryCountForFieldColumns--" + result);
			JsonNode nodes = parser.readTree(result);
			ArrayNode resultNodes = parser.createArrayNode();
            
			for(JsonNode node : nodes) {
            	outputNode = node.get("result");
            	ObjectNode object = (ObjectNode) node;
        	   
            	for(String column: fields) {
        		   
            		LmDruidFieldsDTO fieldDto = lmDruidFieldMostUsed.stream()
        				   .filter(druidMostUsedField -> column.equals(druidMostUsedField.getDisplayName()))
                           .findAny()
                           .orElse(null); 
        		   
            		object.with(column+"countVal").put("count", node.get("result").get(fieldDto.getField()+"countVal"));
            		object.with(column+"countVal").put("columnName", fieldDto.getField()); 
                  
            		object.with(column+"countVal").put("columnDatatype", fieldDto.getDataType()); 
            		object.with(column+"countVal").put("columnDisplayName", fieldDto.getDisplayName()); 
            		resultNodes.add(object.get(column+"countVal"));
        	   }
            	
        	   object.with("result").put("newResult", resultNodes);
               outputNode = object.with("result");
               break;
            }
           return outputNode;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Exception while querying queryCountForFieldColumns", ex);
			throw new RuntimeException("Exception while querying queryCountForFieldColumns", ex);
		}
	}
	/**
	 * SELECT count(DISTINCT(evId)) as evIdCount
FROM "1031-input"
	 * @param request
	 * @param searchGroupByDTO
	 * @return
	 */
	private DruidQuery requestToTimeSeriesQueryForFieldColumns(DruidRequest request,
			List<String> columns) {
		Interval interval = QueryUtils.extractInterval(request);
		
		List<String> fieldColumns = null;
		CardinalityAggregator cardinalityAggregator = null;
		List<DruidAggregator> aggregators = new ArrayList<>();
		
		for(String column : columns) {		
				fieldColumns = new ArrayList<String>();
				fieldColumns.add(column);		
				cardinalityAggregator = CardinalityAggregator.builder()
						.name(column+"Count")
						.fields(fieldColumns)
						.byRow(false)
						.build();
				aggregators.add(cardinalityAggregator);		
		}
		
		Context context = Context.builder().skipEmptyBuckets(true).build();
		DruidTimeSeriesQuery.DruidTimeSeriesQueryBuilder builder = DruidTimeSeriesQuery.builder()
				.dataSource(new UnionDataSource(request.getDatasource()))
				.granularity(new SimpleGranularity(PredefinedGranularity.ALL))
				.intervals(Collections.singletonList(interval))
				.context(context)
				.aggregators(aggregators);

		QueryUtils.buildFilter(request).map(builder::filter);

		return builder.build();
		
	}
	
	/**
	 * calling deep bi to get the json input for druid
	 * @param request
	 * @return
	 * @throws QueryException
	 */
	public String getDruidJSONQuery(DruidRequest request) throws RuntimeException {
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			String body = mapper.writeValueAsString(request);
			log.info("Request JSON to Query Builder - {}", body);
			
			HttpHeaders requestHeader = new HttpHeaders();
			requestHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<String> httpEntity = new HttpEntity<>(body, requestHeader);
			ResponseEntity<String> response =  restTemplate.postForEntity(config.getQueryApiURL(), httpEntity, String.class);

			if (response.getStatusCode() != HttpStatus.OK){
	              if (response.hasBody()) throw new HttpException(Optional.ofNullable(response.getBody()).orElse(""));
	              else throw new HttpException("Status code: "+ response.getStatusCodeValue());
	          }
			
			log.info("Response from Query Builder - {}", response);
			return Optional.ofNullable(response.getBody()).orElse(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Exception during call to DruidQueryBuilder.", ex);
			throw new RuntimeException("Exception during call to DruidQueryBuilder.", ex);
		}
	}

	private LMDeepBiQuery formatQueryToJson( String query, Long queryLimit, Long offset, List<LmDruidFieldsDTO> lmDruidColumns, DruidRequest request) {
		
		Boolean isUtcTimezone = request.getIsUtcTime();
		Integer timezoneOffset = request.getTimezoneOffset();
		
		for (LmDruidFieldsDTO dto : lmDruidColumns) {
			if(query.contains(dto.getDisplayName())) {
				query = query.replace(dto.getDisplayName(), dto.getField());
			}
		}
		
		//String[] tags = query.split(Pattern.quote("|"));
		List<String> tags = getTags(query, request);
		LMDeepBiQuery  deepBiQuery = new LMDeepBiQuery();	
		Map<String, ArrayList<String>> groupClauseMap = new HashMap<String, ArrayList<String>>();
		String fields = "";
		List<String> timestampFields = new ArrayList<>();
		List<String> dateTimeFields = lmDruidColumns.stream().filter(field -> LMConstants.LOG_MANAGEMENT_DATE_FIELDS_DATATYPE.equals(field.getDataType())).map(LmDruidFieldsDTO::getField).collect(Collectors.toList());
		
		for(String tag : tags) {
			if(tag.trim().startsWith(LMConstants.FILTERS_SPACE)) {
				String filters = ((tag.replace(LMConstants.FILTERS_SPACE, "")).replaceAll(LMConstants.REGEX, LMConstants.SIMILAR_TO)).trim().replaceAll("\\s{2,}", " ").replaceAll(LMConstants.LIKE_CI_STRING, LMConstants.SIMILAR_TO_STRING);
				for(String field : dateTimeFields) {
					if(tag.contains(field + LMConstants.BLANK_SPACE)) {
						timestampFields.add(field);
						if(!isUtcTimezone) {
							filters = replaceTimeValueInQuery(filters, field, timezoneOffset);
						}
					}
				}
				deepBiQuery.setFilters(filters);	
			}else if(tag.trim().startsWith(LMConstants.FIELDS_SPACE)) {
				fields = tag.replace(LMConstants.FIELDS_SPACE, "").trim();
				if(StringUtils.containsIgnoreCase(fields, LMConstants.EXACTDISTINCTCOUNT) || StringUtils.containsIgnoreCase(fields, LMConstants.COUNT_VAL_FIELD)) {
					deepBiQuery.setAggregators(fields);
					fields ="";
				}
				if(StringUtils.containsIgnoreCase(fields, LMConstants.CARDINALITYDISTINCT) || StringUtils.containsIgnoreCase(fields, LMConstants.COUNT_VAL_FIELD)) {
					deepBiQuery.setAggregators(fields);
					fields ="";
				}
			}else if(tag.trim().startsWith(LMConstants.GROUP_SPACE)) {		
				String group = tag.replace(LMConstants.GROUP_SPACE, "").trim();				
				if(!group.isEmpty()) {
				
					//String[] groupCriteria = group.split(",");
					List<String> groupCriteriaList = new ArrayList<String>();
					if(StringUtils.containsIgnoreCase(group, LMConstants.AGGR)) {
						groupCriteriaList.add(group.substring(0,group.indexOf(LMConstants.AGGR)));
						if(StringUtils.containsIgnoreCase(group, LMConstants.HAVING)) {
							groupCriteriaList.add(group.substring(group.indexOf(LMConstants.AGGR),group.indexOf(LMConstants.HAVING)));
							groupCriteriaList.add(group.substring(group.indexOf(LMConstants.HAVING), group.length()));
						}else {
							groupCriteriaList.add(group.substring(group.indexOf(LMConstants.AGGR), group.length()));
						}
					}else if(StringUtils.containsIgnoreCase(group, LMConstants.HAVING) && !StringUtils.containsIgnoreCase(group, LMConstants.AGGR)) {
						groupCriteriaList.add(group.substring(0,group.indexOf(LMConstants.HAVING)));
						groupCriteriaList.add(group.substring(group.indexOf(LMConstants.HAVING),group.length()));
					}else if(!StringUtils.containsIgnoreCase(group, LMConstants.HAVING) && !StringUtils.containsIgnoreCase(group, LMConstants.AGGR)) {
						groupCriteriaList.add(group.substring(0,group.length()));
					}
					if(!fields.isEmpty() && StringUtils.containsIgnoreCase(fields, LMConstants.COUNT_VAL_FIELD)) {
						fields = StringUtils.remove(fields, LMConstants.COUNT_VAL_FIELD);						
					}
					if(!fields.isEmpty()) fields+=",";

					/**
					 * For multiple aggregation and multiple group by
					 */
					groupClauseMap.put("groupColumn", new ArrayList<String>());
					groupClauseMap.put("aggregationColumns", new ArrayList<String>());
					groupClauseMap.put("havingColumns", new ArrayList<String>());
					for(String groupCriteria : groupCriteriaList) {
						if(groupCriteria.trim().startsWith(LMConstants.AGGR)) {		
							String groupAggrEntry = groupCriteria.replace(LMConstants.AGGR, "").trim();
							if(groupAggrEntry.contains(",")) {
								String[] groupAggrArr = groupAggrEntry.split(",");
								for(String groupAggr : groupAggrArr) {
									groupClauseMap.get("aggregationColumns").add(processFormatAggrQuery(groupAggr));
								}
							}else {
								groupClauseMap.get("aggregationColumns").add(processFormatAggrQuery(groupAggrEntry));
							}
							
						}else if(groupCriteria.trim().startsWith(LMConstants.HAVING)) {	
							String groupHavingEntry = ((groupCriteria.replace(LMConstants.HAVING, "")).replaceAll(LMConstants.REGEX, LMConstants.SIMILAR_TO)).trim().replaceAll("\\s{2,}", " ").replaceAll(LMConstants.LIKE_CI_STRING, LMConstants.SIMILAR_TO_STRING);
							if(groupHavingEntry.trim().contains(",")){
								String[] groupHavingArr = groupHavingEntry.trim().split(",");
								for(String groupHavingCol : groupHavingArr) {
									groupClauseMap.get("havingColumns").add(groupHavingCol.trim());
								}
							}else {
								groupClauseMap.get("havingColumns").add(groupHavingEntry);
							}
							
							for(String field : dateTimeFields) {
								if(groupHavingEntry.contains(field)) {
									timestampFields.add(field);
								}
							}
						}else {
							if(groupCriteria.trim().contains(",")){
								String[] groupColumnsArr = groupCriteria.trim().split(",");
								for(String groupCol : groupColumnsArr) {
									groupClauseMap.get("groupColumn").add(groupCol.trim());
								}
							}else {
								groupClauseMap.get("groupColumn").add(groupCriteria.trim());
							}
							
						}
					}
					deepBiQuery.setGroupBy(StringUtils.join(groupClauseMap.get("groupColumn"), ","));
		            if(!groupClauseMap.get("aggregationColumns").isEmpty()) {
		            	deepBiQuery.setAggregators(StringUtils.join(groupClauseMap.get("aggregationColumns"),","));
		            }
		            if(!groupClauseMap.get("havingColumns").isEmpty()) {
		            	deepBiQuery.setHaving(StringUtils.join(groupClauseMap.get("havingColumns"),","));
		            }
				}				
			}else if(tag.trim().startsWith(LMConstants.SORT_SPACE)) {
				String sortClause = tag.replace(LMConstants.SORT_SPACE, "").trim();
				sortClause = appendSortType(sortClause, lmDruidColumns);
				deepBiQuery.setOrderBy(sortClause);
			}else if(tag.trim().startsWith(LMConstants.TOP_SPACE)) {
				String top = tag.replace(LMConstants.TOP_SPACE, "").trim();
				if(!top.isEmpty()) {				
					String[] topCriteria = top.split(" ", 2);					
					if(topCriteria.length == 2 && !topCriteria[1].isEmpty()) {
						deepBiQuery.setGroupBy(topCriteria[1].trim());
						if(!fields.isEmpty() && StringUtils.containsIgnoreCase(fields, LMConstants.COUNT_VAL_FIELD)) {
							fields = StringUtils.remove(fields, LMConstants.COUNT_VAL_FIELD);						
						}
						if(!fields.isEmpty()) fields+=",";
						fields +=LMConstants.COUNT_VAL_FIELD;
						deepBiQuery.setOrderBy(LMConstants.COUNT_VAL_COLUMN+" desc");
						deepBiQuery.setLimit(topCriteria[0].trim());
					}else if(topCriteria.length == 1 && !topCriteria[0].isEmpty()){
						deepBiQuery.setGroupBy(topCriteria[0].trim());
						if(!fields.isEmpty() && StringUtils.containsIgnoreCase(fields, LMConstants.COUNT_VAL_FIELD)) {
							fields = StringUtils.remove(fields, LMConstants.COUNT_VAL_FIELD);						
						}
						if(!fields.isEmpty()) fields+=",";
						fields +=LMConstants.COUNT_VAL_FIELD;
						deepBiQuery.setOrderBy(LMConstants.COUNT_VAL_COLUMN+" desc");
					}
				}
			}
		}
		if(StringUtils.containsIgnoreCase(fields, LMConstants.CARDINALITYDISTINCT) || StringUtils.containsIgnoreCase(fields, LMConstants.COUNT_VAL_FIELD)) {
			deepBiQuery.setAggregators(fields);
			fields ="";
		}
		if(!fields.isEmpty() && deepBiQuery.getAggregators().isEmpty()) {
			deepBiQuery.setFields(fields);
		}
		if(StringUtils.isEmpty(deepBiQuery.getLimit()) && queryLimit != null) {
			deepBiQuery.setLimit(queryLimit.toString());
			deepBiQuery.setOffset(offset);
		}
		if(timestampFields.size() > 0) {
			deepBiQuery.setTimestampFields(timestampFields.stream().toArray(String[] ::new));
		}
		
		return deepBiQuery;
	}
	
	private String replaceTimeValueInQuery(String filters, String field, Integer timezoneOffset) {
		
		String[] parts = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
		Calendar dateReplaceValue = Calendar.getInstance();
		Date queryDate = null;
		String dateReplaceString = null;
		
		String[] conditions = filters.split("AND NOT|OR NOT|AND|OR");
		
		for(String condition : conditions) {
			if(condition.contains(field)) {
				parts = condition.split("'");
				try {
					queryDate = dateFormat.parse(parts[1]);
					dateReplaceValue.setTime(queryDate);
					dateReplaceValue.add(Calendar.MINUTE, timezoneOffset);
					dateReplaceString = dateFormat.format(dateReplaceValue.getTime());
					filters = filters.replaceAll(parts[1], dateReplaceString);
				} catch (ParseException e) {
					log.error("Exception while trying to convert local date to UTC for filter query condition - {}", condition);
					e.printStackTrace();
				}
			}
		}
		return filters;
	}

	private List<String> getTags(String input, DruidRequest request) {
		Boolean thFiltersPresent = false;
		String thFiltersTag = " modelName IN (" + request.getModels() + ") AND tenantId = " +request.getTenantId();
		List<String> tags = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		if(StringUtils.isEmpty(input) && !request.getIsTH()) {
			return result;
		}else if(!StringUtils.isEmpty(input) && input.indexOf("|") == -1) {
			result.add(input);
		}else {
			int start = 0;
			boolean inQuotes = false;
			for (int current = 0; current < input.length(); current++) {
			    if (input.charAt(current) == '\'') inQuotes = !inQuotes; // toggle state
			    else if (input.charAt(current) == '|' && !inQuotes) {
			        result.add(input.substring(start, current));
			        start = current + 1;
			    }
			}
			result.add(input.substring(start));
		}
		if(request.getIsTH()) {
			if (!StringUtils.isEmpty(input)) {
				String tagStr = null;
				for (String tag : result) {
					tagStr = tag;
					if (tag.trim().startsWith(LMConstants.FILTERS_SPACE)) {
						tagStr = LMConstants.FILTERS_SPACE + "(" + tagStr.replace(LMConstants.FILTERS_SPACE, "") + ")" + " AND " + thFiltersTag;
						thFiltersPresent = true;
					}
					tags.add(tagStr);
				} 
			}
			if(!thFiltersPresent) {
				thFiltersTag = "FILTERS " + thFiltersTag;
				tags.add(thFiltersTag);
			}
			result = tags;
		}
		return result;
	}

	private String appendSortType(String sortClause, List<LmDruidFieldsDTO> totaldruidFields) {
		String sortColumn = sortClause.split(" ")[0].trim();
		String finalSortClause = sortClause;
		 LmDruidFieldsDTO fieldDto = totaldruidFields.stream()
                 .filter(druidField -> sortColumn.equals(druidField.getField()))
                 .findAny()
                 .orElse(null);
         if(fieldDto!=null && fieldDto.getDataType().equalsIgnoreCase("STRING")) {
        	 finalSortClause= LMConstants.SORT_STRING_TYPE.concat(sortColumn +") ").concat(sortClause.split(" ").length>1?sortClause.split(" ")[1].trim():"ASC");
         }
		return finalSortClause;
	}

	private String processFormatAggrQuery(String groupAggrEntry) {	
		String aggrType= groupAggrEntry.split("\\(")[0].trim().toLowerCase();
		String aggrCol= groupAggrEntry.substring(groupAggrEntry.indexOf('(')+1,groupAggrEntry.indexOf(')'));
		String aggrColAlias = LMConstants.COUNT_VAL_COLUMN;
		switch(aggrType) {
			case LMConstants.SUM : aggrType = LMConstants.SUMLONG;
									   	break;
			case LMConstants.MIN : aggrType = LMConstants.MINLONG;
			   							break;
			case LMConstants.MAX : aggrType = LMConstants.MAXLONG;
			   							break;
			case LMConstants.MEAN : aggrType = LMConstants.MEANDOUBLE;
			   							break;
			case LMConstants.COUNT : aggrType = LMConstants.COUNT;
										break;
		}
		if(StringUtils.containsIgnoreCase(groupAggrEntry," AS ")) {
			aggrColAlias = groupAggrEntry.split(" ")[groupAggrEntry.split(" ").length-1];
		}else if(!aggrType.equalsIgnoreCase(LMConstants.COUNT)) {
			aggrColAlias = groupAggrEntry.split("\\(")[0].trim().toLowerCase()+aggrCol;
		}
		return (aggrType + " (" + aggrCol + ") AS " + aggrColAlias);
	}
	private String formatQuery(String query, long queryLimit) {
		String[] tags = query.split(Pattern.quote("|"));
		StringBuilder queryStr = new StringBuilder();
		for(String tag : tags) {
			if(tag.trim().startsWith(LMConstants.FILTERS)) {
				queryStr.append("<filters>").append(tag.replace(LMConstants.FILTERS, "").trim()).append("</filters>");
			}else if(tag.trim().startsWith(LMConstants.FIELDS)) {
				queryStr.append("<fields>").append(tag.replace(LMConstants.FIELDS, "").trim()).append("</fields>");
			}else if(tag.trim().startsWith(LMConstants.GROUP)) {
				queryStr.append(convertGroupClause(tag.replace(LMConstants.GROUP, "").trim()));
			}else if(tag.trim().startsWith(LMConstants.SORT)) {
				queryStr.append("<orderBy>").append(tag.replace(LMConstants.SORT, "").trim()).append("</orderBy>");
			}else if(tag.trim().startsWith(LMConstants.TOP)) {
				queryStr.append(convertTopClause(tag.replace(LMConstants.TOP, "").trim()));
			}
		}
		
		if(queryStr.indexOf("<fields>") == -1) {
			//queryStr.append("<fields>").append(config.getColumns()).append("</fields>");
		}
		
		if(queryStr.indexOf("<limit>") == -1) {
			queryStr.append("<limit>").append(queryLimit).append("</limit>");
		}
		
		return queryStr.toString();
	}

	private String convertGroupClause(String group) {
		
		if(group.isEmpty()) return "";
		
		String[] groupCriteria = group.split(",", 2);
		StringBuffer groupSubQuery = new StringBuffer("");
		
		if(groupCriteria.length == 2) {
			groupSubQuery.append("<groupBy>").append(groupCriteria[0].trim()).append("</groupBy>");
			groupSubQuery.append("<fields>").append(groupCriteria[1].trim()).append("</fields>");
		}else if (groupCriteria.length == 1) {
			groupSubQuery.append("<groupBy>").append(groupCriteria[0].trim()).append("</groupBy>");
		}
			
		return groupSubQuery.toString();
	}
	
	private String convertTopClause(String top) {
		
		if(top.isEmpty()) return "";
		
		String[] topCriteria = top.split(" ", 2);
		StringBuffer topSubQuery = new StringBuffer("");
		
		if(!topCriteria[1].isEmpty()) {
			topSubQuery.append("<groupBy>").append(topCriteria[1].trim()).append("</groupBy>");
			topSubQuery.append("<fields>").append(LMConstants.COUNT_VAL_FIELD).append("</fields>");
			topSubQuery.append("<orderBy>").append(LMConstants.COUNT_VAL_COLUMN).append(" desc").append("</orderBy>");
			topSubQuery.append("<limit>").append(topCriteria[0].trim()).append("</limit>");
		}
			
		return topSubQuery.toString();
	}
	
	@Override
	public void cancelQuery(String queryId) throws QueryException {
		// TODO Auto-generated method stub
		try {
			RestTemplate restTemplate = new RestTemplate();			
			log.info("query id-"+queryId);
			log.info("String.valueOf(request.hashCode() id-"+queryId);
			String url = config.getUrl()+queryId;
			log.info("Cancel url-"+url);
			
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("Authorization", "Basic " + 
		    		Base64.getUrlEncoder().encodeToString((config.getUser()+":"+config.getPwd()).getBytes(Charset.forName("UTF-8"))));
			
			HttpEntity<?> requestEntity = new HttpEntity<Object>(headers);
			ResponseEntity<String> resp =restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
			log.info("resp cancel request-"+resp);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Exception during cancel query.", ex);
			throw new RuntimeException("Exception during cancel query.", ex);
		}
		
	}

	@Override
	public JsonNode getHistogramData(DruidRequest request, Date fromDateTime, Date toDateTime, List<LmDruidFieldsDTO> lmDruidColumns, boolean isTH) {
        
		try {
        	log.info("calling druid for histogram query");
    		StringBuilder query = new StringBuilder(request.getQueryString()).append(LMConstants.HISTOGRAM_SELECT_CLAUSE);
    		query.append(LMConstants.COUNT_VAL_FIELD);
    		
    		long duration = 0L;
    		if(isTH) {
    			duration = getDurationValueForTH(fromDateTime, toDateTime);
    		}else {
    			duration = getDurationValue(fromDateTime, toDateTime);
    		}
    		
    		
    		LMDeepBiQuery queryObj = formatQueryToJson(query.toString(), LMConstants.HISTOGRAM_QUERY_LIMIT, LMConstants.DEFAULT_OFFSET, lmDruidColumns, request);
    		LMDeepBiQueryContext queryContext = new LMDeepBiQueryContext();
    		queryContext.setBySegment(false);
    		queryContext.setSkipEmptyBuckets(false);
    		queryObj.setContext(queryContext);
    		request.setGranularity(Long.toString(duration));
    		request.setQueryType("Timeseries");
        	request.setQuery(queryObj);
        	
        	String jsonBody = getDruidJSONQuery(request);
        	String result = query(jsonBody);
        	
        	JsonNode values = parser.readTree(result);
        	ObjectNode object = parser.createObjectNode();
            for(JsonNode node: values) {
            	object = (ObjectNode) node;
            	object.set("count", node.get("result").get("countVal"));
            	object.set("timestamp", node.get("timestamp"));
            	object.put("duration",duration);
            	object.remove("result");
            }
            return values;
        }
        catch (Exception ex){
        	ex.printStackTrace();
        	log.error("Exception while getting histogram data.", ex);
        	throw new RuntimeException("Exception while getting histogram data.", ex);
        }
    }
	

	@Override
	    public JsonNode getColumnChart(DruidRequest request, List<LmDruidFieldsDTO> lmDruidColumns) {

	        try {
	        	log.info("calling druid for ColumnChart");
	        	request.setQuery(formatQueryToJson(request.getQueryString(), appConfig.getColumnChartLimit(), LMConstants.DEFAULT_OFFSET, lmDruidColumns, request));
	        	request.setQueryType("GroupBy");
	        	String jsonBody = getDruidJSONQuery(request);
	        	String result = query(jsonBody);
	        	
	        	Map<String, String> mapByfield = lmDruidColumns.stream().collect(Collectors.toMap(LmDruidFieldsDTO::getField, LmDruidFieldsDTO::getDisplayName));
	        	
	        	if(!StringUtils.isEmpty(request.getQuery().getGroupBy())) {
	        		String[] groupTopColumns = getGroupTopColumnName(request.getQuery().getGroupBy());
	        		List<String> groupTopAggrs = getGroupTopAggregationAlias(request.getQuery().getAggregators());
	        		Set<String> groupTopColumnList = new LinkedHashSet<>();
	        		ObjectNode outNode = parser.createObjectNode();
	        		ArrayNode eventsArray = parser.createArrayNode();
	        		JsonNode nodes = parser.readTree(result);
	                for(JsonNode node : nodes) {
	                    //eventsArray.add(node.get("event"));
	                    ObjectNode object = (ObjectNode) node.get("event");
	                    ArrayNode groupTopColumnsValues = parser.createArrayNode();
	                    for(String groupTopCol : groupTopColumns) {
							object.set(mapByfield.get(groupTopCol), node.get("event").get(groupTopCol));
							groupTopColumnList.add(mapByfield.get(groupTopCol));
							groupTopColumnsValues.add(object.get(groupTopCol));
						}
	                    object.put("grouptopcolumns", StringUtils.join(groupTopColumnList,","));
	                    object.set("grouptopcolumnsvalues", groupTopColumnsValues);
	                    object.put("aggrcolumns", StringUtils.join(groupTopAggrs,","));
						eventsArray.add(object);
					}
	                outNode.set("events", eventsArray);
	                return outNode;
	        	}
	            log.info("Got result from druid for ColumnChart");
	            return parser.readTree(result);
	        }
	        catch (Exception ex){
	        	ex.printStackTrace();
	        	log.error("Exception while getting Column Chart data.", ex);
	            throw new RuntimeException("Exception while getting Column Chart data.", ex);
	        }
	    }
    
	private String[] getGroupTopColumnName(String query) {
		// TODO Auto-generated method stub
		String[] groupTopColumn ;
		groupTopColumn =  query.split(",");
		log.info("groupTopColumn====="+groupTopColumn);
		return groupTopColumn;
	}
	private List<String> getGroupTopAggregationAlias(String query) {
		List<String> groupTopAggr = new ArrayList<String>() ;
		if(StringUtils.containsIgnoreCase(query, " AS ")){
			String[] groupTopAggrArr = query.split(",");
			for(String groupTopAggrVal : groupTopAggrArr) {
				if(StringUtils.containsIgnoreCase(groupTopAggrVal, " AS ")) {
					groupTopAggr.add(groupTopAggrVal.split(" AS ")[groupTopAggrVal.split(" AS ").length-1]);
				}			
			}
			
		}
		return groupTopAggr;
	}

	@Override
	public ResponseEntity<String> validateQuery(DruidRequest request, List<String> fields, List<LmDruidFieldsDTO> lmDruidColumns) throws JsonProcessingException, JSONException, ValidationException {
		//try {
		if(!(StringUtils.isEmpty(request.getQueryString()) || StringUtils.contains(request.getQueryString(),LMConstants.FILTERS_SPACE) || StringUtils.contains(request.getQueryString(), LMConstants.GROUP_SPACE)
				|| StringUtils.contains(request.getQueryString(), LMConstants.SORT_SPACE) || StringUtils.contains(request.getQueryString(), LMConstants.TOP_SPACE)
				|| StringUtils.contains(request.getQueryString(), LMConstants.FIELDS_SPACE))) {
			request.setQuery(null);
		}else {
			request.setQuery(formatQueryToJson(request.getQueryString(), appConfig.getPageSize(), LMConstants.DEFAULT_OFFSET, lmDruidColumns, request));
		}
		
		request.setQueryType("Scan");
    	if(!StringUtils.isEmpty(request.getQuery().getGroupBy())) {
    		request.setQueryType("GroupBy");
    	}
    	
		ObjectMapper mapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		String body = "";
		body = mapper.writeValueAsString(request);
		log.info("Request for validation to Query Builder - {}", body);
		
		HttpHeaders requestHeader = new HttpHeaders();
		requestHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> httpEntity = new HttpEntity<>(body, requestHeader);
		log.info("header---"+requestHeader);
		ResponseEntity<String> response = restTemplate.postForEntity(config.getQueryApiURL(), httpEntity, String.class);
		
		//It is easier to get the dimensions from queryBuilder response JSON than to split sql query to validate column names from json string. 
		JSONObject responseBody = new JSONObject(response.getBody());
		fields = addAggregateColumnNames(responseBody, fields);
		validateJsonObject(responseBody, fields);
		
		if(!StringUtils.isEmpty(request.getQuery().getGroupBy()) && !StringUtils.isEmpty(request.getQuery().getHaving())) {
			validateHavingClause(responseBody);
		}
		
		log.info("Response for validation from Query Builder - {}", response);
		return response;
	}

	private void validateHavingClause(JSONObject responseBody) throws JSONException, ValidationException {
		
		try {
			JSONObject havingObj = responseBody.getJSONObject(LMConstants.HAVING_JSON_KEY);
			if(havingObj != null) {
				
				JSONObject havingFilter = havingObj.getJSONObject(LMConstants.FILTER_JSON_KEY);
				List<String> havingFields = new ArrayList<>();
				List<String> groupByFields = new ArrayList<>();
				
				if(havingFilter != null) {
					Iterator itr = havingFilter.keys();
					String key = null;
					while(itr.hasNext()) {
						key = (String) itr.next();
						if(!key.equals("dimension")) {
							continue;
						}
						havingFields.add((String) havingFilter.get(key));
					}
				}
				
				JSONArray dimensionsArray = responseBody.getJSONArray(LMConstants.DIMENSIONS_JSON_KEY);
				if(dimensionsArray != null) {
			    	for (int i = 0; i < dimensionsArray.length(); i++) {
			    		groupByFields.add(dimensionsArray.getString(i));
			    	}
				}
				
				groupByFields = addAggregateColumnNames(responseBody, groupByFields);
				
				for(String str : havingFields) {
					if(!groupByFields.contains(str)) {
						throw new ValidationException(str +" is not applicable in HAVING because it is not used in GROUP or AGGR clause.");
					}
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("Exception while performing HAVING validation for JSON - "+responseBody);
		}
		
	}

	private List<String> addAggregateColumnNames(JSONObject jsonObj, List<String> fields) throws JSONException {
		try {
			
			if(!jsonObj.has(LMConstants.AGGREGATIONS_JSON_KEY)) {
				return fields;
			}
			
			JSONArray aggrArray = (JSONArray) jsonObj.get(LMConstants.AGGREGATIONS_JSON_KEY);
			JSONObject obj = null;
			Iterator itr = null;
			String key = null;
			if(aggrArray != null) {
				for (int i = 0; i < aggrArray.length(); i++) {
					obj = (JSONObject) aggrArray.get(i);
					itr = obj.keys();
					while(itr.hasNext()) {
						key = (String) itr.next();
						if(!key.equals("name")) {
							continue;
						}
						fields.add((String) obj.get(key));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("Exception while trying to add aggregation columns into fields from JSON - "+jsonObj);
		}
		return fields;
	}
	
	public void validateJsonObject(JSONObject jsonObj, List<String> fields) throws JSONException, ValidationException {
		Iterator itr = jsonObj.keys();
		Integer length = null;
		String arrayElement = null;
		
        //for (Object key : jsonObj.keys()) {
		while(itr.hasNext()) {
            //String keyStr = (String) key;
			String keyStr = (String) itr.next();
            Object keyvalue = jsonObj.get(keyStr);
            
            if(Arrays.stream(LMConstants.VALIDATION_IGNORE_KEYS).anyMatch(keyStr::equals)) {
            	continue;
            }
            
            if (keyvalue instanceof JSONObject) {
            	validateJsonObject((JSONObject) keyvalue, fields);
            } else if (keyvalue instanceof JSONArray) {
            	JSONArray array = (JSONArray) keyvalue;
            	if(keyStr.equals("dimensions")) {
            		length = array.length();
                	for (int i = 0; i < length; i++) {
                		arrayElement = array.getString(i);
                		if(!StringUtils.isEmpty(arrayElement) && !fields.contains(arrayElement)) {
            				throw new ValidationException("Please enter valid field name for "+arrayElement); 
            			}
                	}
                }
            	else if(keyStr.equals("values")) {
                	continue;
                }
            	else {
	                for (int i = 0; i < array.length(); i++) {
	                	validateJsonObject((JSONObject) array.get(i), fields);
	                }
                }
            } else {
                //System.out.println(keyStr + ", " + keyvalue);
            	if(!"dimension".equals(keyStr) && !"fieldName".equals(keyStr)) {
                	continue;
                }
            	
            	if(keyvalue != null && !fields.contains(keyvalue.toString())) {
    				throw new ValidationException("Please enter valid field name for "+keyvalue); 
    			}
            }
        }
    }


}
