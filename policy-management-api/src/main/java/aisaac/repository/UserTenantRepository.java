package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import aisaac.entities.UserTenant;

public interface UserTenantRepository extends JpaRepository<UserTenant, Integer> {
    
    @Query(nativeQuery = true, value = 
            "SELECT * FROM user_tenant WHERE user_id = :userId AND tenant_id IN (SELECT tenant_id FROM tenant WHERE enabled = 1)")
	List<UserTenant> findByEnabledTenantsAndUserIdEquals(Integer userId);
}
