package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuditTrailListResponse {

	private Long recId;
	private String organization;
	private String creatorDisplayName;
	private Long createdDateTime;
	private String actionDesc;
	private String details;
	private String productName;
	private String productVendor;
	
}
