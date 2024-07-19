package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DescriptionResponse {

	private String description;
	private String createdBy;
	private Long createdOn;
	private String updatedBy;
	private Long updatedOn;
}
