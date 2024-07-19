package aisaac.payload.request;

import aisaac.domain.datatable.DataTableRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CountryMapAlertRequest  extends DataTableRequest{

	private Long tenantId;
	private String countryCode;
}
