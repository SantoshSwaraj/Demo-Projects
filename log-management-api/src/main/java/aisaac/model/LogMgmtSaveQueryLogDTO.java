package aisaac.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogMgmtSaveQueryLogDTO {
	private Integer userId;
	private LogMgmtSaveQueryDTO logMgmtSaveQueryDTO;
	private String queryName;
	
	public LogMgmtSaveQueryLogDTO(Integer userId, LogMgmtSaveQueryDTO logMgmtSaveQueryDTO, String queryName) {
		this.userId = userId;
		this.logMgmtSaveQueryDTO = logMgmtSaveQueryDTO;
		this.queryName = queryName;
	}	
}
