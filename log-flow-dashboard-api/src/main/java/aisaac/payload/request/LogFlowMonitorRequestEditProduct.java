package aisaac.payload.request;

import aisaac.dto.CustomTimeFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class LogFlowMonitorRequestEditProduct {

	@NotNull(message = "RecId Cannot be Null")
	private Long recId;

	@NotNull(message = "tenantId Cannot be null")
	private Long tenantId;

	@NotNull(message = "AssetType Cannot be Null")
	private Integer assetType;

	@NotBlank(message = "MonitorStatus Cannot be Blank")
	private String monitorStatus;

	private String productType;
	private String productHostName;
	private String productIP;
	private String cloudResourceID;

	@NotBlank(message = "Severity Cannot be Blank")
	private String severity;
	private String collectorIP;
	private String collectorHostName;
	private Integer logStopageThresholdTime;
	private Integer emailAlertFrequency;
	private boolean mailSend;
	private String toEmailAddress;
	private String ccEmailAddress;
	@NotNull(message = "ProductId cannot be null")
	private Integer productID;
	private String note;
	private Long userId;
	private String mdrScannerCode;

	@NotNull(message = "LogStoppageThreshold cannot be null")
	private CustomTimeFields logStoppageThreshold;
	
	@NotNull(message = "EmailNotificationFrequency cannot be null")
	private CustomTimeFields emailNotificationFrequency;

}
