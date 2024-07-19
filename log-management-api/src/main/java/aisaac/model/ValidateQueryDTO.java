package aisaac.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ValidateQueryDTO {
	private DruidRequestDTO druidRequestDTO;
	private List<String> fields;
	private List<LmDruidFieldsDTO> lmDruidColumns;
	
	public ValidateQueryDTO() {
		
	}
}
