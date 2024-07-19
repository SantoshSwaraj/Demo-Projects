package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long>{

	List<Tenant> findAll();
}
