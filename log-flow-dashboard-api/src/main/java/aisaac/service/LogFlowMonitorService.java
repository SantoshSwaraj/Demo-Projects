package aisaac.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import aisaac.entities.ApiConfiguration;
import aisaac.entities.ProductMaster;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.DigestMailSettingRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.payload.request.LogFlowMonitorRequestAddProduct;
import aisaac.payload.request.LogFlowMonitorRequestEditProduct;

public interface LogFlowMonitorService {

	public Object getLogFlowMonitorList(LogFlowDashboardDetailsRequest request,List<ProductMaster> productMasters);

	public Object getAutoCompleteDataForLogFlowMonitor(Map<String, String> request);

	public Object performLogflowMonitorUserActions(String actionType, List<Long> recIds, String note, Long userId
			, Map<String, Long> statusMap, Long moduleRecId);

	public Object addProductToLogStoppageMaster(List<LogFlowMonitorRequestAddProduct> request,Map<String, Long> productTypeMap,String addAction);
	
	public Object logStoppageMasterEditProduct(List<LogFlowMonitorRequestEditProduct> request,Map<String, Long> productTypeMap);
	
	public Object uploadBulkLogFlowMonitorData(MultipartFile file,Integer userId);
	
	public Object getAutoCompleteDataForLFMWithOptionalOrganizationIDs(List<Integer> orgIds,String fieldName);
	
	public Object saveDigestEmailSettings(DigestMailSettingRequest request);
	
	public Object getLogStopageDetailsById(Long recId);
	
	public Object getLFDSettings(List<ProductMaster> productMasters,Integer exportLimit);
	
	public Object deleteProductByTenantId(DeleteAllProductRequest allProductRequest);
	
	public Object getListOfAutoCompleteDataForLogFlowMonitor(List<ProductMaster> productMasters);
	
	public Object getLFDBulkUploadTemplate(List<ProductMaster> productMastersList,ApiConfiguration filePathGlobalSettings,int exportLimit);

	public Object exportLogFlowMonitorList(LogFlowDashboardDetailsRequest request,List<ProductMaster> productMasters, Integer limit, String path);
}
