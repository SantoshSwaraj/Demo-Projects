package aisaac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.ApiConfiguration;

public interface ApiConfigurationRepository extends JpaRepository<ApiConfiguration, Long>{

	Optional<ApiConfiguration> findByParamTypeAndParamName(String paramType, String paramName);
}
