package aisaac.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AssetListResponse {

	private String assetName;
	private String productVendor;
	private String productName;
	private List<String> assetTags;
	private Long assetId;
	private Long productId;
	private String hostName;
	private String ipAddress;
	private Integer productTypeId;
	private String cloudResourceId;
}
