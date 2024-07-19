package aisaac.dto;

import lombok.Data;

@Data
public class DetectedDevicesAuditTrailDto {

	private AuditTrailNewOldValueDto customerName;	
	private AuditTrailNewOldValueDto deviceVendor;	
	private AuditTrailNewOldValueDto deviceProduct;
	private AuditTrailNewOldValueDto deviceGroup;
	private AuditTrailNewOldValueDto deviceAddress;
	private AuditTrailNewOldValueDto deviceHostName;
	private AuditTrailNewOldValueDto collectorIp;
	private AuditTrailNewOldValueDto collectorHostName;
	private AuditTrailNewOldValueDto mdrScannerCode;
	private AuditTrailNewOldValueDto note;
	
	private AuditTrailNewOldValueDto productId;
	private AuditTrailNewOldValueDto tenantId;
	private AuditTrailNewOldValueDto deleted;
	private AuditTrailNewOldValueDto whitelist;
	private AuditTrailNewOldValueDto lastEventReceived;
	private AuditTrailNewOldValueDto createdDate;
	private AuditTrailNewOldValueDto updatedDate;
}
