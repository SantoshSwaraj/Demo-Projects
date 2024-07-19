package aisaac.payload.request;

import aisaac.domain.datatable.DataTableRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AssetsListRequest extends DataTableRequest{

	@NotNull(message = "TenantId can't be Null")
	private Long tenantId;
	
	@NotBlank(message = "IP Address can't be Blank")
	private String ipAddress;
	
	private String dateString;
}
