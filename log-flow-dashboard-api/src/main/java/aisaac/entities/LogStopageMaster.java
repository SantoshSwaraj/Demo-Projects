package aisaac.entities;

import java.time.LocalDateTime;

import aisaac.util.AppConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "log_stopage_master")
public class LogStopageMaster implements Cloneable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id")
	private Long recId;

	@Column(name = "tenant_id")
	private Long tenantId;

	@Column(name = "product_id")
	private Integer productId;
	
	@Column(name = "device_vendor")
	private String productVendor;
	
	@Column(name = "collector_address")
	private String collectorAddress;
	
	@Column(name = "collector_host_name")
	private String collectorHostName;

	@Column(name = "device_product")
	private String productName;

	@Column(name = "device_group")
	private String productType;

	@Column(name = "device_address")
	private String productIP;

	@Column(name = "device_host_name")
	private String productHostName;

	@Column(name = "severity")
	private String severity;


	@Column(name = "tenant_name")
	private String tenantName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_receive_time")
	private LocalDateTime latestRecivedTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_event_received")
	private LocalDateTime lastEventReceived;

	@Column(name = "to_email_address")
	private String toEmailAddress;

	@Column(name = "cc_email_address")
	private String ccEmailAddress;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "email_time")
	private LocalDateTime emailTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "device_receipt_time")
	private LocalDateTime deviceReceiptTime;

	@Column(name = "disabled")
	private boolean disabled=AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE;

	@Column(name = "suppressed")
	private boolean suppressed=AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME;

	@Column(name = "created_by_id")
	private Integer createdBy;

	@Column(name = "updated_by_id")
	private Long updatedBy;

	@Column(name = "deleted")
	private boolean deleted=AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT;

	@Column(name = "logger_status")
	private Boolean loggerStatus;
	
	@Column(name = "log_status_time")
	private Integer logStopageThresholdTime;
	
	@Column(name = "device_status")
	private Integer deviceStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "logger_date")
	private LocalDateTime loggerDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_stoppage_time")
	private LocalDateTime logStoppageTime;
	
	@Column(name = "send_to_email_alert_audit")
	private String sendToEmailAlertAudit;
	
	@Column(name = "send_mail")
	private boolean sendMail=AppConstants.MAIL_SEND_DEFALUT_VALUE;
	
	@Column(name = "email_status")
	private String emailStatus=AppConstants.EMAIL_STATUS_DEFALUT_VALUE;
	
	@Column(name = "email_threshold_reached")
	private String emailThresholdReached;

	@Column(name = "asset_type_id")
	private Integer assetType;
	
	@Column(name = "email_alert_frequency")
	private Integer emailAlertFrequency;

	@Column(name = "cloud_resource_id")
	private String cloudResourceID;

	@Column(name = "asset_entry_method_id")
	private Integer assetEntryMethod;

	@Column(name = "asset_id")
	private Long assetId;
	
	@Column(name = "mdr_scanner_code")
	private String mdrScannerCode;
	
	@Column(name = "log_Stoppage_threshold_json")
	private String logStoppageThresholdJson;
	
	@Column(name = "email_notification_frequency_json")
	private String emailNotificationFrequencyJson;
	
	@Column(name = "product_type_id")
	private Long productTypeId;
	
	@Transient
	private String note;
	
	@Override
	public LogStopageMaster clone() throws CloneNotSupportedException {
		return (LogStopageMaster) super.clone();
	}
}
