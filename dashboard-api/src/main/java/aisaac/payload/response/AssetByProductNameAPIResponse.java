package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AssetByProductNameAPIResponse {

	private Long productCount;
	private String productPercentage;
	private String productName;
	private String productVendor;
}
