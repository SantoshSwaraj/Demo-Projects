package aisaac.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DruidRequestDTO {
	
	private String queryString;
	private LMDeepBiQuery query;
	private String queryType;
	private List<String> datasource;
	private String intervalStart;
	private String intervalEnd;
	private String relativeStart;
	private String granularity;
	private Boolean isLargeFileDownload;
	private Boolean orderByEventTimeDesc;
	private Boolean isUtcTime;
	private Integer timezoneOffset;
	private Boolean isTH;
	private String models;
	private Integer tenantId;
	private Boolean isFreeSearch;
	private Boolean isDeepSearch;
	private List<String> deepSearchFields;
	private String queryId;
	
	public DruidRequestDTO(DruidRequest druidRequest) {
		this.queryString = druidRequest.getQueryString();
		this.query = druidRequest.getQuery();
		this.queryType = druidRequest.getQueryType();
		this.datasource = druidRequest.getDatasource();
		this.intervalStart = druidRequest.getIntervalStart();
		this.intervalEnd = druidRequest.getIntervalEnd();
		this.relativeStart = druidRequest.getRelativeStart();
		this.granularity = druidRequest.getGranularity();
		this.isLargeFileDownload = druidRequest.getIsLargeFileDownload();
		this.orderByEventTimeDesc = druidRequest.getOrderByEventTimeDesc();
		this.isUtcTime = druidRequest.getIsUtcTime();
		this.timezoneOffset = druidRequest.getTimezoneOffset();
		this.isTH = druidRequest.getIsTH();
		this.models = druidRequest.getModels();
		this.tenantId = druidRequest.getTenantId();
		this.isFreeSearch = druidRequest.getIsFreeSearch();
		this.isDeepSearch = druidRequest.getIsDeepSearch();
		this.deepSearchFields = druidRequest.getDeepSearchFields();
		this.queryId = druidRequest.getQueryId();
	}

}