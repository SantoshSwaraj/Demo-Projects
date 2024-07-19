package aisaac.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LogManagementSaveSearchQueryDTO {
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
	private Integer userId;

	public LogManagementSaveSearchQueryDTO() {
	
	}
	
	public LogManagementSaveSearchQueryDTO(LogMgmtSearchDTO logMgmtSearchDTO, Integer userId, String dataSrc, boolean isUtcTime, Integer timezoneOffset) {
		this.start = logMgmtSearchDTO.getStart();
		this.length = logMgmtSearchDTO.getLength();
		this.fromDateTime = logMgmtSearchDTO.getFromDateTime();
		this.toDateTime = logMgmtSearchDTO.getToDateTime();
		this.query = logMgmtSearchDTO.getQuery();
		this.tenantIds = logMgmtSearchDTO.getTenantIds();
		this.orderByEventTimeDesc = logMgmtSearchDTO.getOrderByEventTimeDesc();
		this.timeSplitMinutes = logMgmtSearchDTO.getTimeSplitMinutes();
		this.isLargeFileDownload = logMgmtSearchDTO.getIsLargeFileDownload();
		this.isFreeSearch = logMgmtSearchDTO.getIsFreeSearch();
		this.isDeepSearch = logMgmtSearchDTO.getIsDeepSearch();
		this.deepSearchFields = logMgmtSearchDTO.getDeepSearchFields();
		this.druidRequest = new DruidRequestDTO(logMgmtSearchDTO.getDruidRequest(dataSrc, isUtcTime, timezoneOffset));
		this.userId = userId;
	}
}