package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AllowListResponse {
	private String tenantName;
	private String productVendor;
	private String productName;
	private String description;
	private Long allowListedDate;
	private Long recId;
	private Long productId;
}
