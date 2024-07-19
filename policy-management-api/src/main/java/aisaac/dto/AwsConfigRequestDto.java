package aisaac.dto;

import aisaac.domain.datatable.DataTableRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsConfigRequestDto {
	
	private String cloudResourceId;
	
	private Integer tenantId;
	
	private DataTableRequest dataTableRequest;

}
