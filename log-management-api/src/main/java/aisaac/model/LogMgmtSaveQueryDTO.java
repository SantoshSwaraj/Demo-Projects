package aisaac.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogMgmtSaveQueryDTO extends LogMgmtSearchDTO{
	
	private String queryName;
	private String dataSourceType;
	private Integer recId;
	
	private String isFreeSearch;
	private String searchValue;
	private String isDeepSearch;
	

	public LogMgmtSaveQueryDTO() {
	}
	
}
