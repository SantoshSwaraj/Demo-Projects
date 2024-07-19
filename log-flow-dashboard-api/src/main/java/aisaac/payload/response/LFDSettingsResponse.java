package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LFDSettingsResponse {
	
	private Integer exportLimit;
	private Object custom;
	private Object defaults;
	private Object assetType;
	private Object severity;
	private Object monitorStatus;
	private Object deviceMasterList;
	
}
