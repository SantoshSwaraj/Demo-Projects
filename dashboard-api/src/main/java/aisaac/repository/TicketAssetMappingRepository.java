package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.TicketAssetMapping;

public interface TicketAssetMappingRepository extends JpaRepository<TicketAssetMapping, Long>,JpaSpecificationExecutor<TicketAssetMapping>{

}
