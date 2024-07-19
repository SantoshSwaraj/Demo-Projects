package aisaac.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllowListRequestEditProduct {
	
	@NotNull(message = "recId cannot be blank")
	private Long recId;

	@NotNull(message = "tenantId cannot be null")
	private Long tenantId;

	@NotNull(message = "productId cannot be null")
	private Long productId;

	private String note;
	
	@NotNull(message = "userId cannot be blank")
	private Long userId;

}
