package aisaac.payload.mapper;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.LogStopageMaster;
import aisaac.payload.response.AssetByLogFlowStatusResponse;
import aisaac.util.AppConstants;
import aisaac.util.DashboardUtils;

public class AssetByLogFlowStatusMapper {

	public static Object map(Map<String, LogStopageMaster> logStopageMasterStoppedMap, Map<String, LogStopageMaster> logStopageMasterRunnedMap) {
		return AppConstants.ASSET_BY_LOG_FLOW_STATUS_DEFAULT_RESPONSE_PARAMS.stream()
				.map(o -> {
					LogStopageMaster logStopageMasterStopped = logStopageMasterStoppedMap.getOrDefault(o,
							new LogStopageMaster());
					LogStopageMaster logStopageMasterRunned = logStopageMasterRunnedMap.getOrDefault(o,
							new LogStopageMaster());

					AssetByLogFlowStatusResponse data = new AssetByLogFlowStatusResponse();
					data.setTimeDuration(o);
					data.setStopped(
							Objects.nonNull(logStopageMasterStopped.getCount()) ? logStopageMasterStopped.getCount()
									: 0L);
					data.setRunning(
							Objects.nonNull(logStopageMasterRunned.getCount()) ? logStopageMasterRunned.getCount()
									: 0L);
					data.setTotal(data.getStopped() + data.getRunning());
					data.setMinLogStoppedTime(
							DashboardUtils.getLocalDateTimeInMilliSec(logStopageMasterStopped.getMinLogStopageTime()));
					data.setMaxLogStoppedTime(
							DashboardUtils.getLocalDateTimeInMilliSec(logStopageMasterStopped.getMaxLogStopageTime()));

					data.setMinLogRunningTime(
							DashboardUtils.getLocalDateTimeInMilliSec(logStopageMasterRunned.getMinLogReceiveTime()));

					data.setMaxLogRunningTime(
							DashboardUtils.getLocalDateTimeInMilliSec(logStopageMasterRunned.getMaxLogReceiveTime()));
					return data;
				}
						).collect(Collectors.toList());

	}
}
