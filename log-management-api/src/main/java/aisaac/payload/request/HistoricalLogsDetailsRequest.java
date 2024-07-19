package aisaac.payload.request;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.utils.LMUtils;
//import aisaac.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class HistoricalLogsDetailsRequest extends DataTableRequest{
	
	@Getter
	@Setter
	private Integer searchType;
	@Getter
	@Setter
	private Integer tenantId;
	@Getter
	private Date fromDate;
	@Getter
	private Date toDate;
	
	public void setFromDate(@JsonProperty("fromDate") Long searchFromDateEpochMilli) {
		if(Objects.nonNull(searchFromDateEpochMilli)) {
			this.fromDate = LMUtils.getDateTimeByEpochMilli(searchFromDateEpochMilli);
		}
	}

	public void setToDate(@JsonProperty("toDate") Long searchToDateEpochMilli) {
		if(Objects.nonNull(searchToDateEpochMilli)) {
			this.toDate = LMUtils.getDateTimeByEpochMilli(searchToDateEpochMilli);
		}
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
