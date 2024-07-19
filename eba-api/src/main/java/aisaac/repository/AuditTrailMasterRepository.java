package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.AuditTrailMaster;

public interface AuditTrailMasterRepository extends JpaRepository<AuditTrailMaster, Long> {

	<T> Optional<T> findByActionTypeAndModuleName(String actionType, String moduleName, Class<T> type);
	
	List<AuditTrailMaster> findAllByModuleName(String moduleName);

}
