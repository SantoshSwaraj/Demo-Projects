package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WatchListAuditTrailResponse {

	private String Action;
	private String performedBy;
	private Long performedOn;
}
