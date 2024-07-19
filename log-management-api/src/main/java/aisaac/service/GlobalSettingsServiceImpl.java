package aisaac.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisaac.dao.GlobalSettingsRepository;
import aisaac.model.GlobalSettings;


@Service
@Transactional
public class GlobalSettingsServiceImpl implements GlobalSettingsService {

	@Autowired
	private GlobalSettingsRepository globalSettingsRepo;
	
	@Autowired
	public GlobalSettingsServiceImpl(GlobalSettingsRepository globalSettingsRepo) {
		this.globalSettingsRepo = globalSettingsRepo;
	}

	@Override
	public List<GlobalSettings> getGlobalSettings(String paramType) {
		return globalSettingsRepo.findByParamType(paramType)
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("No settings found for param type: ", paramType)));
	}

	@Override
	public Map<String, String> getSettingsOfType(String paramType) {
		return this.getGlobalSettings(paramType)
				.stream().collect(Collectors.toMap(GlobalSettings::getParamName, GlobalSettings::getParamValue));
	}

	@Override
	public GlobalSettings getGlobalSettingsByParamTypeAndParamName(String paramType, String paramName) {
		return globalSettingsRepo.findByParamTypeAndParamName(paramType, paramName)
				.orElseThrow(() -> new IllegalArgumentException(String
						.format("No settings found for param type: %s and param value: %s ", paramType, paramName)));
	}
	
	@Override
	public String getGlobalSettingsParamValue(String paramType, String paramName) {
		return globalSettingsRepo.findByParamTypeAndParamName(paramType, paramName).get().getParamValue();
	}
  
}
