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
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.TicketListRequest;
import aisaac.payload.request.UserTileListRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.service.ApiConfigurationService;
import aisaac.service.GlobalSettingsService;
import aisaac.service.UbaService;
import aisaac.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/uba")
@RequiredArgsConstructor
public class UbaController {

	@Autowired
	private UbaService ubaService;

	@Autowired
	private GlobalSettingsService globalSettingsService;

	@Autowired
	private ApiConfigurationService apiConfigurationService;

	// URL :: http://localhost:9004/v1/uba/department/list
	@GetMapping("/department/list")
	public ResponseEntity<Object> getAdUserDetailDepartmentList(@RequestParam Long tenantId) {

		Object data = ubaService.getAdUserDetailDepartmentList(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9004/v1/uba/threat/list
	@PostMapping("/threat/list")
	public ResponseEntity<Object> getThreatList(@Valid @RequestBody ThreatListRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}

		Object data = ubaService.getThreatList(request);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	// URL :: http://localhost:9004/v1/uba/watchlist/add
	@PostMapping("/watchlist/add")
	public ResponseEntity<Object> addOrDeleteWatchListData(@Valid @RequestBody AddToWatchListRequest request,
			Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}

		Object data = ubaService.addOrDeleteWatchListDetails(request);

		ApiResponse response = new ApiResponse();
		response.setData(null);
		response.setStatus(200);
		response.setMessage(data.toString());
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9004/v1/uba/watchlist/auditTrail/{userId}/list
	@GetMapping("/watchlist/auditTrail/{userId}/list")
	public ResponseEntity<Object> getWatchListAuditTrailDetails(@PathVariable Long userId) {

		Object data = ubaService.getWatchListAuditTrail(userId);

		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9004/v1/uba/threat/export
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/threat/export")
	public ResponseEntity<Object> exportThreatList(@RequestBody ThreatListRequest request) throws IOException {

		GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

		Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
				AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);

		ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_UBA,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_UBA_EXPORT_PATH);

		String path = filePathGlobalSettings.getParamValue();
		Object result = ubaService.exportThreatList(request, limit, path);

		ApiResponse response = new ApiResponse();
		response.setData(result);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9004/v1/uba/tile/list
	@PostMapping("/tile/list")
	public ResponseEntity<Object> getUserTileList(@RequestBody UserTileListRequest request) {

		Object data = ubaService.getUserTileList(request);

		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/uba/dashboard/risk-score
	@GetMapping("/dashboard/risk-score")
	public ResponseEntity<Object> getRiskScoreDonutDetails(@RequestParam Long tenantId) {

		Object data = ubaService.getRiskScoreDonutDetails(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/uba/dashboard/watchlist/list
	@GetMapping("/dashboard/watchlist/chart")
	public ResponseEntity<Object> getWatchlistedUsers(@RequestParam Long tenantId) {

		Object data = ubaService.getWatchlistedUsers(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	// URL :: http://localhost:9005/v1/uba/dashboard/risk-users/list
	@GetMapping("/dashboard/risk-users/chart")
	public ResponseEntity<Object> getRiskUsersList(@RequestParam Long tenantId) {

		Object data = ubaService.getRiskUsersList(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	// URL :: http://localhost:9005/v1/uba/dashboard/ticket/list
	@PostMapping("/dashboard/ticket/list")
	public ResponseEntity<Object> getTicketList(@RequestBody @Valid TicketListRequest request, Errors errors) {
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			throw new ValidationException(errorMessages.toString());
		}
		Object data = ubaService.getTicketList(request);

		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

}
