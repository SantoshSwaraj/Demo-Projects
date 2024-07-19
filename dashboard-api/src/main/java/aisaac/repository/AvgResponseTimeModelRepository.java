package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.AvgResponseTimeModel;

public interface AvgResponseTimeModelRepository
		extends JpaRepository<AvgResponseTimeModel, Long>, JpaSpecificationExecutor<AvgResponseTimeModel> {

}
