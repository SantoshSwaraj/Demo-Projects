package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.GlobalSettings;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
	
	Optional<List<GlobalSettings>> findByParamType(String paramType);
	
	Optional<GlobalSettings> findByParamTypeAndParamName(String paramType, String paramName);
	
}