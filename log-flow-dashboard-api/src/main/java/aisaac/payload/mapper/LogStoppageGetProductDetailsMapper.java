package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;

import aisaac.dto.NotesFormatDto;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.payload.response.LogStopageDetectMasterRequiredAddFields;
import aisaac.payload.response.LogStopageMasterGetEditableFieldsResponse;
import aisaac.util.LogFlowDashboardUtils;

public class LogStoppageGetProductDetailsMapper {

	
	public static LogStopageMasterGetEditableFieldsResponse map(LogStopageMaster data,Map<Long, String> tenantMap,Map<Long, List<NotesFormatDto>> noteMap) {
		return new LogStopageMasterGetEditableFieldsResponse(
				data.getRecId(),
				data.getTenantId(),
				tenantMap.get(data.getTenantId()),
				data.getAssetType(),
				LogFlowDashboardUtils.decideMonitorStatus(data.isSuppressed(), data.isDisabled()),
				data.getProductType(),
				data.getProductHostName(),
				data.getProductIP(),
				data.getCloudResourceID(),
				data.getSeverity(),
				data.getCollectorAddress(),
				data.getCollectorHostName(),
				data.getLogStopageThresholdTime(),
				data.getEmailAlertFrequency(),
				data.isSendMail(),
				data.getToEmailAddress(),
				data.getCcEmailAddress(),
				data.getProductId(),
				noteMap.get(data.getRecId()),
				data.getMdrScannerCode(),
				LogFlowDashboardUtils.StringToCustomTimeFieldsEntity(data.getLogStoppageThresholdJson()),
				LogFlowDashboardUtils.StringToCustomTimeFieldsEntity(data.getEmailNotificationFrequencyJson()));
	}
	
	public static Object mapLogDetected(List<LogStoppageDetectedDevices> data,Map<Long, String> tenantMap,Map<Long, List<NotesFormatDto>> noteMap) {
		return data.stream().map(o->new LogStopageDetectMasterRequiredAddFields(
				o.getRecId(),
				o.getTenantId(),
				tenantMap.get(o.getTenantId()),
				o.getProductHostName(),
				o.getProductIP(),
				o.getCloudResourceID(),
				o.getCollectorAddress(),
				o.getCollectorHostName(),
				o.getProductId(),
				o.getMdrScannerCode(),
				noteMap.get(o.getRecId()),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getDetectedDate()),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getLastEventReceived())
				)).toList();
	}
}
