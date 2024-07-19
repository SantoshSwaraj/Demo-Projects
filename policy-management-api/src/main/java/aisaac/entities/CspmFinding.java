package aisaac.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

import aisaac.util.AppConstants;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cspm_finding")
public class CspmFinding {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id",insertable = false, updatable = false)
	private BigInteger recId;

	@Column(name = "security_control_id")
	private String securityControlId;

	@Column(name = "cloud_account_id")
	private String cloudAccountId;
	
	@Column(name = "tenant_id")
	private Integer tenantId;
	
	@Column(name="raw_event")
	private String rawEvent;
	
	@Column(name="source_name")
	private String sourceName;
	
	@Column(name="aisaac_normalized_severity")
	private Integer aisaacNormalizedSeverity;
	
	@Column(name="external_id")
	private String externalId;
	
	@Column(name="receipt_time")
	private Date receiptTime;
	
	@Column(name="source_product")
	private String sourceProduct;
	
	@Column(name="classification")
	private String classification;
	
	@Column(name="description")
	private String description;
	
	@Column(name="compliance_status")
	private String complianceStatus;
	
	@Column(name="compliance_clauses")
	private String complianceClauses;
	
	@Column(name="compliance_standards")
	private String complianceStandards;
	
	@Column(name="status_reasons_reason_code")
	private String statusReasonsReasonCode;
	
	@Column(name="status_reasons_description")
	private String statusReasonsDescription;
	
	@Column(name="product_name")
	private String productName;
	
	@Column(name="version")
	private String version;
	
	@Column(name="first_observed_at")
	private Date firstObservedAt;
	
	@Column(name="control_id")
	private String controlId;
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "record_state")
	private String recordState;

	@Column(name = "finding_title")
	private String finding;

	@Column(name = "finding_status")
	private String findingStatus;
	
	@Column(name = "remediation_status")
	private String remediationStatus;

	@Column(name = "last_observed_at")
	private Date lastObservedAt;
	
	@Column(name = "product_normalized_severity")
	private Integer productNormalizedSeverity;
	
	@Column(name = "product_severity")
	private String severity;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "processed_at")
	private LocalDateTime processedAt;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	@Column(name = "annotation")
	private String annotation;
	
	@Column(name = "evaluation_resource")
	private String evaluationResource;
	
	@Column(name = "evaluation_resource_type")
	private String evaluationResourceType;
	
	@Column(name = "cloud_service_region")
	private String cloudServiceRegion;
	
	@Column(name = "finding_id")
	private String findingId;
	
	@Column(name = "remediation_info")
	private String remediationInfo;
	
	@Column(name = "remediation_url")
	private String remediationUrl;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Column(name = "cloud_resource_id")
	private String cloudResourceId;
	
	@Column(name = "resource_type")
	private String resourceType;
	
	@Column(name = "resource_region")
	private String resourceRegion;
	
	@Column(name = "resource_details")
	private String remediationDetails;
	
	@Column(name = "cspm_str1")
	private String cspmStr1;
	
	@Column(name = "cspm_str2")
	private String cspmStr2;
	
	@Column(name = "cspm_str3")
	private String cspmStr3;
	
	@Column(name = "cspm_str4")
	private String cspmStr4;
	
	@Column(name = "cspm_str5")
	private String cspmStr5;
	
	@Column(name = "cspm_str6")
	private String cspmStr6;
	
	@Column(name = "cspm_str7")
	private String cspmStr7;
	
	@Column(name = "cspm_str8")
	private String cspmStr8;
	
	@Column(name = "cspm_str9")
	private String cspmStr9;
	
	@Column(name = "cspm_str10")
	private String cspmStr10;
	
	@Column(name = "cspm_str11")
	private String cspmStr11;
	
	@Column(name = "cspm_str12")
	private String cspmStr12;
	
	@Column(name = "cspm_str13")
	private String cspmStr13;
	
	@Column(name = "cspm_str14")
	private String cspmStr14;
	
	@Column(name = "cspm_str15")
	private String cspmStr15;
	
	@Column(name = "cspm_str1_name")
	private String cspmStr1Name;
	
	@Column(name = "cspm_str2_name")
	private String cspmStr2Name;
	
	@Column(name = "cspm_str3_name")
	private String cspmStr3Name;
	
	@Column(name = "cspm_str4_name")
	private String cspmStr4Name;
	
	@Column(name = "cspm_str5_name")
	private String cspmStr5Name;
	
	@Column(name = "cspm_str6_name")
	private String cspmStr6Name;
	
	@Column(name = "cspm_str7_name")
	private String cspmStr7Name;
	
	@Column(name = "cspm_str8_name")
	private String cspmStr8Name;
	
	@Column(name = "cspm_str9_name")
	private String cspmStr9Name;
	
	@Column(name = "cspm_str10_name")
	private String cspmStr10Name;
	
	@Column(name = "cspm_str11_name")
	private String cspmStr11Name;
	
	@Column(name = "cspm_str12_name")
	private String cspmStr12Name;
	
	@Column(name = "cspm_str13_name")
	private String cspmStr13Name;
	
	@Column(name = "cspm_str14_name")
	private String cspmStr14Name;
	
	@Column(name = "cspm_str15_name")
	private String cspmStr15Name;
	
	@Transient
	private String tenantName;

	@Transient
	private String remediationPlaybookName;
	
	@Transient
	private Long count;
	
}
