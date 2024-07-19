package aisaac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisaac.entities.ApiConfiguration;
import aisaac.repository.ApiConfigurationRepository;

@Service
@Transactional
public class ApiConfigurationServiceImpl implements ApiConfigurationService {

	@Autowired
	private ApiConfigurationRepository apiConfigurationRepository;

	@Override
	public ApiConfiguration getApiSettingsByParamTypeAndParamName(String paramType, String paramName) {
		return apiConfigurationRepository.findByParamTypeAndParamName(paramType, paramName)
				.orElseThrow(() -> new IllegalArgumentException(String
						.format("No settings found for param type: %s and param value: %s ", paramType, paramName)));
	}
}
