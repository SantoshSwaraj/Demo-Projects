package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssetCriticalityApiResponse {

	private Long count;
	private String status;
}
