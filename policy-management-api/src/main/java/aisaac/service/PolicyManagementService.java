package aisaac.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.dto.PolicyManagementDTO;
import aisaac.payload.request.PolicyManagementDetailsRequest;
import aisaac.payload.response.PolicyManagementFindingRemediationResponse;
import aisaac.payload.response.PolicyManagementRemediationRefreshResponse;
import jakarta.persistence.EntityManager;

public interface PolicyManagementService {

	public Object getPolicyManagementList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport);

	public Object getcspmFindingList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport);
	
	public Object getAutoCompleteDataForPolicyManagementMonitor(Map<String, String> request);

	public Object getPolicyManagementListForHistoryFindings(String cloudAccountId, String securityControlId, PolicyManagementDetailsRequest request);

	Object getDataWithOptionalOrganizationID(PolicyManagementDetailsRequest request);

	public PolicyManagementFindingRemediationResponse remediateWithFindingId(BigInteger complianceFindingId, Integer userId);

	public PolicyManagementRemediationRefreshResponse remediateFindingWithRefreshStatus(BigInteger complianceFindingId, Integer userId);

	Map<String, Object> getUserCustomization(Long userId, String moduleName, String sectionName);

	String updateOrAddUserCustomColumn(Long userId, String moduleName, String sectionName, String userCustomizedJson);
	
	void deleteUserCustomColumn(Long userId, String moduleName, String sectionName);
	//public List<PolicyManagementDTO> getCountByCspmFinding();
	
	public Map<String, Object> getCountByCspmFinding(Map<String, Object> collectedMap, boolean conditionCheck, PolicyManagementDetailsRequest request, Pageable pageable,String paramValue,String defaultSearchDays,String recordState,String cspmFindingVendorName, String cspmFindingProductName);
	
	public Object getAwsConfigFindings(String resourceId,Integer tenantId);
	
	public Object getAwsConfigFindings(String resourceId,Integer tenantId,DataTableRequest dataTableRequest);

	Object exportPolicyManagementList(PolicyManagementDetailsRequest request, Integer limit, String path,
			Integer userId);

	public Object getcspmFindingAzureList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport);

	public Map<String, Object> getCountByCspmFindingAzure(Map<String, Object> collectedMap, boolean conditionCheck,
			PolicyManagementDetailsRequest request, Pageable pageable, String sourceName, String defaultSearchDays,
			String vendorName, String cspmFindingProductName,String recordState);

	Object exportPolicyManagementAzureList(PolicyManagementDetailsRequest request, Integer limit, String path,
			Integer userId);

	Object getPolicyManagementAzureListForHistoryFindings(String securityControlId, String cloudResourceId,
			PolicyManagementDetailsRequest request);

	public Object getcspmFindingPrismaCloudList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport);

	Map<String, Object> getCountByCspmFindingPlasmaCloud(Map<String, Object> collectedMap, boolean conditionCheck,
			PolicyManagementDetailsRequest request, Pageable pageable, String sourceName, String defaultSearchDays,
			String vendorName, String productName, String recordState);

	Object getPolicyManagementPlasmaCloudListForHistoryFindings(String securityControlId, String cloudResourceId,
			PolicyManagementDetailsRequest request);

	Object exportPolicyManagementPrismaCloudList(PolicyManagementDetailsRequest request, Integer limit, String path,
			Integer userId);



	//Object exportPolicyManagementList(PolicyManagementDetailsRequest request, Integer limit, String path, Integer userId);
	
	}
