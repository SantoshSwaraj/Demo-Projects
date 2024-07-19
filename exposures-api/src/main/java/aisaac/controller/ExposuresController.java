package aisaac.controller;

import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aisaac.entities.ApiConfiguration;
import aisaac.entities.GlobalSettings;
import aisaac.payload.request.ExposuresDetailsRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.service.ApiConfigurationService;
import aisaac.service.ExposuresService;
import aisaac.service.GlobalSettingsService;
import aisaac.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/ex")
@RequiredArgsConstructor
@Slf4j
public class ExposuresController {
	
	@Autowired
	private ExposuresService exposuresService;
	
	@Autowired
	private GlobalSettingsService globalSettingsService;
	
	@Autowired
	private ApiConfigurationService apiConfigurationService;
	
	// URL :: http://localhost:9004/v1/ex/exposures/list
		@PostMapping("/exposures/list")
		public ResponseEntity<Object> getExposuresList(@RequestBody ExposuresDetailsRequest request,@RequestParam Integer userId) {
			Object data = exposuresService.getExposuresList(request,userId,false);
			return new ResponseEntity<Object>(data, HttpStatus.OK);
		}
		
		// URL :: http://localhost:9004/v1/ex/exposures/list/path-export
		@SuppressWarnings("unchecked")
		@PostMapping("/exposures/list/path-export")
		public ResponseEntity<Object> exportExposuresList(
				@RequestBody ExposuresDetailsRequest request)
				throws IllegalArgumentException, IllegalAccessException, IOException {

			GlobalSettings globalSettings = globalSettingsService.getGlobalSettingsByParamTypeAndParamName(
					AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS,
					AppConstants.GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT);

			Integer limit = NumberUtils.toInt(globalSettings.getParamValue(),
					AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT);
		
			ApiConfiguration filePathGlobalSettings = apiConfigurationService.getApiSettingsByParamTypeAndParamName(
					AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_EXPOSURES,
					AppConstants.API_CONFIGURATION_SETTINGS_PARAM_NAME_EXPOSURES_EXPORT_PATH);
			
			String path = filePathGlobalSettings.getParamValue();
			Object result = exposuresService.exportExposuresList(request, limit, path);
			
			ApiResponse response = new ApiResponse();
			response.setData(result);
			response.setStatus(200);
			response.setMessage(null);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		
}
