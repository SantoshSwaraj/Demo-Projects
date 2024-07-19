package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import aisaac.entities.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long>{

	List<Tenant> findAll();
	
	@Query("SELECT e.tenantId FROM Tenant e WHERE e.tenantName LIKE %:searchOrganizationName% ")
	List<Integer> findAllByColumnName(List<String> searchOrganizationName);
	
	@Query("SELECT e.tenantId FROM Tenant e WHERE e.tenantName LIKE %:searchOrganizationName% ")
	List<Integer> findAllByTenantName(String searchOrganizationName);
}
