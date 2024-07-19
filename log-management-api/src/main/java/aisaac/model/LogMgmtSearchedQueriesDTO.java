package aisaac.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogMgmtSearchedQueriesDTO {
	private Integer userId;
	
	public LogMgmtSearchedQueriesDTO(Integer userId) {
		this.userId = userId;
	}
	
	public LogMgmtSearchedQueriesDTO() {
		
	}
}
