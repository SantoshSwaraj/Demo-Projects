package aisaac.domain.datatable;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataTableRequest {
	private Integer draw;
	private Integer start;
	private Integer length;
	private Map<Search, String> search;
	private List<ColumnDTO> columns;
	private List<Map<Order, String>> order;
	private Integer part;
	private boolean lastPart;
}
