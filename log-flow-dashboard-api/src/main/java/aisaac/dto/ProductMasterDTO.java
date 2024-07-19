package aisaac.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductMasterDTO {
	private Long productId;

	private String productVendor;

	private String productName;

	private Long productTypeId;

	private String productType;
}
