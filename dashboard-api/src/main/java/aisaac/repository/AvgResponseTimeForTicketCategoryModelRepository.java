package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.AvgResponseTimeForTicketCategoryModel;

public interface AvgResponseTimeForTicketCategoryModelRepository
		extends JpaRepository<AvgResponseTimeForTicketCategoryModel, Long>,
		JpaSpecificationExecutor<AvgResponseTimeForTicketCategoryModel> {

}
