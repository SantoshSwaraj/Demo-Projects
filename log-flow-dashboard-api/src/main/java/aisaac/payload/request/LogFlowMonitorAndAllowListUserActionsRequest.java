package aisaac.payload.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class LogFlowMonitorAndAllowListUserActionsRequest {

	@NotEmpty(message = "RecIds Cannot be Blank")
	private List<Long> recIds;
	
	@NotNull(message = "UserId Cannot be Blank")
	private Long userId;
	
	private String addNote;
}
