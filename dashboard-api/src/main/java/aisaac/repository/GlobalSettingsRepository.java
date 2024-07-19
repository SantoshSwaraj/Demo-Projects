package aisaac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.GlobalSettings;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
	
	Optional<GlobalSettings> findByParamTypeAndParamName(String paramType, String paramName);
	
}