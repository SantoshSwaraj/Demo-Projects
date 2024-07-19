package aisaac.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogMgmtHistogramDTO {
	private DruidRequestDTO druidRequest;
	private Date fromDateTime;
	private Date toDateTime;
	private List<LmDruidFieldsDTO> lmDruidColumns;
}
