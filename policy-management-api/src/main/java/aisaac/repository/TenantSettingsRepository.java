package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.TenantSettings;

public interface TenantSettingsRepository extends JpaRepository<TenantSettings, Long>{
	
	Optional<List<TenantSettings>> findByParamTypeAndTenantId(String paramType, Integer tenantId);

}
