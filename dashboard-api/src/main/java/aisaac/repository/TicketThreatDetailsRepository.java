package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.TicketThreatDetails;

public interface TicketThreatDetailsRepository extends JpaRepository<TicketThreatDetails, Long>{
	
	public List<TicketThreatDetails> findAllByTicketId(Long ticketId);

}
