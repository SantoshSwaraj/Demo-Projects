package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.entities.ProductMaster;
import aisaac.payload.request.AddToLogFlowMonitorUserActionRequest;
import aisaac.payload.request.AuditTrailListRequest;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.payload.request.LogStopageDetectedDeviceCompositeKeyRequest;

public interface AddToLogFlowMonitorService {

	public Object getDetectedDevicesList(LogFlowDashboardDetailsRequest request,List<ProductMaster> productMasters);

	public Object getAutoCompleteDataForDetectedDevices(Map<String, String> request);

	public Object performAddToLogflowMonitorUserActions(AddToLogFlowMonitorUserActionRequest request,
			String actionType);

	public Object getAutoCompleteDataForDetectedDevicesWithOptionalOrganizationIDs(List<Integer> orgIds,
			String fieldName);

	public Object getDetectedDevicesListToAddLogFlowDetails(LogStopageDetectedDeviceCompositeKeyRequest request);
	
	public Object getDetectedDeviceListByRecIds(List<Long> recIds);
	
	public Object deleteProductByTenantId(DeleteAllProductRequest allProductRequest);
	
	public Object getListOfAutoCompleteDataForLogFlowMonitor(List<ProductMaster> productMasters);
	
	public Object getAuditTrailList(AuditTrailListRequest auditTrailListRequest);
}
