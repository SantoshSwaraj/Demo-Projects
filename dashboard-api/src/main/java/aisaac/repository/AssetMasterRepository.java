package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.dto.AssetIdDto;
import aisaac.entities.AssetMaster;

public interface AssetMasterRepository extends JpaRepository<AssetMaster, Long>, JpaSpecificationExecutor<AssetMaster> {

	public Long countByTenantIdAndIsDeletedFalseAndAssetStatus(Long tenantId, Long assetStateSysparam);

	public List<AssetIdDto> findAllByTenantIdAndAssetIdInAndIsDeletedFalseAndAssetStatus(Long tenantId,
			List<Long> assetIdList, Long assetStateSysparam);

}
