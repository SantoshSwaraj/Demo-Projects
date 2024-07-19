package aisaac.model;

import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class LmQueryHistoryDTO {
	private BigInteger recId;
	private Integer executedBy;
	private String tenantIds;
	private String queryText;
//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;
	private String tenantNames;
	
	public LmQueryHistoryDTO(){}
	
	public LmQueryHistoryDTO(BigInteger recId,
			Integer executedBy,
			String tenantIds,
			String queryText,
			Date createdDate,
			String tenantNames) {
		this.recId = recId;
		this.executedBy = executedBy;
		this.tenantIds = tenantIds;
		this.queryText = queryText;
		this.createdDate = createdDate;
		this.tenantNames = tenantNames;
	}
}