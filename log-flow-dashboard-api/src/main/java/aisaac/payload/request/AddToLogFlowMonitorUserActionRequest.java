package aisaac.payload.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class AddToLogFlowMonitorUserActionRequest {


	@NotEmpty(message = "recId cannot be Empty")
	private List<Long> recIds;

	@NotNull(message = "UserId cannot be null")
	private Integer userId;

	private String addNote;
}
