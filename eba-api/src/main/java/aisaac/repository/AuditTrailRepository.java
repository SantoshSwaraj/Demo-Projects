package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.AuditTrail;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

	<T> T findTop1ByAuditTrailMasterIdOrderByRecIdDesc(Long auditTrailMasterId, Class<T> type);

	List<AuditTrail> findAllByAuditTrailMasterIdInAndModuleRecIdOrderByCreatedDateDesc(List<Long> recIds, Long adUserId);

}
