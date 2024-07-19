package aisaac.payload.request;

import org.apache.commons.lang3.StringUtils;

import aisaac.dto.CustomTimeFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class LogFlowMonitorRequestAddProduct {

	private Long recId;
	
	@NotNull(message = "tenantId Cannot be Blank")
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
	private boolean sendMail;
	private String toEmailAddress;
	private String ccEmailAddress;
	private String note;
	@NotNull(message = "productId cannot be null")
	private Integer productID;
	private Integer userId;
	private String mdrScannerCode;

	@NotNull(message = "LogStoppageThreshold cannot be null")
	private CustomTimeFields logStoppageThreshold;

	@NotNull(message = "EmailNotificationFrequency cannot be null")
	private CustomTimeFields emailNotificationFrequency;

	public void setProductIP(String productIP) {
		if (StringUtils.isBlank(productIP)) {
			productIP = "NA";
		}
		this.productIP = productIP;
	}

	public void setProductHostName(String productHostName) {
		if (StringUtils.isBlank(productHostName)) {
			productHostName = "NA";
		}
		this.productHostName = productHostName;
	}
}
