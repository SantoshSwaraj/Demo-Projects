package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.entities.ProductMaster;
import aisaac.payload.request.AllowListRequestAddProduct;
import aisaac.payload.request.AllowListRequestEditProduct;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;

public interface AllowlistService {

	public Object getAllowListDevicesList(LogFlowDashboardDetailsRequest request,List<ProductMaster> productMasters);

	public Object getAutoCompleteDataForAllowList(Map<String, String> request);

	public Object allowlistUserActionDelete(List<Long> recIds, String note, Long long1);

	public Object userCustomizationDataInsertForLFD(Integer userId, String tabName, String fieldDetails);
	
	public Object userCustomizationDataResetForLFD(Integer userId, String tabName);

	public Object getUserCustomizationDetailsForLFD(Integer userId, String tabName);

	public Object addProductToAllowList(List<AllowListRequestAddProduct> request);

	public Object editAllowListProduct(List<AllowListRequestEditProduct> request);
	
	public Object deleteProductByTenantId(DeleteAllProductRequest allProductRequest);
	
	public Object getListOfAutoCompleteDataForLogFlowMonitor(List<ProductMaster> productMasters);
}
