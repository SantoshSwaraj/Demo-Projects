package aisaac.payload.response;

import java.math.BigInteger;

import aisaac.utils.LmHistoricalStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoricalLogResponse {
	private BigInteger recId;
	private Integer tenantId;
	private String queryText;
	private Long fromDate;
	private Long toDate;
	private String executedBy;
	private Long executedOn;
	private String queryId;
	private LmHistoricalStatus status;
}
