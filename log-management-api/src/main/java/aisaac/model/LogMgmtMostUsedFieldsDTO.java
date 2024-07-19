package aisaac.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogMgmtMostUsedFieldsDTO extends LogMgmtSearchDTO {
	
	private String[] fieldsMostUsed;
	private List<LmDruidFieldsDTO> lmDruidFieldMostUsed;
	private DruidRequestDTO druidRequest;
	private List<LmDruidFieldsDTO> lmDruidColumns;

	public LogMgmtMostUsedFieldsDTO() {
	}
	
}
