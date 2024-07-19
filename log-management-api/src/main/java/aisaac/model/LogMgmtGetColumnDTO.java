package aisaac.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogMgmtGetColumnDTO {

	private List<LmDruidFieldsDTO> lmDruidColumns;
	private DruidRequestDTO druidRequestDTO;
	
	public LogMgmtGetColumnDTO() {
		
	}
	
	public LogMgmtGetColumnDTO(DruidRequest druidRequest, List<LmDruidFieldsDTO> lmDruidColumns) {
		this.druidRequestDTO = new DruidRequestDTO(druidRequest);
		this.lmDruidColumns = lmDruidColumns;
	}
	
}
