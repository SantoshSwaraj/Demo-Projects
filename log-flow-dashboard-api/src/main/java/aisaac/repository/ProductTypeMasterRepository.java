package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.ProductTypeMaster;

public interface ProductTypeMasterRepository extends JpaRepository<ProductTypeMaster, Long>{
	
	List<ProductTypeMaster> findAllByDeletedFalse();
	
	List<ProductTypeMaster> findAllByDeletedFalseAndProductTypeIdIn(List<Long> productTypeId);

}
