package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import aisaac.entities.LogStopageMaster;
import aisaac.payload.request.LogFlowMonitorRequestEditProduct;
import aisaac.util.AppConstants;
import aisaac.util.LogFlowDashboardUtils;

public class LogStopageMasterEditProductMapper {

	private List<LogFlowMonitorRequestEditProduct> logflow = new ArrayList<>();
	private List<LogStopageMaster> logStopage = new ArrayList<>();
	private Map<Long, LogFlowMonitorRequestEditProduct> mapEntityToRecID = new HashMap<>();
	private Map<Long, String> tenantNameAndId = new HashMap();
	private Map<String, Long> productTypeMap = new HashMap<>();

	public LogStopageMasterEditProductMapper(List<LogFlowMonitorRequestEditProduct> request,
			List<LogStopageMaster> dbData, Map<Long, String> tenantNameAndId, Map<String, Long> productTypeMap) {
		this.logflow = request;
		this.logStopage = dbData;
		this.tenantNameAndId = tenantNameAndId;
		this.productTypeMap = productTypeMap;
		mapEntityToRecID = logflow.stream()
				.collect(Collectors.toMap(LogFlowMonitorRequestEditProduct::getRecId, obj -> obj));
	}

	public List<LogStopageMaster> map() {

		return logStopage.stream().map(o -> mapUnmatchedFieldsFromTwoEntities(o)).collect(Collectors.toList());
	}

	private LogStopageMaster mapUnmatchedFieldsFromTwoEntities(LogStopageMaster log) {
		LogFlowMonitorRequestEditProduct edit = mapEntityToRecID.get(log.getRecId());

		if (edit != null) {

			log.setTenantId(ObjectUtils.isNotEmpty(edit.getTenantId()) ? edit.getTenantId() : log.getTenantId());
			log.setTenantName(StringUtils.isNotBlank(tenantNameAndId.get(edit.getTenantId()))
					? tenantNameAndId.get(edit.getTenantId())
					: log.getTenantName());

			log.setAssetType(ObjectUtils.isNotEmpty(edit.getAssetType()) ? edit.getAssetType() : log.getAssetType());

			if (StringUtil.isNotBlank(edit.getMonitorStatus())) {
				log.setSuppressed(
						AppConstants.MONITOR_STATUS_SUPPRESSED.equals(edit.getMonitorStatus()));
				log.setDisabled(
						AppConstants.MONITOR_STATUS_DISABLED.equals(edit.getMonitorStatus()));
			}

			log.setProductType(
					StringUtil.isNotBlank(edit.getProductType()) ? edit.getProductType() : log.getProductType());
			
			log.setProductTypeId(productTypeMap.get(log.getProductType()));
			
			log.setProductHostName(StringUtil.isNotBlank(edit.getProductHostName()) ? edit.getProductHostName()
					: log.getProductHostName());

			log.setProductIP(StringUtil.isNotBlank(edit.getProductIP()) ? edit.getProductIP() : log.getProductIP());

			log.setCloudResourceID(StringUtil.isNotBlank(edit.getCloudResourceID()) ? edit.getCloudResourceID()
					: log.getCloudResourceID());

			log.setSeverity(StringUtil.isNotBlank(edit.getSeverity()) ? edit.getSeverity() : log.getSeverity());

			log.setCollectorAddress(
					StringUtil.isNotBlank(edit.getCollectorIP()) ? edit.getCollectorIP() : log.getCollectorAddress());

			log.setCollectorHostName(StringUtil.isNotBlank(edit.getCollectorHostName()) ? edit.getCollectorHostName()
					: log.getCollectorHostName());

			log.setLogStopageThresholdTime(
					ObjectUtils.isNotEmpty(edit.getLogStopageThresholdTime()) ? edit.getLogStopageThresholdTime()
							: log.getLogStopageThresholdTime());

			log.setEmailAlertFrequency(
					ObjectUtils.isNotEmpty(edit.getEmailAlertFrequency()) ? edit.getEmailAlertFrequency()
							: log.getEmailAlertFrequency());

			log.setSendMail(ObjectUtils.isNotEmpty(edit.isMailSend()) ? edit.isMailSend() : log.isSendMail());

			log.setToEmailAddress(StringUtil.isNotBlank(edit.getToEmailAddress()) ? edit.getToEmailAddress()
					: log.getToEmailAddress());

			log.setCcEmailAddress(StringUtil.isNotBlank(edit.getCcEmailAddress()) ? edit.getCcEmailAddress()
					: log.getCcEmailAddress());

			log.setProductId(ObjectUtils.isNotEmpty(edit.getProductID()) ? edit.getProductID() : log.getProductId());

			log.setUpdatedBy(edit.getUserId());
			log.setUpdatedDate(LocalDateTime.now());

			log.setMdrScannerCode(StringUtil.isNotBlank(edit.getMdrScannerCode()) ? (edit.getMdrScannerCode())
					: log.getMdrScannerCode());
			log.setLogStoppageThresholdJson(StringUtil
					.isNotBlank(LogFlowDashboardUtils.CustomTimeFieldsEntityToString(edit.getLogStoppageThreshold()))
							? (LogFlowDashboardUtils.CustomTimeFieldsEntityToString(edit.getLogStoppageThreshold()))
							: log.getLogStoppageThresholdJson());
			log.setEmailNotificationFrequencyJson(StringUtil.isNotBlank(
					LogFlowDashboardUtils.CustomTimeFieldsEntityToString(edit.getEmailNotificationFrequency()))
							? (LogFlowDashboardUtils
									.CustomTimeFieldsEntityToString(edit.getEmailNotificationFrequency()))
							: log.getEmailNotificationFrequencyJson());
		}
		return log;
	}
}
