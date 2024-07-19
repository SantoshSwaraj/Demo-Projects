package aisaac.service;

import aisaac.entities.ApiConfiguration;

public interface ApiConfigurationService {
	public ApiConfiguration getApiSettingsByParamTypeAndParamName(String paramType, String paramName);
}
