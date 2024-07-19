package aisaac.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogMgmtTopTenRecordsDTO {
	private DruidRequestDTO druidRequest;
	private LogMgmtSearchByFieldsDTO logMgmtSearchByFieldsDTO;
	private List<LmDruidFieldsDTO> lmDruidColumns;;
}
