package aisaac.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.AssetMaster;

public interface AssetMasterRepository extends JpaRepository<AssetMaster, Long> {

	Page<AssetMaster> findAllByTenantIdAndIpAddressAndIsDeletedFalse(Long tenantID, String ipAddress,
			Pageable pageable);

}
