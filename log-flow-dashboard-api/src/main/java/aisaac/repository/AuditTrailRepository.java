package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.AuditTrail;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long>,JpaSpecificationExecutor<AuditTrail> {

	<T> T findTop1ByModuleRecIdAndAuditTrailMasterIdOrderByRecIdDesc(Long moduleRecId, Long auditTrailMasterId, Class<T> type);

}
