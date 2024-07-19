package aisaac.payload.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.util.LogFlowDashboardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuditTrailListRequest extends DataTableRequest {

	private Integer searchType;

	private List<Long> searchOrgIds;
	
	private LocalDateTime searchFromDate;
	
	private LocalDateTime searchToDate;
	
	public void setSearchFromDate(@JsonProperty("searchFromDate") Long searchFromDateEpochMilli) {
		this.searchFromDate= LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchFromDateEpochMilli);
	}
	public void setSearchToDate(@JsonProperty("searchFromDate") Long searchToDateEpochMilli) {
		this.searchToDate= LogFlowDashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
	}
}
