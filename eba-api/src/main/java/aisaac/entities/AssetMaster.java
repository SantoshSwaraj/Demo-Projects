package aisaac.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.util.CustomDateTimeSerializer;
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
@Table(name = "asset_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AssetMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "asset_id", nullable = false)
	private Long assetId;

	@Column(name = "tenant_id", nullable = false)
	private Long tenantId;

	
	@Column(name = "asset_name")
	private String assetName;
	
	@Column(name = "is_mdr_agent_live")
	private Boolean mdrAgentStatus;
	
	@Column(name = "created_by")
	private Long createdBy;
	
	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(name = "asset_type_id")
	private Integer assetType;

	@Column(name = "asset_entry_method_id")
	private Integer assetEntryMethod;
	
	@Column(name = "ip_address")
	private String ipAddress;
	
	@Column(name = "ip_address_list")
	private String ipAddressList;

	@Column(name = "vm_id")
	private String vmID;
	
	@Column(name = "cloud_resource_id")
	private String cloudResourceID;
	
	@Column(name = "cloud_resource_name")
	private String cloudResourceName;


	@Column(name = "device_type")
	private String deviceType;

	@Column(name = "device_vendor")
	
	
	private String deviceVendor;

	@Column(name = "device_product")
	
	
	private String deviceProduct;
	
	@Column(name = "product_version")
	
	
	private String productVersion;
	
	
	@Column(name = "final_ri_score")
	private Float getFinalRiScore;

	@Column(name = "criticality")
	private Integer criticality;
	
	@Column(name = "product_id")
	private Long productId;

	@JsonSerialize(using=CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@JsonSerialize(using=CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	@Column(name = "asset_state_sysparam")
	private Integer assetStatus;

	@Column(name = "hostname")
	private String hostName;
	
	@Column(name = "hostname_list")
	private String hostnameList;

	@Column(name = "mac_address")
	private String macAddress;

	@Column(name = "asset_score")
	private Float assetScore;

	@Column(name = "external_id ")
	private String externalId ;

	@Column(name = "external_created_date")
	private String externalCreatedDate ;

	@Column(name = "external_updated_date  ")
	private String externalUpdatedDate ;
	
	
	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
	
	@JsonSerialize(using=CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_first_received_date", length = 19)
	private LocalDateTime logFirstReceivedDate;
	
	@JsonSerialize(using=CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_last_received_date", length = 19)
	private LocalDateTime logLastReceivedDate;
	
	@Column(name = "log_monitoring_status")
	private Boolean logMonitoringStatus;
	
	@Column(name = "product_type_id")
	private Integer productTypeId;
}
