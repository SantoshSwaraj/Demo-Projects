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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "log_stopage_detected_devices_audit_trail")

public class LogStopageDetectedDevicesAuditTrail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id")
	private Long recId;
	
	@Column(name = "log_stopage_detected_devices_id")
	private Long logStopageDetectedDevicesId;
	
	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "device_address")
	private String productIP;

	@Column(name = "device_host_name")
	private String productHostName;

	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "device_vendor")
	private String productVendor;

	@Column(name = "device_product")
	private String productName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_event_received")
	private LocalDateTime lastEventReceived;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime detectedDate;

	@Column(name = "whitelist")
	private boolean whitelist = AppConstants.DETECTED_DEVICE_WHITELIST_DEFAULT_VALUE;

	@Column(name = "deleted")
	private boolean deleted = AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT;

	@Column(name = "cloud_resource_id")
	private String cloudResourceID;

	@Column(name = "created_by_id")
	private Long createdBy;

	@Column(name = "updated_by_id")
	private Long updatedBy;

}
