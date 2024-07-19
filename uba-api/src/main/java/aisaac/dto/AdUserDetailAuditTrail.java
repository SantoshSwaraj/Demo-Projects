package aisaac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdUserDetailAuditTrail {

	private AuditTrailNewOldValueDto tenantId;
	private AuditTrailNewOldValueDto performedBy;
	private AuditTrailNewOldValueDto performedOn;
}
