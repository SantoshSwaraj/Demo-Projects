package aisaac.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.ThreatCopy;

public interface ThreatCopyRepository extends JpaRepository<ThreatCopy, Long>,JpaSpecificationExecutor<ThreatCopy>{

	public Page<ThreatCopy> findAllByTenantId(Long tenantId, Pageable pageable);
}
