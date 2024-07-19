package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ThreatByProductNameResponse {

	private Long count;
	private String productName;
	private String productVendor;
}
