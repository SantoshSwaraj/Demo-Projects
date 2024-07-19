package aisaac.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aisaac.entities.ApiConfiguration;
import aisaac.entities.GlobalSettings;
import aisaac.exception.ValidationException;
import aisaac.payload.request.AddToWatchListRequest;
import aisaac.payload.request.AssetsListRequest;
import aisaac.payload.request.EntitiesTileListRequest;
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.TicketListRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.service.ApiConfigurationService;
import aisaac.service.EBAService;
import aisaac.service.GlobalSettingsService;
import aisaac.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/eba")
@RequiredArgsConstructor
public class EBAController {

	@Autowired
	private EBAService ebaService;

	@Autowired
	private GlobalSettingsService globalSettingsService;

	@Autowired
	private ApiConfigurationService apiConfigurationService;

	// URL :: http://localhost:9005/v1/eba/tile/list
	@PostMapping("/tile/list")
	public ResponseEntity<Object> getEntitiesTileList(@RequestBody EntitiesTileListRequest request) {

		Object data = ebaService.getEntitiesTileList(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/eba/threat/list
	@PostMapping("/threat/list")
	public ResponseEntity<Object> getThreatList(@Valid @RequestBody ThreatListRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object data = ebaService.getThreatList(request);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/eba/watchlist/add
	@PostMapping("/watchlist/add")
	public ResponseEntity<Object> updateWatchListData(@Valid @RequestBody AddToWatchListRequest request,
			Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}

		Object data = ebaService.updateWatchListDetails(request);

		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(data.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/eba/watchlist/auditTrail/{recId}/list
	@GetMapping("/watchlist/auditTrail/{recId}/list")
	public ResponseEntity<Object> getWatchListAuditTrailDetails(@PathVariable Long recId) {

		Object data = ebaService.getWatchListAuditTrail(recId);

		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/eba/threat/export
	@PostMapping(value = "/threat/export")
	public ResponseEntity<Object> exportThreatList(@RequestBody ThreatListRequest request) throws IOException {

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);
		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_EBA,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_EBA_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		Object result = ebaService.exportThreatList(request, limit, path);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/eba/settings
	@GetMapping("/settings")
	public ResponseEntity<Object> getEBASettings() {

		Object data = ebaService.getEBASettings();
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/eba/asset/list
	@PostMapping("/asset/list")
	public ResponseEntity<Object> getAssetsList(@Valid @RequestBody AssetsListRequest assetsListRequest,
			Errors errors) {

		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object data = ebaService.getAssetsByUserEntityId(assetsListRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9006/v1/eba/dashboard/risk-score
	@GetMapping("/dashboard/risk-score")
	public ResponseEntity<Object> getRiskScoreDonutList(@RequestParam Long tenantId) {

		Object data = ebaService.getRiskScoreDonutList(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9006/v1/eba/dashboard/watchlist/list
	@GetMapping("/dashboard/watchlist/chart")
	public ResponseEntity<Object> getWatchListData(@RequestParam Long tenantId) {

		Object data = ebaService.getWatchlistedEntities(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	// URL :: http://localhost:9006/v1/eba/dashboard/risk-entity/list
	@GetMapping("/dashboard/risk-entity/chart")
	public ResponseEntity<Object> getRiskEntitiesListData(@RequestParam Long tenantId) {

		Object data = ebaService.getRiskEntitiesList(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	// URL :: http://localhost:9006/v1/eba/dashboard/ticket/list
	@PostMapping("/dashboard/ticket/list")
	public ResponseEntity<Object> getTicketList(@RequestBody @Valid TicketListRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object data = ebaService.getTicketList(request);

		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	// URL :: http://localhost:9006/v1/eba/asset/list/export
	@PostMapping(value = "/asset/list/export")
	public ResponseEntity<Object> mappedAssetexport(@Valid @RequestBody AssetsListRequest assetsListRequest,
			Errors errors) {

		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);
		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_EBA,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_EBA_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		Object result = ebaService.getMappedAssetExport(assetsListRequest, limit, path);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
}
