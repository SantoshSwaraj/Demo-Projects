package aisaac.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.LogStoppageWhitelistDevices;

public interface LogStopageWhiteListRepositary extends JpaRepository<LogStoppageWhitelistDevices, Long>,
		JpaSpecificationExecutor<LogStoppageWhitelistDevices> {

	public Page<LogStoppageWhitelistDevices> findAllByDeleted(boolean delete, Pageable pageable);

	public Page<LogStoppageWhitelistDevices> findAllByDeletedAndTenantIdIn(boolean delete, List<Long> tenantIds,
			Pageable pageable);

	public Long countByDeleted(boolean deleted);

	public List<LogStoppageWhitelistDevices> findAllByDeletedAndTenantIdInAndProductIdIn(boolean delete,
			List<Long> customerIds, List<Long> productIds);

	public List<LogStoppageWhitelistDevices> findAllByDeletedAndTenantId(boolean delete,Long tenantId);

	public List<LogStoppageWhitelistDevices> findAllByDeletedAndTenantIdIn(boolean delete, List<Long> tenantIds);
}
