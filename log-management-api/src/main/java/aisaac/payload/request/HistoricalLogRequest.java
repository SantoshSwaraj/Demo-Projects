package aisaac.payload.request;

import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import aisaac.utils.LMUtils;
import lombok.Data;


@Data
public class HistoricalLogRequest {	
	@NotNull(message = "Tenant Id can't be null")
	private Integer tenantId;
	
	@NotNull(message = "From Date can't be null")
	private Date fromDate;

	@NotNull(message = "To Date can't be null")
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
	
	@NotBlank(message = "Query text can't be null")
	private String queryText;
}
