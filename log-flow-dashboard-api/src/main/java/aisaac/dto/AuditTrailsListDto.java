package aisaac.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditTrailsListDto {

	private AuditTrailNewOldValueDto customerName;
	private AuditTrailNewOldValueDto deviceVendor;
	private AuditTrailNewOldValueDto deviceProduct;
	
}
