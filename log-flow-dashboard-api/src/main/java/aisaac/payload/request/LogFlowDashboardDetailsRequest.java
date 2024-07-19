package aisaac.payload.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.util.LogFlowDashboardUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class LogFlowDashboardDetailsRequest extends DataTableRequest {

	private String token;

	private Integer searchType = 0;

	private List<Long> searchOrgIds;
	
	private List<Long> productId;

	private String searchProductVendor;

	private String searchProductName;

	private String searchProductType;

	private String searchProductIP;

	private String searchProductHostName;

	private String searchCloudResourcID;

	private List<Integer> searchAssetTypeList;

	private List<String> searchMonitoringStatus;

	private Integer searchLogStoppageThresholdFrom;

	private Integer searchLogStoppageThresholdTo;

	private List<Integer> searchAisaacCurrentFlowStatusList;

	private List<String> searchSeverityList;

	private Integer searchEmailNotificationFrequencyFrom;

	private Integer searchEmailNotificationFrequencyTo;

	private String searchColletorIP;

	private String searchCollectorHostName;

	private String searchDescription;

	private String searchToEmail;

	private String searchCcEmail;
	
	private String searchMdrScannerCode;
	
	private List<Long> productTypeId;
	
	private boolean buRequired;
	
	private String dateString;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchCurrentFlowStateFrom;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchCurrentFlowStateUpdateTo;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchLastEventReceivedFrom;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchLastEventReceivedTo;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchCreatedBetweenFrom;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchCreatedBetweenTo;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchUpdatedBetweenFrom;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchUpdatedBetweenTo;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchDetectedBetweenFrom;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchDetectedBetweenTo;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchAllowlistedBetweenFrom;

//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private LocalDateTime searchAllowlistedBetweenTo;


	public void setSearchCurrentFlowStateFrom(@JsonProperty("searchCurrentFlowStateFrom") Long searchFromDateEpochMilli) {
		this.searchCurrentFlowStateFrom = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchFromDateEpochMilli);
	}

	public void setSearchCurrentFlowStateUpdateTo(@JsonProperty("searchCurrentFlowStateUpdateTo") Long searchToDateEpochMilli) {
		this.searchCurrentFlowStateUpdateTo = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchLastEventReceivedFrom(@JsonProperty("searchLastEventReceivedFrom") Long searchToDateEpochMilli) {
		this.searchLastEventReceivedFrom = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchLastEventReceivedTo(@JsonProperty("searchLastEventReceivedTo") Long searchToDateEpochMilli) {
		this.searchLastEventReceivedTo = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchCreatedBetweenFrom(@JsonProperty("searchCreatedBetweenFrom") Long searchToDateEpochMilli) {
		this.searchCreatedBetweenFrom = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchCreatedBetweenTo(@JsonProperty("searchCreatedBetweenTo") Long searchToDateEpochMilli) {
		this.searchCreatedBetweenTo = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchUpdatedBetweenFrom(@JsonProperty("searchUpdatedBetweenFrom") Long searchToDateEpochMilli) {
		this.searchUpdatedBetweenFrom = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchUpdatedBetweenTo(@JsonProperty("searchUpdatedBetweenTo") Long searchToDateEpochMilli) {
		this.searchUpdatedBetweenTo = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchDetectedBetweenFrom(@JsonProperty("searchDetectedBetweenFrom") Long searchToDateEpochMilli) {
		this.searchDetectedBetweenFrom = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchDetectedBetweenTo(@JsonProperty("searchDetectedBetweenTo") Long searchToDateEpochMilli) {
		this.searchDetectedBetweenTo = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchAllowlistedBetweenFrom(@JsonProperty("searchAllowlistedBetweenFrom") Long searchToDateEpochMilli) {
		this.searchAllowlistedBetweenFrom = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
	
	public void setSearchAllowlistedBetweenTo(@JsonProperty("searchAllowlistedBetweenTo") Long searchToDateEpochMilli) {
		this.searchAllowlistedBetweenTo = LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}

	public boolean isDefaultSearch() {
		return this.searchType.equals(0);
	}

	public boolean isFullSearch() {
		return this.searchType.equals(1);
	}

	public boolean isSmartSearch() {
		return this.searchType.equals(2);
	}

	public boolean isAdvancedSearch() {
		return this.searchType.equals(3);
	}
}
