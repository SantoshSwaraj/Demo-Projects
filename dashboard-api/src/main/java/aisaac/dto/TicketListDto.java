package aisaac.dto;

import java.time.LocalDateTime;

public interface TicketListDto {
	LocalDateTime getCreatedDate();
	LocalDateTime getReOpenedDate();
	Long getTicketId();
	String getTicketName();
	Long getAssigneeUserId();
	Long getPriority();
	Long getCategory();
	Long getThreatCount();
	LocalDateTime getUpdatedDate();
}
