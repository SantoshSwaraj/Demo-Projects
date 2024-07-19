package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import aisaac.dto.NotesFormatDto;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.ProductMaster;
import aisaac.payload.response.LFMResponse;
import aisaac.util.AppConstants;
import aisaac.util.LogFlowDashboardUtils;

public class LogStopageMasterMapper {

	private List<LogStopageMaster> data = new ArrayList<>();
	private Map<Long, List<NotesFormatDto>> noteMap = new HashMap<>();
	private Map<Long, String> assetTypeMap=new HashMap<>();
	private Map<Long, ProductMaster> productMasterMap=new HashMap<>();

	public LogStopageMasterMapper(List<LogStopageMaster> in, Map<Long, List<NotesFormatDto>> noteMap,
			Map<Long, String> assetTypeMap, Map<Long, ProductMaster> productMasterMap) {
		this.data = in;
		this.noteMap = noteMap;
		this.assetTypeMap = assetTypeMap;
		this.productMasterMap = productMasterMap;
	}

	public List<Object> map(){
		return data.stream().map(o-> new LFMResponse(
				o.getTenantName(),
				o.getCollectorAddress(),
				o.getCollectorHostName(),
				LogFlowDashboardUtils.getProductVendorById(o.getProductId(),productMasterMap),
				LogFlowDashboardUtils.getProductNameById(o.getProductId(),productMasterMap),
				LogFlowDashboardUtils.getProductTypeById(o.getProductId(),productMasterMap),
				o.getProductIP(),
				o.getProductHostName(),
				assetTypeMap.get(o.getAssetType().longValue()),
				o.getCloudResourceID(),
				o.getSeverity(),
				o.getLogStopageThresholdTime(),
				o.getEmailAlertFrequency(),
				o.getDeviceStatus(),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(
						(Objects.nonNull(o.getDeviceStatus()) && o.getDeviceStatus() == 0) ? o.getLogStoppageTime()
								: o.getLatestRecivedTime()),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getLastEventReceived()),
				LogFlowDashboardUtils.decideMonitorStatus(o.isSuppressed(),o.isDisabled()),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getCreatedDate()),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getUpdatedDate()),
				o.getToEmailAddress(),
				o.getCcEmailAddress(),
				noteMap.get(o.getRecId()),
				o.getRecId(),
				o.getProductId(),
				o.getMdrScannerCode(),
				LogFlowDashboardUtils.StringToCustomTimeFieldsEntity(o.getLogStoppageThresholdJson()),
				LogFlowDashboardUtils.StringToCustomTimeFieldsEntity(o.getEmailNotificationFrequencyJson()),
				o.getAssetType(),
				o.isDisabled(),
				o.isSuppressed(),
				o.getAssetId()
				)).collect(Collectors.toList());
	}
	
	public static List<String[]> export(List<LogStopageMaster> logStopageMasterList, Map<Long, List<NotesFormatDto>> noteMap,
			Map<Long, String> assetTypeMap, Map<Long, ProductMaster> productMasterMap){
		List<String[]> result = new ArrayList<>();
		result.add(AppConstants.LOG_FLOW_MONITOR_EXPORT_COLUMNS
				.toArray(new String[AppConstants.LOG_FLOW_MONITOR_EXPORT_COLUMNS.size()]));
		logStopageMasterList.forEach(obj -> {
			result.add(new String[] {
					StringUtils.wrap(obj.getTenantName(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getProductType(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(LogFlowDashboardUtils.getProductVendorById(obj.getProductId(), productMasterMap),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(LogFlowDashboardUtils.getProductNameById(obj.getProductId(), productMasterMap),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getProductIP(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getProductHostName(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils
							.wrap(Objects.nonNull(obj.getAssetType()) ? assetTypeMap.get(obj.getAssetType().longValue())
									: StringUtils.EMPTY, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCloudResourceID(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getSeverity(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCollectorAddress(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCollectorHostName(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getMdrScannerCode(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(
							LogFlowDashboardUtils.getStringFromCustomTimeFields(LogFlowDashboardUtils
									.StringToCustomTimeFieldsEntity(obj.getLogStoppageThresholdJson())),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(
							LogFlowDashboardUtils.getStringFromCustomTimeFields(LogFlowDashboardUtils
									.StringToCustomTimeFieldsEntity(obj.getEmailNotificationFrequencyJson())),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					Objects.nonNull(obj.getLoggerDate())?LogFlowDashboardUtils.getLocalDateTimeInStringFormat(obj.getLoggerDate()):StringUtils.EMPTY,
					LogFlowDashboardUtils.getLocalDateTimeInStringFormat(obj.getLastEventReceived()),
					StringUtils.wrap(obj.getToEmailAddress(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCcEmailAddress(), AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					LogFlowDashboardUtils.getLocalDateTimeInStringFormat(obj.getCreatedDate()),
					LogFlowDashboardUtils.getLocalDateTimeInStringFormat(obj.getUpdatedDate()),
					StringUtils.wrap(LogFlowDashboardUtils.getDeviceStatusByNumber(obj.getDeviceStatus()),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(LogFlowDashboardUtils.decideMonitorStatus(obj.isSuppressed(), obj.isDisabled()),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX),
					StringUtils.wrap(getNoteFromNoteEntity(noteMap.get(obj.getRecId())),
							AppConstants.LOG_FLOW_DASHBOARD_FILENAME_PREFIX), });
		});
		return result;
	}
	
	private static String getNoteFromNoteEntity(List<NotesFormatDto> note) {
		if(CollectionUtils.isNotEmpty(note)) {
			return note.get(0).getNote();
		}else {
			return StringUtils.EMPTY;
		}
	}
}
