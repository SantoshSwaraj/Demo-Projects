package aisaac.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import aisaac.entities.LogStoppageDetectedDevices;

public interface LogStopageDetectedDevicesRepository
		extends JpaRepository<LogStoppageDetectedDevices, Long>, JpaSpecificationExecutor<LogStoppageDetectedDevices> {

	public Page<LogStoppageDetectedDevices> findAllByDeleted(boolean delete, Pageable pageable);

	public Page<LogStoppageDetectedDevices> findAllByDeletedAndTenantIdIn(boolean delete, List<Long> tenantIds,
			Pageable pageable);

	public Long countByDeleted(boolean deleted);

//	@Query("select lsd from LogStoppageDetectedDevices lsd where deleted=:delete and productId in (:productIds) and tenantId in (:customerIds) and productIP in (:productIps) and productHostName in (:productHostNames)")
//	public List<LogStoppageDetectedDevices> findAllByQuery(boolean delete, List<Integer> productIds,
//			List<Long> customerIds, List<String> productIps, List<String> productHostNames);
	
	public List<LogStoppageDetectedDevices> findAllByDeletedAndProductIdInAndTenantIdInAndProductIPInAndProductHostNameIn(
			boolean delete, List<Integer> productIds, List<Long> customerIds, List<String> productIps,
			List<String> productHostNames);

	public List<LogStoppageDetectedDevices> findAllByDeletedAndRecIdIn(boolean delete, List<Long> recIds);

	public List<LogStoppageDetectedDevices> findAllByDeletedAndTenantId(boolean delete,Long tenantId);

	public List<LogStoppageDetectedDevices> findAllByDeletedAndTenantIdIn(boolean delete, List<Long> tenantIds);

}
