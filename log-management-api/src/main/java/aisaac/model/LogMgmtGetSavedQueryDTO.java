package aisaac.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogMgmtGetSavedQueryDTO extends LogMgmtSearchDTO{
	private Integer recId;
	private Integer createdBy;
	private Integer tenantId;
	private String queryName;
	private String queryText;
//	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;
	private String tenantName;
	private String displayName;
	
	public LogMgmtGetSavedQueryDTO() {}
	
	public LogMgmtGetSavedQueryDTO(Integer recId, 
			Integer createdBy, 
			Integer tenantId, 
			String queryName, 
			String queryText, 
			Date createdDate,
			String tenantName,
			String displayName) {
		this.recId = recId;
		this.createdBy = createdBy;
		this.tenantId = tenantId;
		this.queryName = queryName;
		this.queryText = queryText;
		this.createdDate = createdDate;
		this.tenantName = tenantName;
		this.displayName = displayName;
	}
	
}
