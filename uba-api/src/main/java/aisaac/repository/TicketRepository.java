package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	public List<Ticket> findTop10ByTicketIdInAndUpdatedDateBetweenAndStatus(Set<Long> tickedIdList,
			LocalDateTime fromDate,		LocalDateTime toDate, Long status);
}
