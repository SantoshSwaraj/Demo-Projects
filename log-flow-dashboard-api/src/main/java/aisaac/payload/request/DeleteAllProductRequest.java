package aisaac.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class DeleteAllProductRequest {
	@NotNull(message = "tenantId Cannot be null")
	private Long tenantId;
	
	@NotNull(message = "UserId Cannot be null")
	private Long userId;
	
	private String addNote;
}
