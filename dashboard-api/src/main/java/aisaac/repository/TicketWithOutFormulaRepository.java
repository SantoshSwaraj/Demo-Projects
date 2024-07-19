package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.TicketWithOutFormula;

public interface TicketWithOutFormulaRepository
		extends JpaRepository<TicketWithOutFormula, Long>, JpaSpecificationExecutor<TicketWithOutFormula> {

}
