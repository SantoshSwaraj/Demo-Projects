package aisaac.payload.response;

import java.util.List;

import aisaac.dto.CustomTimeFields;
import aisaac.dto.NotesFormatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LFMResponse {

	private String tenantName;
	private String collectorAddress;
	private String collectorHostName;
	private String productVendor;
	private String productName;
	private String productType;
	private String productIP;
	private String productHostName;
	private String assetType;
	private String cloudResourceID;
	private String severity;
	private Integer logStoppageThresholdTime;
	private Integer emailNotificationFrequencyTime;
	private Integer deviceStatus;
	private Long loggerDate;
	private Long lastEventReceived;
	private String monitorStatus;
	private Long createdDate;
	private Long updatedDate;
	private String toEmailAddress;
	private String ccEmailAddress;
	private List<NotesFormatDto> note;
	private Long recId;
	private Integer productId;
	private String mdrScannerCode;
	private CustomTimeFields logStoppageThresholdJson;
	private CustomTimeFields emailNotificationFrequencyJson;
	private Integer assetTypeId;
	private Boolean disabled;
	private Boolean suppressed;
	private Long assetId;
	
}
