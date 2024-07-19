package aisaac.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllowListRequestAddProduct {

	@NotNull(message = "tenantId cannot be blank")
	private Long tenantId;
	
	@NotNull(message = "ProductId cannot be blank")
	private Long productId;
	
	private String note;
	
	@NotNull(message = "userId cannot be blank")
	private Long userId;
	
	private Long recId;
}
