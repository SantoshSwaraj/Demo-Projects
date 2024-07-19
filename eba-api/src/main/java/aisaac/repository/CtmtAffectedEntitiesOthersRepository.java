package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.CtmtAffectedEntitiesOthers;

public interface CtmtAffectedEntitiesOthersRepository extends JpaRepository<CtmtAffectedEntitiesOthers, Long> {

	public List<CtmtAffectedEntitiesOthers> findAllByCreatedDateBetweenAndTenantId(LocalDateTime fromDate,
			LocalDateTime toDate, Long tenantId);
}
