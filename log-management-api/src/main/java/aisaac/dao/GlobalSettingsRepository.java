package aisaac.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aisaac.model.GlobalSettings;

@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
	
	Optional<List<GlobalSettings>> findByParamType(String paramType);
	
	Optional<GlobalSettings> findByParamTypeAndParamName(String paramType, String paramName);
	
//	public String findParamValueByParamTypeAndParamName(String paramType, String paramName);

//	public List<String> getGlobalSettingsParamValueList(String paramType, String paramName);
	
}