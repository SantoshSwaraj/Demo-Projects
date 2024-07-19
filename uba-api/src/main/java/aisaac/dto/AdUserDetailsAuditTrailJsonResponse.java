package aisaac.dto;

import lombok.Data;

@Data
public class AdUserDetailsAuditTrailJsonResponse {
	
	private String type;
	
	private AdUserDetailAuditTrail data;
}
