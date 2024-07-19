package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.dto.LogStopageMasterAssetDto;
import aisaac.entities.LogStopageMaster;

public interface LogStopageMasterRepository
		extends JpaRepository<LogStopageMaster, Long>, JpaSpecificationExecutor<LogStopageMaster> {

	List<LogStopageMasterAssetDto> findAllByTenantIdAndDeletedFalseAndDisabledFalseAndSuppressedFalse(Long tenantId);

}
