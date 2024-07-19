package aisaac.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.dto.AwsConfigRequestDto;
import aisaac.dto.PolicyManagementDTO;
import aisaac.entities.ApiConfiguration;
import aisaac.entities.GlobalSettings;
import aisaac.payload.request.PolicyManagementDetailsRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.payload.response.PolicyManagementFindingRemediationResponse;
import aisaac.payload.response.PolicyManagementRemediationRefreshResponse;
import aisaac.payload.response.PolicyManagementResponse;
import aisaac.service.ApiConfigurationService;
import aisaac.service.GlobalSettingsService;
import aisaac.service.PolicyManagementService;
import aisaac.util.AppConstants;
import aisaac.util.ExcelUtils;
import aisaac.util.PolicyManagementExcel;
import aisaac.util.ResponseMsgConstants;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/pm")
@RequiredArgsConstructor
@Slf4j
public class PolicyManagementController {

	@Autowired
	private PolicyManagementService policyManagementService;

	@Autowired
	private GlobalSettingsService globalSettingsService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ApiConfigurationService apiConfigurationService;

	// URL :: http://localhost:9900/v1/pm/policy-management/list
	@PostMapping("/policy-management/list")
	public ResponseEntity<Object> getPolicyManagementList(@RequestBody PolicyManagementDetailsRequest request,
			@RequestParam Integer userId) {
		// List<PolicyManagementDTO> p =
		// policyManagementService.getCountByCspmFinding();
		// Object data =
		// policyManagementService.getPolicyManagementList(request,userId,false);
		Object data = policyManagementService.getcspmFindingList(request, userId, false);

		// List<PolicyManagementDTO> pm =
		// policyManagementService.getCountByCspmFinding(null, 10, 0, updatedAt);

		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// azure list
	@PostMapping("/policy-management/azure/list")
	public ResponseEntity<Object> getPolicyManagementAzureList(@RequestBody PolicyManagementDetailsRequest request,
			@RequestParam Integer userId) {
		// List<PolicyManagementDTO> p =
		// policyManagementService.getCountByCspmFinding();
		// Object data =
		// policyManagementService.getPolicyManagementList(request,userId,false);
		Object data = policyManagementService.getcspmFindingAzureList(request, userId, false);

		// List<PolicyManagementDTO> pm =
		// policyManagementService.getCountByCspmFinding(null, 10, 0, updatedAt);

		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// prisma cloud list
	@PostMapping("/policy-management/prisma-cloud/list")
	public ResponseEntity<Object> getPolicyManagementPrismaCloudList(
			@RequestBody PolicyManagementDetailsRequest request, @RequestParam Integer userId) {
		// List<PolicyManagementDTO> p =
		// policyManagementService.getCountByCspmFinding();
		// Object data =
		// policyManagementService.getPolicyManagementList(request,userId,false);
		Object data = policyManagementService.getcspmFindingPrismaCloudList(request, userId, false);

		// List<PolicyManagementDTO> pm =
		// policyManagementService.getCountByCspmFinding(null, 10, 0, updatedAt);

		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/history/list
	@PostMapping("/policy-management/history/list")
	public ResponseEntity<Object> getPolicyManagementListForHistoryFindings(
			@RequestBody PolicyManagementDetailsRequest request) {
		Object data = policyManagementService.getPolicyManagementListForHistoryFindings(request.getSecurityControlId(),
				request.getCloudResourceId(), request);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/history/list
	@PostMapping("/policy-management/azure/history/list")
	public ResponseEntity<Object> getPolicyManagementAzureListForHistoryFindings(
			@RequestBody PolicyManagementDetailsRequest request) {
		Object data = policyManagementService.getPolicyManagementAzureListForHistoryFindings(
				request.getSecurityControlId(), request.getCloudResourceId(), request);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/history/list
	@PostMapping("/policy-management/prisma-cloud/history/list")
	public ResponseEntity<Object> getPolicyManagementPlasmaCoudListForHistoryFindings(
			@RequestBody PolicyManagementDetailsRequest request) {
		Object data = policyManagementService.getPolicyManagementPlasmaCloudListForHistoryFindings(
				request.getSecurityControlId(), request.getCloudResourceId(), request);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/list/export
	@SuppressWarnings("unchecked")
	@PostMapping("/policy-management/list/export")
	public void exportLogFlowMonitorDetails(@RequestBody PolicyManagementDetailsRequest request,
			@RequestParam Integer userId, HttpServletResponse response)
			throws IllegalArgumentException, IllegalAccessException, IOException {

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIME_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);
		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);

		DatatablePage<Object> datatable = (DatatablePage<Object>) policyManagementService
				.getPolicyManagementList(request, userId, true);

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileName(AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
				AppConstants.POLICY_MANAGEMENT_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, 0,
				itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_CSV);

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				AppConstants.DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX + fileName);

		boolean isMultiTenantUser = true;
		ExcelUtils.download(new PolicyManagementExcel(listData, isMultiTenantUser), response);

	}

	@GetMapping("/policy-management/finding/{finding_id}/remediate")
	public ResponseEntity<Object> remediateFinding(@PathVariable(value = "finding_id") BigInteger complianceFindingId,
			@RequestParam Integer userId) {
		PolicyManagementFindingRemediationResponse response = policyManagementService
				.remediateWithFindingId(complianceFindingId, userId);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	@GetMapping("/policy-management/finding/{finding_id}/remediate/status")
	public ResponseEntity<Object> remediateFindingStatus(@PathVariable("finding_id") BigInteger complianceFindingId,
			@RequestParam Integer userId) {
		PolicyManagementRemediationRefreshResponse response = null;
		try {

			response = policyManagementService.remediateFindingWithRefreshStatus(complianceFindingId, userId);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Exception occurred: " + e.getMessage(), e);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// localhost:9900/v1/pm/policy-management/list/path-export
	@SuppressWarnings("unchecked")
	@PostMapping("/policy-management/list/path-export")
	public ResponseEntity<Object> exportPolicyManagementList(@RequestBody PolicyManagementDetailsRequest request,
			@RequestParam Integer userId) throws IOException {

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_POLICY_MANAGEMENT,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_PM_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		Object result = policyManagementService.exportPolicyManagementList(request, limit, path, userId);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

// localhost:9900/v1/pm/policy-management/list/path-export
	@SuppressWarnings("unchecked")
	@PostMapping("/policy-management/azure/list/path-export")
	public ResponseEntity<Object> exportPolicyManagementAzureList(@RequestBody PolicyManagementDetailsRequest request,
			@RequestParam Integer userId) throws IOException {

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_POLICY_MANAGEMENT,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_PM_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		Object result = policyManagementService.exportPolicyManagementAzureList(request, limit, path, userId);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

//localhost:9900/v1/pm/policy-management/list/path-export
	@SuppressWarnings("unchecked")
	@PostMapping("/policy-management/prisma-cloud/list/path-export")
	public ResponseEntity<Object> exportPolicyManagementPrismaCloudList(
			@RequestBody PolicyManagementDetailsRequest request, @RequestParam Integer userId) throws IOException {

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_POLICY_MANAGEMENT,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_PM_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		Object result = policyManagementService.exportPolicyManagementPrismaCloudList(request, limit, path, userId);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

// URL :: http://localhost:9900/v1/pm/policy-management
	@GetMapping("/{sectionName}")
	public ResponseEntity<Object> getUserCustomization(@PathVariable String sectionName, @RequestParam Long userId)
			throws JsonProcessingException {

		Map<String, Object> pmMap = policyManagementService.getUserCustomization(userId,
				AppConstants.POLICY_MANAGEMENT_NAME, sectionName);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.FIND_USER_CUSTOMIZATION_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, sectionName, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(pmMap);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/uc/apply
	@PostMapping("/{sectionName}/uc/apply")
	public ResponseEntity<Object> applyUserCustomization(@PathVariable String sectionName,
			@RequestBody String userCustomizedJson, @RequestParam Long userId) throws JsonProcessingException {

		Object dto = policyManagementService.updateOrAddUserCustomColumn(userId, AppConstants.POLICY_MANAGEMENT_NAME,
				sectionName, userCustomizedJson);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.ADD_OR_UPDATE_USER_CUSTOMIZATION_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, sectionName, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(dto);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/uc/reset
	@DeleteMapping("/{sectionName}/uc/reset")
	public ResponseEntity<Object> resetUserCustomization(@PathVariable String sectionName, @RequestParam Long userId)
			throws JsonProcessingException {

		policyManagementService.deleteUserCustomColumn(userId, AppConstants.POLICY_MANAGEMENT_NAME, sectionName);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.USER_CUSTOMIZATION_RESET_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, sectionName, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(null);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management
	@GetMapping("/policy-management-azure")
	public ResponseEntity<Object> getUserAzureCustomization(@RequestParam Long userId) throws JsonProcessingException {

		Map<String, Object> pmMap = policyManagementService.getUserCustomization(userId,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_AZURE_SECTION_NAME);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.FIND_USER_CUSTOMIZATION_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_AZURE_SECTION_NAME, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(pmMap);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/uc/apply
	@PostMapping("/policy-management-azure/uc/apply")
	public ResponseEntity<Object> applyUserAzureCustomization(@RequestBody String userCustomizedJson,
			@RequestParam Long userId) throws JsonProcessingException {

		Object dto = policyManagementService.updateOrAddUserCustomColumn(userId, AppConstants.POLICY_MANAGEMENT_NAME,
				AppConstants.POLICY_MANAGEMENT_AZURE_SECTION_NAME, userCustomizedJson);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.ADD_OR_UPDATE_USER_CUSTOMIZATION_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_AZURE_SECTION_NAME, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(dto);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/uc/reset
	@DeleteMapping("/policy-management-azure/uc/reset")
	public ResponseEntity<Object> resetUserAzureCustomization(@RequestParam Long userId)
			throws JsonProcessingException {

		policyManagementService.deleteUserCustomColumn(userId, AppConstants.POLICY_MANAGEMENT_NAME,
				AppConstants.POLICY_MANAGEMENT_AZURE_SECTION_NAME);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.USER_CUSTOMIZATION_RESET_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_AZURE_SECTION_NAME, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(null);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management
	@GetMapping("/policy-management-prisma-cloud")
	public ResponseEntity<Object> getUserPrismaCloudCustomization(@RequestParam Long userId)
			throws JsonProcessingException {

		Map<String, Object> pmMap = policyManagementService.getUserCustomization(userId,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.FIND_USER_CUSTOMIZATION_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(pmMap);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/uc/apply
	@PostMapping("/policy-management-prisma-cloud/uc/apply")
	public ResponseEntity<Object> applyUserPrismaCloudCustomization(@RequestBody String userCustomizedJson,
			@RequestParam Long userId) throws JsonProcessingException {

		Object dto = policyManagementService.updateOrAddUserCustomColumn(userId, AppConstants.POLICY_MANAGEMENT_NAME,
				AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME, userCustomizedJson);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.ADD_OR_UPDATE_USER_CUSTOMIZATION_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(dto);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/uc/reset
	@DeleteMapping("/policy-management-prisma-cloud/uc/reset")
	public ResponseEntity<Object> resetUserPrismaCloudCustomization(@RequestParam Long userId)
			throws JsonProcessingException {

		policyManagementService.deleteUserCustomColumn(userId, AppConstants.POLICY_MANAGEMENT_NAME,
				AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME);
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage(String.format(ResponseMsgConstants.USER_CUSTOMIZATION_RESET_SUCCESS,
				AppConstants.POLICY_MANAGEMENT_NAME, AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME, userId));
		apiResponse.setStatus(200);
		apiResponse.setData(null);
		return ResponseEntity.ok().body(apiResponse);
	}

	// URL :: http://localhost:9900/v1/pm/policy-management/AWS/cspm
	@PostMapping("/policy-management/awsConfigFindings")
	public ResponseEntity<Object> getAwsConfigFindings(@RequestBody AwsConfigRequestDto awsConfigRequestDto)
			throws JsonProcessingException {
		String resourceId = awsConfigRequestDto.getCloudResourceId();
		Integer tenantId = awsConfigRequestDto.getTenantId();

		if (Objects.isNull(resourceId) || StringUtils.isEmpty(resourceId) || Objects.isNull(tenantId)) {
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}

		DatatablePage<Object> data = (DatatablePage<Object>) policyManagementService.getAwsConfigFindings(resourceId,
				tenantId);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	@PostMapping("/policy-management/awsConfigFindings/all")
	public ResponseEntity<Object> getAwsConfigFindingsAll(@RequestBody AwsConfigRequestDto awsConfigRequestDto)
			throws JsonProcessingException {

		String resourceId = awsConfigRequestDto.getCloudResourceId();
		Integer tenantId = awsConfigRequestDto.getTenantId();
		DataTableRequest dataTableRequest = awsConfigRequestDto.getDataTableRequest();

		if (Objects.isNull(resourceId) || StringUtils.isEmpty(resourceId) || Objects.isNull(dataTableRequest)) {
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}

		DatatablePage<Object> data = (DatatablePage<Object>) policyManagementService.getAwsConfigFindings(resourceId,
				tenantId, dataTableRequest);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

}
