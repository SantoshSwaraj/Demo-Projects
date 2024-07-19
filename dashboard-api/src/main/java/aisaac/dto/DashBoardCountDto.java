package aisaac.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DashBoardCountDto {

	private LocalDateTime fromDate;
	private LocalDateTime toDate;
}
