package aisaac.payload.response;

import java.util.List;

import aisaac.dto.CustomTimeFields;
import aisaac.dto.NotesFormatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogStopageMasterGetEditableFieldsResponse {

	private Long recId;
	private Long tenantId;
	private String tenantName;
	private Integer assetType;
	private String monitorStatus;
	private String productType;
	private String productHostName;
	private String productIP;
	private String cloudResourceID;
	private String severity;
	private String collectorIP;
	private String collectorHostName;
	private Integer logStopageThresholdTime;
	private Integer emailAlertFrequency;
	private boolean mailSend;
	private String toEmailAddress;
	private String ccEmailAddress;
	private Integer productID;
	private List<NotesFormatDto> note;
	private String mdrScannerCode;
	private CustomTimeFields logStoppageThreshold;
	private CustomTimeFields emailNotificationFrequency;

}
