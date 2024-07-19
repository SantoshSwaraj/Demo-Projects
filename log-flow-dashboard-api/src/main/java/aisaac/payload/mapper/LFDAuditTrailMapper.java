package aisaac.payload.mapper;

import java.util.Objects;

import aisaac.entities.LogStopageDetectedDevicesAuditTrail;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.LogStopageMasterAuditTrail;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.entities.WhitelistAuditTrail;

public class LFDAuditTrailMapper {

	public static LogStopageMasterAuditTrail map(LogStopageMaster logStopageMaster) {
		LogStopageMasterAuditTrail audit = new LogStopageMasterAuditTrail();
		audit.setProductId(logStopageMaster.getProductId());
		audit.setProductType(logStopageMaster.getProductType());
		audit.setTenantId(logStopageMaster.getTenantId());
		audit.setTenantName(logStopageMaster.getTenantName());
		audit.setProductIP(logStopageMaster.getProductIP());
		audit.setProductHostName(logStopageMaster.getProductHostName());
		audit.setProductName(logStopageMaster.getProductName());
		audit.setProductVendor(logStopageMaster.getProductVendor());
		audit.setDeviceStatus(logStopageMaster.getDeviceStatus());
		audit.setSeverity(logStopageMaster.getSeverity());
		audit.setEmailAlertFrequency(logStopageMaster.getEmailAlertFrequency());
		audit.setLogStopageThresholdTime(logStopageMaster.getLogStopageThresholdTime());
		audit.setLogStoppageTime(logStopageMaster.getLogStoppageTime());
		audit.setLatestRecivedTime(logStopageMaster.getLatestRecivedTime());
		audit.setSuppressed(logStopageMaster.isSuppressed());
		audit.setDisabled(logStopageMaster.isDisabled());
		audit.setDeleted(logStopageMaster.isDeleted());
		audit.setCreatedDate(logStopageMaster.getCreatedDate());
		audit.setUpdatedDate(logStopageMaster.getUpdatedDate());
		audit.setCreatedDate(logStopageMaster.getCreatedDate());
		audit.setCcEmailAddress(logStopageMaster.getCcEmailAddress());
		audit.setToEmailAddress(logStopageMaster.getToEmailAddress());
		audit.setLastEventReceived(logStopageMaster.getLastEventReceived());
		audit.setCreatedBy(Objects.nonNull(logStopageMaster.getCreatedBy()) ? logStopageMaster.getCreatedBy().intValue()
				: logStopageMaster.getUpdatedBy());
		audit.setUpdatedBy(logStopageMaster.getUpdatedBy());
		audit.setLogStoppageThresholdJson(logStopageMaster.getLogStoppageThresholdJson());
		audit.setEmailNotificationFrequencyJson(logStopageMaster.getEmailNotificationFrequencyJson());
		audit.setCloudResourceID(logStopageMaster.getCloudResourceID());
		return audit;
	}

	public static LogStopageDetectedDevicesAuditTrail map(LogStoppageDetectedDevices logStoppageDetectedDevices,
			Long userId) {
		LogStopageDetectedDevicesAuditTrail audit = new LogStopageDetectedDevicesAuditTrail();
		audit.setLogStopageDetectedDevicesId(logStoppageDetectedDevices.getRecId());
		audit.setTenantId(logStoppageDetectedDevices.getTenantId());
		audit.setProductHostName(logStoppageDetectedDevices.getProductHostName());
		audit.setProductIP(logStoppageDetectedDevices.getProductIP());
		audit.setProductName(logStoppageDetectedDevices.getProductName());
		audit.setProductVendor(logStoppageDetectedDevices.getProductVendor());
		audit.setProductId(logStoppageDetectedDevices.getProductId());
		audit.setDeleted(logStoppageDetectedDevices.isDeleted());
		audit.setLastEventReceived(logStoppageDetectedDevices.getLastEventReceived());
		audit.setDetectedDate(logStoppageDetectedDevices.getDetectedDate());
		audit.setUpdatedDate(logStoppageDetectedDevices.getUpdatedDate());
		audit.setCreatedBy(userId);
		if(logStoppageDetectedDevices.getUpdatedDate()!=null)
			audit.setUpdatedBy(userId);
		audit.setWhitelist(logStoppageDetectedDevices.isWhitelist());
		audit.setCloudResourceID(logStoppageDetectedDevices.getCloudResourceID());

		return audit;
	}

	public static WhitelistAuditTrail map(LogStoppageWhitelistDevices logStoppageWhitelistDevices) {
		WhitelistAuditTrail audit = new WhitelistAuditTrail();
		audit.setTenantId(logStoppageWhitelistDevices.getTenantId());
		audit.setProductVendor(logStoppageWhitelistDevices.getProductVendor());
		audit.setProductName(logStoppageWhitelistDevices.getProductName());
		audit.setAllowListedDate(logStoppageWhitelistDevices.getAllowListedDate());
		audit.setCreatedById(logStoppageWhitelistDevices.getCreatedById());
		audit.setUpdatedById(logStoppageWhitelistDevices.getUpdatedById());
		audit.setUpdatedDate(logStoppageWhitelistDevices.getUpdatedDate());
		audit.setProductId(logStoppageWhitelistDevices.getProductId());
		audit.setDeleted(logStoppageWhitelistDevices.isDeleted());
		audit.setDescription(logStoppageWhitelistDevices.getDescription());

		return audit;
	}
}
