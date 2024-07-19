package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CountAndNameResponse {

	private Long count;

	private String name;
	
	private Long minClosedDate;
	
	private Long maxClosedDate;
	
}
