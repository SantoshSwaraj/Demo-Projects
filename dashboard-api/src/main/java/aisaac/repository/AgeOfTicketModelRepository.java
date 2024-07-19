package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.AgeOfTicketModel;

public interface AgeOfTicketModelRepository
		extends JpaRepository<AgeOfTicketModel, Long>, JpaSpecificationExecutor<AgeOfTicketModel> {

}
