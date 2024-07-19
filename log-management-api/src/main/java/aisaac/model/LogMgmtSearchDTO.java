package aisaac.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import aisaac.utils.LMConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class LogMgmtSearchDTO {
	
	private Integer start;
	private Integer length;
	private Date fromDateTime;
	private Date toDateTime;
	private String query;
	private String tenantIds;
	private Boolean orderByEventTimeDesc;
	private Short timeSplitMinutes;
	private Boolean isLargeFileDownload;
	private String isFreeSearch;
	private String isDeepSearch;
	private List<String> deepSearchFields;
	private DruidRequestDTO druidRequest;
	
	public DruidRequest getDruidRequest(String dataSrc, boolean isUtcTime, Integer timezoneOffset) {
		
		String fromDate = convertDateToStr(fromDateTime, LMConstants.ISO_FORMAT);
		String toDate = convertDateToStr(toDateTime, LMConstants.ISO_FORMAT);
		//String dataSource = dataSrc.replace("tenantId", tenantId);
		List<String> dataSources = new ArrayList<String>();
		List<String> organisations = Arrays.asList(tenantIds.split(","));
		
		if(query.startsWith(LMConstants.STORAGE_GROUP_SPACE) && organisations.size() == 1) {
			String queryWithoutStorageGroupKeyWord = query.replace(LMConstants.STORAGE_GROUP_SPACE, "").trim();
			
			int storageGroupPipeIndex = queryWithoutStorageGroupKeyWord.indexOf("|");
			String storageGroupName = null;
			
			if(storageGroupPipeIndex >= 0) {
				storageGroupName = queryWithoutStorageGroupKeyWord.substring(0, storageGroupPipeIndex).trim();
			} else {
				storageGroupPipeIndex = queryWithoutStorageGroupKeyWord.length();
				storageGroupName = queryWithoutStorageGroupKeyWord.substring(0, storageGroupPipeIndex).trim();
			}
			
			query = queryWithoutStorageGroupKeyWord.replace(storageGroupName, "").trim();
			dataSources.add(organisations.get(0)+ "-"+ storageGroupName);
		}else {
			for(String tenantId : organisations) {
				dataSources.add(dataSrc.replace("tenantId", tenantId));
			}
		}
		
		DruidRequest.DruidRequestBuilder context = DruidRequest.builder().datasource(dataSources).timeRangeBegin(fromDate).timeRangeEnd(toDate);
		context.queryString(query);
		context.isLargeFileDownload(isLargeFileDownload);
		context.orderByEventTimeDesc(orderByEventTimeDesc);
		context.isUtcTime(isUtcTime);
		context.timezoneOffset(timezoneOffset);
		context.isTH(false);
		context.models(null);
		context.tenantId(null);
		context.isFreeSearch(isFreeSearch);
		context.isDeepSearch(isDeepSearch);
		context.deepSearchFields(deepSearchFields);
		
		return context.build();
	}

	public LogMgmtSearchDTO() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromDateTime == null) ? 0 : fromDateTime.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((tenantIds == null) ? 0 : tenantIds.hashCode());
		result = prime * result + ((toDateTime == null) ? 0 : toDateTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogMgmtSearchDTO other = (LogMgmtSearchDTO) obj;
		if (fromDateTime == null) {
			if (other.fromDateTime != null)
				return false;
		} else if (!fromDateTime.equals(other.fromDateTime))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (tenantIds == null) {
			if (other.tenantIds != null)
				return false;
		} else if (!tenantIds.equals(other.tenantIds))
			return false;
		if (toDateTime == null) {
			if (other.toDateTime != null)
				return false;
		} else if (!toDateTime.equals(other.toDateTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LogMgmtSearchDTO [start=" + start + ", length=" + length + ", fromDateTime=" + fromDateTime
				+ ", toDateTime=" + toDateTime + ", query=" + query + ", tenantIds=" + tenantIds
				+ ", orderByEventTimeDesc=" + orderByEventTimeDesc + ", timeSplitMinutes=" + timeSplitMinutes
				+ ", isLargeFileDownload=" + isLargeFileDownload + "]";
	}

	public String convertDateToStr(Date since, String dateFormat) {
		String dateStr = "";
		if (since != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			dateStr = sdf.format(since);
		}
		return dateStr;
	}

	
}
