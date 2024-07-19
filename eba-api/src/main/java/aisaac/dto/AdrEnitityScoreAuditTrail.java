package aisaac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdrEnitityScoreAuditTrail {

	private AuditTrailNewOldValueDto performedBy;
	private AuditTrailNewOldValueDto performedOn;
}
