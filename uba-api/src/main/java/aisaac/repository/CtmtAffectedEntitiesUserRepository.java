package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.CtmtAffectedEntitiesUser;

public interface CtmtAffectedEntitiesUserRepository extends JpaRepository<CtmtAffectedEntitiesUser, Long> {

	public List<CtmtAffectedEntitiesUser> findAllByUpdatedDateBetweenAndTenantId(LocalDateTime fromDate,
			LocalDateTime toDate, Long tenantId);
}
