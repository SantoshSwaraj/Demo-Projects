package aisaac.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import aisaac.utils.LmHistoricalStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoricalLogDto {
	@JsonIgnore
	private Integer userId;
	private Integer tenantId;
	private Date fromDate;
	private Date toDate;
	private String queryText;
	private String queryId;
	private LmHistoricalStatus status;
	private String statusMessage;
}
