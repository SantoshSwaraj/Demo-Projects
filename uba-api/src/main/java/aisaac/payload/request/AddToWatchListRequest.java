package aisaac.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToWatchListRequest {

	@NotNull(message = "TenantId can't be null")
	private Long tenantId;
	
	@NotNull(message = "AduserId can't be null")
	private Integer adUserId;
	
	@NotBlank(message = "Action can't be Blank")
	private String action;
	
	@NotNull(message = "userId can't be null")
	private Long userId;
}
