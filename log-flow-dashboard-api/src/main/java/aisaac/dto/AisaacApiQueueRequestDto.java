package aisaac.dto;

import java.util.Set;

import lombok.Data;

@Data
public class AisaacApiQueueRequestDto {
	
	private String status;

	private Set<Long> productId;

	private Set<Long> productTypeId;

	private Set<Long> recId;

}
