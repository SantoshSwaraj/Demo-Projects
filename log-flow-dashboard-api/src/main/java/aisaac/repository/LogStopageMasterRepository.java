package aisaac.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.LogStopageMaster;

public interface LogStopageMasterRepository
		extends JpaRepository<LogStopageMaster, Long>, JpaSpecificationExecutor<LogStopageMaster> {

	public Page<LogStopageMaster> findAllByDeleted(boolean delete, Pageable pageable);

	public Page<LogStopageMaster> findAllByDeletedAndTenantIdIn(boolean delete, List<Long> tenantIds,
			Pageable pageable);

	public Long countByDeleted(boolean deleted);

	public List<LogStopageMaster> findAllByDeletedAndTenantIdInAndProductIdInAndProductIPInAndProductHostNameIn(
			boolean delete, List<Long> customerIds, List<Integer> productIds, List<String> productIps,
			List<String> productHostNames);

	public List<LogStopageMaster> findAllByDeletedAndTenantId(boolean delete,Long tenantId);
	
	public List<LogStopageMaster> findAllByDeletedAndTenantIdIn(boolean delete, List<Long> tenantIds);

}
