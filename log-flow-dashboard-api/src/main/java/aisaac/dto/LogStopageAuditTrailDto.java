package aisaac.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogStopageAuditTrailDto {

	private AuditTrailNewOldValueDto customerName;
	private AuditTrailNewOldValueDto assetType;
	private AuditTrailNewOldValueDto MonitorStatus;
	private AuditTrailNewOldValueDto deviceVendor;
	private AuditTrailNewOldValueDto deviceProduct;
	private AuditTrailNewOldValueDto deviceGroup;
	private AuditTrailNewOldValueDto deviceAddress;
	private AuditTrailNewOldValueDto deviceHostName;
	private AuditTrailNewOldValueDto cloudResourceID;
	private AuditTrailNewOldValueDto severity;
	private AuditTrailNewOldValueDto collectorIp;
	private AuditTrailNewOldValueDto collectorHostName;
	private AuditTrailNewOldValueDto stoppageThreshold;
	private AuditTrailNewOldValueDto emailNotificationFrequency;
	private AuditTrailNewOldValueDto toEmailAddress;
	private AuditTrailNewOldValueDto ccEmailAddress;
	private AuditTrailNewOldValueDto isMailSend;
	private AuditTrailNewOldValueDto note;
	
	private AuditTrailNewOldValueDto productId;
	private AuditTrailNewOldValueDto mdrScannerCode;
	private AuditTrailNewOldValueDto assetEntryMethodId;
	private AuditTrailNewOldValueDto assetId;
	private AuditTrailNewOldValueDto lastEventReceived;
	private AuditTrailNewOldValueDto customerId;
	private AuditTrailNewOldValueDto deviceStatus;
	private AuditTrailNewOldValueDto logStopageTime;
	private AuditTrailNewOldValueDto logRecieveTime;
	private AuditTrailNewOldValueDto disabled;
	private AuditTrailNewOldValueDto suppressed;
	private AuditTrailNewOldValueDto loggerStatus;
	private AuditTrailNewOldValueDto loggerDate;
	private AuditTrailNewOldValueDto deleted;
	private AuditTrailNewOldValueDto createdDate;
	private AuditTrailNewOldValueDto updatedDate;
	private AuditTrailNewOldValueDto createdId;
	private AuditTrailNewOldValueDto updatedId;
	private AuditTrailNewOldValueDto emailStatus;
	private AuditTrailNewOldValueDto emailThreasholdReached;
	private AuditTrailNewOldValueDto emailTime;
	private AuditTrailNewOldValueDto deviceReceiptTime;
	private AuditTrailNewOldValueDto sendEmailAlertToAudit;
	private AuditTrailNewOldValueDto logStoppageThresholdJson;
	private AuditTrailNewOldValueDto emailNotificationFrequencyJson;
	private AuditTrailNewOldValueDto productTypeId;
}
