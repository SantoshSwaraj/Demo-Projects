package aisaac.controller;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RestController;

import aisaac.exception.ValidationException;
import aisaac.payload.request.CountryMapAlertRequest;
import aisaac.payload.request.DashboardCountRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.service.DashboardService;
import aisaac.util.AppConstants;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/cb/asset/{tenantId}")
	public ResponseEntity<Object> getAssetCount(@PathVariable Long tenantId) {

		Long count = dashboardService.getAssetCountByTenantId(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(count);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/cb/ticket")
	public ResponseEntity<Object> getTicketsCount(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getTicketsDetails(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/asset/{tenantId}/product")
	public ResponseEntity<Object> getAssetByProductNameAPI(@PathVariable Long tenantId) {

		Object data = dashboardService.getAssetByProductNamesAPI(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/ticket/open")
	public ResponseEntity<Object> getOpenTicketsCount(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getOpenTicketsCountDetails(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/ticket/re-opened")
	public ResponseEntity<Object> getReOpenedTicketsCount(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getReOpenedTicketsCountDetails(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/ticket/category")
	public ResponseEntity<Object> getTicketsCategory(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getTicketsByCategoryAPI(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/ticket/list")
	public ResponseEntity<Object> getTicketsList(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getTicketsList(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/ticket/{threatId}/threat")
	public ResponseEntity<Object> getThreatAlertDetails(@PathVariable BigInteger threatId) {

		Object data = dashboardService.getThreatAlertDetails(threatId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/ticket/{ticketId}/threat/list")
	public ResponseEntity<Object> getThreatAlertListByTicketId(@PathVariable Long ticketId) {

		Object data = dashboardService.getThreatAlertListByTicketId(ticketId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/asset/{tenantId}/criticality")
	public ResponseEntity<Object> getAssetCriticalityDetails(@PathVariable Long tenantId) {

		Object data = dashboardService.getAssetByCriticalityDetails(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/ticket/closed")
	public ResponseEntity<Object> getClosedTicketsCount(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getClosedTicketsCountDetails(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/cb/threat")
	public ResponseEntity<Object> getThreatCountDetails(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getThreatCountDetails(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/threat/products")
	public ResponseEntity<Object> getThreatCountByProductName(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getThreatCountByProductName(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/asset/ticket/priority")
	public ResponseEntity<Object> getAssetByTicketPriorityCount(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getAssetByTicketPriorityCount(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/asset/ticket/priority/level")
	public ResponseEntity<Object> getAssetByTicketPriorityLevelCount(
			@RequestBody @Valid DashboardCountRequest countRequest, Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getAssetByTicketPriorityLevelCount(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/ticket/{tenantId}/by-age")
	public ResponseEntity<Object> getAgeOfTicketsDetails(@PathVariable Long tenantId) {

		Object data = dashboardService.getAgeOfTicketsDetails(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/threat/countries")
	public ResponseEntity<Object> getThreatCountriesData(@RequestBody @Valid DashboardCountRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getThreatCountriesData(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping("/threat/country/pop-up")
	public ResponseEntity<Object> getCountryMapDetailsForAlert(@RequestBody @Valid CountryMapAlertRequest countRequest,
			Errors errors) {
		Map<String, Object> errorMap = new HashMap<>();
		if (errors.hasErrors()) {
			List<String> errorMessages = errors.getAllErrors().stream()
					.filter(item -> StringUtils.isNotBlank(item.getDefaultMessage()))
					.map(ObjectError::getDefaultMessage).collect(Collectors.toList());
			errorMap.put(AppConstants.MESSAGE, errorMessages);
			throw new ValidationException(errorMessages.toString());
		}
		Object data = dashboardService.getCountryMapDetailsForAlert(countRequest);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/asset/{tenantId}/log-flow-status")
	public ResponseEntity<Object> getAssetByLogFlowStatusDetails(@PathVariable Long tenantId) {

		Object data = dashboardService.getAssetByLogFlowStatusDetails(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/avg-response/ticket/{tenantId}/category")
	public ResponseEntity<Object> getAverageResponseTimeForTicketCategory(@PathVariable Long tenantId) {

		Object data = dashboardService.getAverageResponseTimeForTicketCategory(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@GetMapping("/avg-response/{tenantId}/ticket")
	public ResponseEntity<Object> getAverageResponseTime(@PathVariable Long tenantId) {

		Object data = dashboardService.getAverageResponseTime(tenantId);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@GetMapping("/settings")
	public ResponseEntity<Object> getDefaultSettings() {

		Object data = dashboardService.getDefaultSettings();
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

}
