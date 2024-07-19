package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.dto.MinutesAndLastEventDto;
import aisaac.entities.LogStopageMaster;
import aisaac.payload.request.LogFlowMonitorRequestAddProduct;
import aisaac.util.AppConstants;
import aisaac.util.LogFlowDashboardUtils;

public class LogStopageMasterAddProductMapper {

	private List<LogFlowMonitorRequestAddProduct> data = new ArrayList<>();
	private Map<Long, String> tenantNameAndId = new HashMap<Long, String>();
	private Map<String, MinutesAndLastEventDto> minutesMap = new HashMap<>();
	private Map<String, Long> productTypeMap = new HashMap<>();

	public LogStopageMasterAddProductMapper(List<LogFlowMonitorRequestAddProduct> in, Map<Long, String> tenantNameAndId,
			Map<String, MinutesAndLastEventDto> minutesMap, Map<String, Long> productTypeMap) {
		this.data = in;
		this.tenantNameAndId = tenantNameAndId;
		this.minutesMap = minutesMap;
		this.productTypeMap = productTypeMap;
	}

	public List<LogStopageMaster> map(){
		return data.stream().map(o -> new LogStopageMaster().setTenantId(o.getTenantId())
				.setTenantName(tenantNameAndId.get(o.getTenantId()))
				.setAssetType(o.getAssetType())
				.setSuppressed(AppConstants.MONITOR_STATUS_SUPPRESSED.equals(o.getMonitorStatus()))
				.setDisabled(AppConstants.MONITOR_STATUS_DISABLED.equals(o.getMonitorStatus()))
				.setProductType(o.getProductType())
				.setProductHostName(o.getProductHostName())
				.setProductIP(o.getProductIP())
				.setCloudResourceID(o.getCloudResourceID())
				.setSeverity(o.getSeverity())
				.setCollectorAddress(o.getCollectorIP())
				.setCollectorHostName(o.getCollectorHostName())
				.setLogStopageThresholdTime(o.getLogStopageThresholdTime())
				.setEmailAlertFrequency(o.getEmailAlertFrequency())
				.setSendMail(o.isSendMail())
				.setToEmailAddress(o.getToEmailAddress())
				.setCcEmailAddress(o.getCcEmailAddress())
				.setProductId(o.getProductID())
				.setCreatedDate(LocalDateTime.now())
				.setCreatedBy(o.getUserId())
				.setUpdatedDate(LocalDateTime.now())
				.setUpdatedBy(Objects.nonNull(o.getUserId()) ? o.getUserId().longValue() : null)
				.setLastEventReceived(getLastEventRecieved(o))
				.setEmailTime(LocalDateTime.now())
				.setMdrScannerCode(o.getMdrScannerCode())
				.setDeviceStatus(getDeviceStatus(o))
				.setLogStoppageThresholdJson(LogFlowDashboardUtils.CustomTimeFieldsEntityToString(o.getLogStoppageThreshold()))
				.setEmailNotificationFrequencyJson(LogFlowDashboardUtils.CustomTimeFieldsEntityToString(o.getEmailNotificationFrequency()))
				.setProductTypeId(productTypeMap.get(o.getProductType()))
				).collect(Collectors.toList());
	}
	
	private Integer getDeviceStatus(LogFlowMonitorRequestAddProduct o) {
		
		MinutesAndLastEventDto dto = minutesMap
				.get(o.getProductID() + o.getTenantId() + o.getProductIP() + o.getProductHostName());

		if (dto != null && o.getLogStopageThresholdTime() != null
				&& dto.getMinutes() <= o.getLogStopageThresholdTime()) {
			return 1;
		}
		return 0;
	}
	
	private LocalDateTime getLastEventRecieved(LogFlowMonitorRequestAddProduct o) {
		MinutesAndLastEventDto dto = minutesMap
				.get(o.getProductID() + o.getTenantId() + o.getProductIP() + o.getProductHostName());
		if (dto != null) {
			return dto.getLastEventReceived();
		}
		return null;
	}
}
