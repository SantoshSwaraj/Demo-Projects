package aisaac.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.dto.ModuleMasterIdOnly;
import aisaac.entities.ApiConfiguration;
import aisaac.entities.GlobalSettings;
import aisaac.exception.ValidationException;
import aisaac.payload.request.AddToLogFlowMonitorUserActionRequest;
import aisaac.payload.request.AllowListRequestAddProduct;
import aisaac.payload.request.AllowListRequestEditProduct;
import aisaac.payload.request.AuditTrailListRequest;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.DigestMailSettingRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.payload.request.LogFlowMonitorAndAllowListUserActionsRequest;
import aisaac.payload.request.LogFlowMonitorRequestAddProduct;
import aisaac.payload.request.LogFlowMonitorRequestEditProduct;
import aisaac.payload.request.LogStopageDetectedDeviceCompositeKeyRequest;
import aisaac.payload.request.ValidateRequestBodyList;
import aisaac.payload.response.ApiResponse;
import aisaac.service.AddToLogFlowMonitorService;
import aisaac.service.AllowlistService;
import aisaac.service.ApiConfigurationService;
import aisaac.service.CommonService;
import aisaac.service.GlobalSettingsService;
import aisaac.service.LogFlowMonitorService;
import aisaac.service.ProductMasterService;
import aisaac.util.AddToLogFlowMonitorExcel;
import aisaac.util.AllowListExcel;
import aisaac.util.AppConstants;
import aisaac.util.AuditTrailExcel;
import aisaac.util.ExcelUtils;
import aisaac.util.LogFlowMonitorExcel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tm-lfd")
@RequiredArgsConstructor
public class LogFlowMonitorController {

	@Autowired
	private LogFlowMonitorService logFlowMonitorService;

	@Autowired
	private AddToLogFlowMonitorService addToLogFlowMonitorService;

	@Autowired
	private AllowlistService allowlistService;

	@Autowired
	private GlobalSettingsService globalSettingsService;

	@Autowired
	private ProductMasterService productMasterService;

	@Autowired
	private ApiConfigurationService apiConfigurationService;
	
