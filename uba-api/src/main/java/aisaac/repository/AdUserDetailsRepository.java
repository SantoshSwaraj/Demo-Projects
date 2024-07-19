package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.AdUserDetail;

public interface AdUserDetailsRepository extends JpaRepository<AdUserDetail, Long>{

	public List<AdUserDetail> findAllByTenantIdAndDepartmentNameIsNotNull(Long tenatId);
	
	public Optional<AdUserDetail> findByRecIdAndTenantId(Integer recId,Long tenantId);
}
