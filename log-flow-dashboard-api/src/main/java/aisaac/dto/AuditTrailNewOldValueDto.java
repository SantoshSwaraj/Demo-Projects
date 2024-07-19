package aisaac.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditTrailNewOldValueDto {
	
	private String label;
	
	private String newValue;
	
	private String oldValue;
	
	private boolean edited;

}
