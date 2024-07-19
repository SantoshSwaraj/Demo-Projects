package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.dto.TicketListDto;
import aisaac.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

	List<TicketListDto> findFirst10ByCreatedDateBetweenOrReOpenedDateBetweenAndTenantIdAndStatusAndPriorityNotOrderByCreatedDateDescReOpenedDateDescPriorityAsc(
			LocalDateTime fromDate, LocalDateTime toDate, LocalDateTime reopenedFromDate, LocalDateTime reopenedToDate,
			Long tenantId, Integer status, Integer priority);
}