	@Autowired
	private CommonService commonService;

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/list
	@PostMapping("/log-flow-monitoring/list")
	public ResponseEntity<Object> getLogFlowMonitorList(@RequestBody LogFlowDashboardDetailsRequest request) {
		Object data = logFlowMonitorService.getLogFlowMonitorList(request,
				productMasterService.getAllProductMasterWithProductType());
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/list/export NOT IN
	// USE
	@SuppressWarnings("unchecked")
	@PostMapping("/log-flow-monitoring/list/export")
	public void exportLogFlowMonitorDetails(@RequestBody LogFlowDashboardDetailsRequest request,
			HttpServletResponse response) throws IllegalArgumentException, IllegalAccessException, IOException {

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

		DatatablePage<Object> datatable = (DatatablePage<Object>) logFlowMonitorService.getLogFlowMonitorList(request,
				productMasterService.getAllProductMasterWithProductType());

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileName(AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_MONITORING_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
				itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				AppConstants.DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX + fileName);

//		ExcelUtils.download(new LogFlowMonitorExcel(listData), response);

	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/log-flow-monitoring/list?productVendor=
	@GetMapping("/log-flow-monitoring/list")
	public ResponseEntity<Object> getAutoCompleteDataForLogFlowMonitoringAdavanceSearch(
			@RequestParam Map<String, String> request) {
		Object data = logFlowMonitorService.getAutoCompleteDataForLogFlowMonitor(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices/list
	@PostMapping("/detected-devices/list")
	public ResponseEntity<Object> getDetectedDevicesList(@RequestBody LogFlowDashboardDetailsRequest request) {
		Object data = addToLogFlowMonitorService.getDetectedDevicesList(request,
				productMasterService.getAllProductMasterWithProductType());
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices/list/export
	@SuppressWarnings("unchecked")
	@PostMapping("/detected-devices/list/export")
	public void exportLogFlowDetectedDeviceDetails(@RequestBody LogFlowDashboardDetailsRequest request,
			HttpServletResponse response) throws IllegalArgumentException, IllegalAccessException, IOException {

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

		DatatablePage<Object> datatable = (DatatablePage<Object>) addToLogFlowMonitorService
				.getDetectedDevicesList(request, productMasterService.getAllProductMasterWithProductType());

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileName(AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_ADD_TO_LOGFLOW_MONITORING_FILENAME, currentDateTime,
				moreThanOnePartsAvailable, part, itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				AppConstants.DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX + fileName);

		ExcelUtils.download(new AddToLogFlowMonitorExcel(listData), response);

	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/list
	@PostMapping("/allowlist/list")
	public ResponseEntity<Object> getAllowListDevicesList(@RequestBody LogFlowDashboardDetailsRequest request) {
		Object data = allowlistService.getAllowListDevicesList(request,
				productMasterService.getAllProductMasterWithProductType());
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/list/export
	@SuppressWarnings("unchecked")
	@PostMapping("/allowlist/list/export")
	public void exportLogFlowAllowListDeviceDetails(@RequestBody LogFlowDashboardDetailsRequest request,
			HttpServletResponse response) throws IllegalArgumentException, IllegalAccessException, IOException {

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

		DatatablePage<Object> datatable = (DatatablePage<Object>) allowlistService.getAllowListDevicesList(request,
				productMasterService.getAllProductMasterWithProductType());

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileName(AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_ALLOWLIST_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
				itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				AppConstants.DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX + fileName);

		ExcelUtils.download(new AllowListExcel(listData), response);

	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices/list?productVendor=
	@GetMapping("/detected-devices/list")
	public ResponseEntity<Object> getAutoCompleteDataForDetectedDevicesAdavanceSearch(
			@RequestParam Map<String, String> request) {
		Object data = addToLogFlowMonitorService.getAutoCompleteDataForDetectedDevices(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/list?productVendor=
	@GetMapping("/allowlist/list")
	public ResponseEntity<Object> getAutoCompleteDataForAllowListDevicesAdavanceSearch(
			@RequestParam Map<String, String> request) {
		Object data = allowlistService.getAutoCompleteDataForAllowList(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/log-flow-monitoring/action/{actionType}
	// actionType can be suppress, enable, disable, delete, addNote, resume
	@PostMapping("/log-flow-monitoring/action/{actionType}")
	public ResponseEntity<Object> performLogflowMonitorUserActions(@PathVariable String actionType,
			@Valid @RequestBody LogFlowMonitorAndAllowListUserActionsRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Map<String, Long> statusMap = commonService.mapSysParamValueByType(AppConstants.SYSPARAM_TYPE_STATUS);
		
		ModuleMasterIdOnly moduleMasterIdOnly = commonService.getModuleMasterId(AppConstants.MODULE_MASTER_NAME_LOG_FLOW_DASHBOARD, AppConstants.MODULE_MASTER_SECTION_NAME_LOG_MONITORING);
		
		Long moduleRecId = moduleMasterIdOnly.recId();
		
		Object msg = logFlowMonitorService.performLogflowMonitorUserActions(actionType, request.getRecIds(),
				request.getAddNote(), request.getUserId(), statusMap, moduleRecId);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices/action/{actionType}
	// actionType can be delete or addNote
	@PostMapping("/detected-devices/action/{actionType}")
	public ResponseEntity<Object> performAddToLogflowMonitorUserActions(@PathVariable String actionType,
			@Valid @RequestBody AddToLogFlowMonitorUserActionRequest request, Errors errors) {
		Map<String, Object> errorMap = new LinkedHashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object msg = addToLogFlowMonitorService.performAddToLogflowMonitorUserActions(request, actionType);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/action/delete
	@PostMapping("/allowlist/action/delete")
	public ResponseEntity<Object> performAllowlistUserActions(
			@RequestBody LogFlowMonitorAndAllowListUserActionsRequest request) {
		Object msg = allowlistService.allowlistUserActionDelete(request.getRecIds(), request.getAddNote(),
				request.getUserId());
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/{tabName}/uc/apply
	@PostMapping("/{tabName}/uc/apply")
	public ResponseEntity<Object> userCustomizationDataInsertForLFD(@PathVariable String tabName,
			@RequestBody String fieldDetails, @RequestParam Integer userId) {

		Object msg = allowlistService.userCustomizationDataInsertForLFD(userId, tabName, fieldDetails);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/{tabName}/uc/reset
	@DeleteMapping("/{tabName}/uc/reset")
	public ResponseEntity<Object> userCustomizationDataResetForLFD(@PathVariable String tabName,
			@RequestParam Integer userId) {

		Object msg = allowlistService.userCustomizationDataResetForLFD(userId, tabName);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/uc/{tabName}/list
	@GetMapping("/uc/{tabName}/list")
	public ResponseEntity<Object> userCustomizationDetailsForLFD(@PathVariable String tabName,
			@RequestParam Integer userId) {
		try {
			Object Data = allowlistService.getUserCustomizationDetailsForLFD(userId, tabName);
			ApiResponse response = new ApiResponse();
			response.setData(Data);
			response.setStatus(200);
			response.setMessage(null);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(String.format("Something went wrong due to %s", e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/product/add
	@PostMapping("/log-flow-monitoring/product/add")
	public ResponseEntity<Object> addProductToLogFlowMonitor(
			@Valid @RequestBody ValidateRequestBodyList<LogFlowMonitorRequestAddProduct> valRequest, Errors errors,
			@RequestParam(value = "action") String addAction) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		List<LogFlowMonitorRequestAddProduct> requests = valRequest.getRequestBody();
		Object data = logFlowMonitorService.addProductToLogStoppageMaster(requests,
				productMasterService.getProductTypeMap(), addAction);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/product/edit
	@PutMapping("/log-flow-monitoring/product/edit")
	public ResponseEntity<Object> editProductLogFlowMonitor(
			@Valid @RequestBody ValidateRequestBodyList<LogFlowMonitorRequestEditProduct> valRequest, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		List<LogFlowMonitorRequestEditProduct> requests = valRequest.getRequestBody();

		Object data = logFlowMonitorService.logStoppageMasterEditProduct(requests,
				productMasterService.getProductTypeMap());
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/product/add
	@PostMapping("/allowlist/product/add")
	public ResponseEntity<Object> addProductToAllowList(
			@Valid @RequestBody ValidateRequestBodyList<AllowListRequestAddProduct> valRequests, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		List<AllowListRequestAddProduct> requests = valRequests.getRequestBody();
		Object data = allowlistService.addProductToAllowList(requests);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/product/edit
	@PutMapping("/allowlist/product/edit")
	public ResponseEntity<Object> editProductToAllowList(
			@Valid @RequestBody ValidateRequestBodyList<AllowListRequestEditProduct> valRequests, Errors errors) {

		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		List<AllowListRequestEditProduct> requests = valRequests.getRequestBody();
		Object data = allowlistService.editAllowListProduct(requests);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/file/download
	@GetMapping("/log-flow-monitoring/file/download")
	public void downloadLogFlowMonitorSampleFile(HttpServletResponse response) throws IOException {
		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_LFD_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_SAMPLE_FILE);
		if (ObjectUtils.isEmpty(globalSettings) || StringUtils.isBlank(globalSettings.getParamValue())) {
			throw new ValidationException("Global Settings not found to download sample file ");
		}
		Resource resource = new FileSystemResource(globalSettings.getParamValue());
		if (resource.exists()) {
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, AppConstants.DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX
					+ AppConstants.LOG_FLOW_MONITOR_DOWNLOAD_FILE_NAME);

			InputStream inputStream = resource.getInputStream();
			ServletOutputStream outputStream = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
		}
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/upload
	@PostMapping("/log-flow-monitoring/upload")
	public ResponseEntity<Object> uploadLogFlowMonitoringData(@RequestParam("file") MultipartFile multipartFile,
			@RequestParam("userId") Integer userId) {

		Object msg = logFlowMonitorService.uploadBulkLogFlowMonitorData(multipartFile, userId);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/{productFieldName}/list
	@GetMapping("/{productFieldName}/list")
	public ResponseEntity<Object> getProductMasterFieldListByName(@PathVariable String productFieldName) {

		Object data = productMasterService.getProductMasterFieldListByName(productFieldName);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/log-flow-monitoring/{productFieldName}/list
	@GetMapping("/log-flow-monitoring/{productFieldName}/list")
	public ResponseEntity<Object> getLFMFieldListByName(@PathVariable String productFieldName,
			@RequestBody List<Integer> orgIDs) {

		Object data = logFlowMonitorService.getAutoCompleteDataForLFMWithOptionalOrganizationIDs(orgIDs,
				productFieldName);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/detected-devices/{productFieldName}/list
	@GetMapping("/detected-devices/{productFieldName}/list")
	public ResponseEntity<Object> getAddToLFMFieldListByName(@PathVariable String productFieldName,
			@RequestBody List<Integer> orgIDs) {

		Object data = addToLogFlowMonitorService
				.getAutoCompleteDataForDetectedDevicesWithOptionalOrganizationIDs(orgIDs, productFieldName);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/digest-mail-settings/save
	@PostMapping("/digest-mail-settings/save")
	public ResponseEntity<Object> saveDigestMailSettings(@Valid @RequestBody DigestMailSettingRequest request,
			Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object data = logFlowMonitorService.saveDigestEmailSettings(request);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(data.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/{rec_id}
	@GetMapping("/log-flow-monitoring/{rec_id}")
	public ResponseEntity<Object> getLogStopageDetailsById(@PathVariable Long rec_id) {

		Object data = logFlowMonitorService.getLogStopageDetailsById(rec_id);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/settings
	@GetMapping("/log-flow-monitoring/settings")
	public ResponseEntity<Object> getCustomTrueSettings() {
		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);
		Object data = logFlowMonitorService.getLFDSettings(productMasterService.getAllProductMasterWithProductType(),
				limit);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices/compositeKey
	@PostMapping("/detected-devices/compositeKey")
	public ResponseEntity<Object> getDetectedDevicesListToAddLogFlowDetailsByCompositeKey(
			@Valid @RequestBody LogStopageDetectedDeviceCompositeKeyRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object data = addToLogFlowMonitorService.getDetectedDevicesListToAddLogFlowDetails(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices
	@PostMapping("/detected-devices")
	public ResponseEntity<Object> getDetectedDevicesListToAddLogFlowDetails(@RequestBody List<Long> request) {

		Object data = addToLogFlowMonitorService.getDetectedDeviceListByRecIds(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/list/path-export
	@PostMapping("/log-flow-monitoring/list/path-export")
	public ResponseEntity<Object> exportLogFlowMonitorDetailsToPath(@RequestBody LogFlowDashboardDetailsRequest request)
			throws IllegalArgumentException, IllegalAccessException, IOException {

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_LFD_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();

		Object result = logFlowMonitorService.exportLogFlowMonitorList(request,
				productMasterService.getAllProductMasterWithProductType(), limit, path);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	// URL :: http://localhost:9900/v1/tm-lfd/detected-devices/list/path-export
	@SuppressWarnings("unchecked")
	@PostMapping("/detected-devices/list/path-export")
	public ResponseEntity<Object> exportLogFlowDetectedDeviceDetailsToPath(
			@RequestBody LogFlowDashboardDetailsRequest request)
			throws IllegalArgumentException, IllegalAccessException, IOException {

		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIME_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		
		// when Date selected as local time format in UI
		if (StringUtils.isNotBlank(request.getDateString())) {
			currentDateTime = request.getDateString();
		}
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

		DatatablePage<Object> datatable = (DatatablePage<Object>) addToLogFlowMonitorService
				.getDetectedDevicesList(request, productMasterService.getAllProductMasterWithProductType());

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
				AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_ADD_TO_LOGFLOW_MONITORING_FILENAME, currentDateTime,
				moreThanOnePartsAvailable, part, itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_LFD_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		path = ExcelUtils.downloadToPath(new AddToLogFlowMonitorExcel(listData), path, fileName);
		
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			//first time only data is less than export limit
			if((datatable.getRecordsTotal() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart((datatable.getRecordsTotal() - offset) <= limit);
		} 

		ApiResponse response = new ApiResponse();
		response.setData(Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.EXPORT_FILE_NAME, fileName,
				AppConstants.PART_FILE_INFO, returnDataTable));
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/allowlist/list/path-export
	@SuppressWarnings("unchecked")
	@PostMapping("/allowlist/list/path-export")
	public ResponseEntity<Object> exportLogFlowAllowListDeviceDetailsToPath(
			@RequestBody LogFlowDashboardDetailsRequest request)
			throws IllegalArgumentException, IllegalAccessException, IOException {

		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
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

		DatatablePage<Object> datatable = (DatatablePage<Object>) allowlistService.getAllowListDevicesList(request,
				productMasterService.getAllProductMasterWithProductType());

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
				AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_ALLOWLIST_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
				itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_LFD_EXPORT_PATH);
		String path = filePathGlobalSettings.getParamValue();
		path = ExcelUtils.downloadToPath(new AllowListExcel(listData), path, fileName);
		
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			//first time only data is less than export limit
			if((datatable.getRecordsTotal() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart((datatable.getRecordsTotal() - offset) <= limit);
		} 

		ApiResponse response = new ApiResponse();
		response.setData(Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable));
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9900/v1/tm-lfd/log-flow-monitoring/file/download-path
	@GetMapping("/log-flow-monitoring/file/download-path")
	public ResponseEntity<Object> downloadLogFlowMonitorSampleFilePath() {

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_LFD_EXPORT_PATH);
		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);
		if (ObjectUtils.isEmpty(globalSettings) || StringUtils.isBlank(globalSettings.getParamValue())) {
			throw new EntityNotFoundException("Export Limit is not Configured in Global Settings ");
		}
		Object result = logFlowMonitorService.getLFDBulkUploadTemplate(
				productMasterService.getAllProductMasterWithProductType(), filePathGlobalSettings,
				Integer.parseInt(globalSettings.getParamValue()));

		ApiResponse response = new ApiResponse();
		response.setData(result.toString());
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/log-flow-monitoring/file/old/download-path
	@GetMapping("/log-flow-monitoring/file/old/download-path")
	public ResponseEntity<Object> returnDownloadLogFlowMonitorSampleFilePath() throws IOException {
		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_LFD_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_SAMPLE_FILE);
		if (ObjectUtils.isEmpty(globalSettings) || StringUtils.isBlank(globalSettings.getParamValue())) {
			throw new ValidationException("Global Settings not found to download sample file ");
		}
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());

		String paramPath = globalSettings.getParamValue();
		String path = paramPath.substring(0, paramPath.lastIndexOf("/"));
		String fileName = paramPath.substring(paramPath.lastIndexOf("/"), paramPath.lastIndexOf("."));
		String extension = paramPath.substring(paramPath.lastIndexOf("."));

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		fileName = path + AppConstants.LOG_FLOW_DASHBOARD_FILENAME_FORWARD_SLASH + fileName
				+ AppConstants.LOG_FLOW_DASHBOARD_SEPARATOR_UNDERSCORE + currentDateTime + extension;

		Path sourcePath = Paths.get(paramPath);
		Path destinationPath = Paths.get(path).resolve(fileName);
		Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

		ApiResponse response = new ApiResponse();
		response.setData(fileName);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

//URL ::// http://localhost:9900/v1/tm-lfd/log-flow-monitoring/action/delete/all
	@PostMapping("/log-flow-monitoring/action/delete/all")
	public ResponseEntity<Object> deleteLogFlowProductsByOrganization(
			@Valid @RequestBody DeleteAllProductRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object msg = logFlowMonitorService.deleteProductByTenantId(request);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

//URL ::// http://localhost:9900/v1/tm-lfd/detected-devices/action/delete/all
	@PostMapping("/detected-devices/action/delete/all")
	public ResponseEntity<Object> deleteDetectedProductsByOrganization(
			@Valid @RequestBody DeleteAllProductRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object msg = addToLogFlowMonitorService.deleteProductByTenantId(request);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

//URL ::// http://localhost:9900/v1/tm-lfd/allowlist/action/delete/all
	@PostMapping("/allowlist/action/delete/all")
	public ResponseEntity<Object> deleteWhilteListProductsByOrganization(
			@Valid @RequestBody DeleteAllProductRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object msg = allowlistService.deleteProductByTenantId(request);
		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(msg.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/log-flow-monitoring/auto-suggestion/list
	@GetMapping("/log-flow-monitoring/auto-suggestion/list")
	public ResponseEntity<Object> getListOfAutoCompleteDataForLogFlowMonitor() {
		Object data = logFlowMonitorService
				.getListOfAutoCompleteDataForLogFlowMonitor(productMasterService.getAllProductMasterWithProductType());
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/detected-devices/auto-suggestion/list
	@GetMapping("/detected-devices/auto-suggestion/list")
	public ResponseEntity<Object> getListOfAutoCompleteDataForDetectedDevices() {
		Object data = addToLogFlowMonitorService
				.getListOfAutoCompleteDataForLogFlowMonitor(productMasterService.getAllProductMasterWithProductType());
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9900/v1/tm-lfd/allowlist/auto-suggestion/list
	@GetMapping("/allowlist/auto-suggestion/list")
	public ResponseEntity<Object> getListOfAutoCompleteDataForAllowList() {
		Object data = allowlistService
				.getListOfAutoCompleteDataForLogFlowMonitor(productMasterService.getAllProductMasterWithProductType());
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL ::
	// http://localhost:9003/v1/tm-lfd/auditTrail/list
	@PostMapping("/auditTrail/list")
	public ResponseEntity<Object> getAuditTrailList(@RequestBody AuditTrailListRequest auditTrailListRequest) {
		Object data = addToLogFlowMonitorService.getAuditTrailList(auditTrailListRequest);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}
	
	// URL ::
	// http://localhost:9003/v1/tm-lfd/auditTrail/list/export
	@SuppressWarnings("unchecked")
	@PostMapping("/auditTrail/list/export")
	public ResponseEntity<Object> auditTrailListExport(@RequestBody AuditTrailListRequest request) throws IOException {

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

		DatatablePage<Object> datatable = (DatatablePage<Object>) addToLogFlowMonitorService.getAuditTrailList(request);

		List<Object> listData = datatable.getData();

		boolean moreThanOnePartsAvailable = Integer.compare(datatable.getRecordsTotal(), limit) > 0;

		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
				AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_AUDIT_TRAIL_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
				itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_LFD_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		path = ExcelUtils.downloadToPath(new AuditTrailExcel(listData), path, fileName);

		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			// first time only data is less than export limit
			if ((datatable.getRecordsTotal() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart((datatable.getRecordsTotal() - offset) <= limit);
		}

		ApiResponse response = new ApiResponse();
		response.setData(Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable));
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}
}
