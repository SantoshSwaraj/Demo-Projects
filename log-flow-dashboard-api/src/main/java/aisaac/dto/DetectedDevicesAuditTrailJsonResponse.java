package aisaac.dto;

import lombok.Data;

@Data
public class DetectedDevicesAuditTrailJsonResponse {

	private String type;

	private DetectedDevicesAuditTrailDto data;
}
