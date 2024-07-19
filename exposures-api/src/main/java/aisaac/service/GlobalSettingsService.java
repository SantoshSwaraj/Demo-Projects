package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.entities.GlobalSettings;

public interface GlobalSettingsService {

	public List<GlobalSettings> getGlobalSettings(String type);

	public Map<String, String> getSettingsOfType(String type);

	public GlobalSettings getGlobalSettingsByParamTypeAndParamName(String paramType, String paramName);
}
