package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.ProductMaster;

public interface ProductMasterRepository extends JpaRepository<ProductMaster, Long> {

	List<ProductMaster> findAllByDeletedFalse();

}
