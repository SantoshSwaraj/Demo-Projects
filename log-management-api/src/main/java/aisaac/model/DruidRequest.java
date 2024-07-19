package aisaac.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

import aisaac.utils.LMConstants;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@JsonIgnoreProperties(value = { "queryString", "isLargeFileDownload", "orderByEventTimeDesc", "isUtcTime", "timezoneOffset", 
		"isTH", "models", "tenantId", "isFreeSearch", "isDeepSearch", "deepSearchFields" })
public class DruidRequest {

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
	
	public DruidRequest() {
	}
	

	public DruidRequest(DruidRequestDTO dto) {
		this.queryString = dto.getQueryString();
		this.query = dto.getQuery();
		this.queryType = dto.getQueryType();
		this.datasource = dto.getDatasource();
		this.intervalStart = dto.getIntervalStart();
		this.intervalEnd = dto.getIntervalEnd();
		this.relativeStart = dto.getRelativeStart();
		this.granularity = dto.getGranularity();
		this.isLargeFileDownload = dto.getIsLargeFileDownload();
		this.orderByEventTimeDesc = dto.getOrderByEventTimeDesc();
		this.isUtcTime = dto.getIsUtcTime();
		this.timezoneOffset = dto.getTimezoneOffset();
		this.isTH = dto.getIsTH();
		this.models = dto.getModels();
		this.tenantId = dto.getTenantId();
		this.isFreeSearch = dto.getIsFreeSearch();
		this.isDeepSearch = dto.getIsDeepSearch();
		this.deepSearchFields = dto.getDeepSearchFields();
		this.queryId = dto.getQueryId();
	}

	@Builder
	public DruidRequest(String queryString, @NonNull List<String> datasource, @NonNull String timeRangeBegin, @NonNull String timeRangeEnd,
			Boolean isLargeFileDownload, Boolean orderByEventTimeDesc, Boolean isUtcTime, Integer timezoneOffset,
			Boolean isTH, String models, Integer tenantId, String isFreeSearch, String isDeepSearch, List<String> deepSearchFields) {
		this.queryString = queryString;
		this.datasource = datasource;
		this.intervalStart = timeRangeBegin;
		this.intervalEnd = timeRangeEnd;
		this.isLargeFileDownload = isLargeFileDownload;
		this.orderByEventTimeDesc = orderByEventTimeDesc;
		this.isUtcTime = isUtcTime;
		this.timezoneOffset = timezoneOffset;
		this.isTH = isTH;
		this.models = models;
		this.tenantId = tenantId;
		this.isFreeSearch = !StringUtils.isEmpty(isFreeSearch) ? (LMConstants.TRUE.equalsIgnoreCase(isFreeSearch) ? true : false) : false;
		this.isDeepSearch = !StringUtils.isEmpty(isDeepSearch) ? (LMConstants.TRUE.equalsIgnoreCase(isDeepSearch) ? true : false) : false;
		this.deepSearchFields = deepSearchFields;
	}

	 @Override
    public int hashCode() {
        return Objects.hashCode(query, intervalStart, intervalEnd);
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DruidRequest other = (DruidRequest) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (intervalStart == null) {
			if (other.intervalStart != null)
				return false;
		} else if (!intervalStart.equals(other.intervalStart))
			return false;
		if (intervalEnd == null) {
			if (other.intervalEnd != null)
				return false;
		} else if (!intervalEnd.equals(other.intervalEnd))
			return false;
		return true;
	}
	
}
