package aisaac.payload.response;

import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssetByTicketPriorityLevelResponse {

	private String productName;
	private Map<String, Long> assetCount;
}
