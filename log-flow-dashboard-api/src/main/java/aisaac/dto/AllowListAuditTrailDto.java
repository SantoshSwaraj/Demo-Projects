package aisaac.dto;

import lombok.Data;

@Data
public class AllowListAuditTrailDto {

	private AuditTrailNewOldValueDto customerName;	
	private AuditTrailNewOldValueDto deviceVendor;	
	private AuditTrailNewOldValueDto deviceProduct;	
	private AuditTrailNewOldValueDto description;
	private AuditTrailNewOldValueDto note;
	
	private AuditTrailNewOldValueDto productId;
	private AuditTrailNewOldValueDto tenantId;
	private AuditTrailNewOldValueDto deleted;
	private AuditTrailNewOldValueDto createdDate;
	private AuditTrailNewOldValueDto updatedDate;
	private AuditTrailNewOldValueDto createdId;
	private AuditTrailNewOldValueDto updatedId;
}
