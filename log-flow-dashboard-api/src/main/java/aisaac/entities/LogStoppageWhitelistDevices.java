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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "log_stopage_whitelist_devices")
public class LogStoppageWhitelistDevices implements Cloneable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id")
	private Long recId;

	@Column(name = "device_vendor")
	private String productVendor;

	@Column(name = "device_product")
	private String productName;

	@Column(name = "tenant_id")
	private Long tenantId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime allowListedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "deleted")
	private boolean deleted = AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "created_by_id")
	private Long createdById;

	@Column(name = "updated_by_id")
	private Long updatedById;

	
	@Column(name = "description")
	private String description;

	@Transient
	private String tenantName;
	
	@Override
	public LogStoppageWhitelistDevices clone() throws CloneNotSupportedException {
		return (LogStoppageWhitelistDevices) super.clone();
	}
}
