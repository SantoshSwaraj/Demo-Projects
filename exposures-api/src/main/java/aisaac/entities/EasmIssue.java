package aisaac.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="easm_issue")
public class EasmIssue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id",insertable = false, updatable = false)
	private BigInteger recId;
	
	@Column(name = "tenant_id")
	private Integer tenantId;
	
	@Column(name = "issue_id")
	private String issueId;
	
	@Column(name = "issue_status")
	private String issueStatus;
	
	@Column(name = "issue_category")
	private String issueCategory;
	
	@Column(name = "issue_subcategory")
	private String issueSubcategory;
	
	@Column(name = "issue_title")
	private String issueTitle;
	
	@Column(name = "resource_ip")
	private String resourceIp;
	
	@Column(name = "resource_id")
	private String resourceId;
	
	@Column(name = "resource_type")
	private String resourceType;
	
	@Column(name = "severity")
	private String severity;
	
	@Column(name = "vendor_name")
	private String vendorName;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "severity_score")
	private Integer severityScore;
	
	@Column(name = "confidence")
	private Integer confidence;
	
	@Column(name = "cve_id")
	private String cveId;
	
	@Column(name = "asset_status")
	private String assetStatus;
	
	@Column(name = "investigation_status")
	private String investigationStatus;
	
	@Column(name = "mitre_name")
	private String mitreName;
	
	@Column(name = "mitre_next_name")
	private String mitreNextName;
	
	@Column(name = "detection_complexity")
	private String detectionComplexity;
	
	@Column(name = "exploitation_method")
	private String exploitationMethod;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_detected_at")
	private LocalDateTime lastDetectedAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "first_detected_at")
	private LocalDateTime firstDetectedAt;
	 
	@Column(name="resolved_at")
	private Date resolvedAt;
	
	@Column(name="created_date") 
	private Date createdDate; 
	
	@Column(name = "issue_comment")
	private String issueComment;
	
	@Column(name = "raw_event")
	private String rawEvent;
	
	@Column(name = "reference")
	private String reference;
	
	@Column(name = "asset_owner_details")
	private String assetOwnerDetails;
	
	@Column(name = "potential_threat")
	private String potentialThreat;
	
	@Column(name = "asset_tags")
	private String assetTags;
	
	@Column(name = "summary")
	private String summary;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "region")
	private String region;
	
	@Column(name = "platform")
	private String platform;
	
	@Column(name = "organization")
	private String organization;
	
	@Column(name = "remediation_steps")
	private String remediationSteps;
	
	@Column(name = "potential_impact")
	private String potentialImpact;
	
	@Column(name = "evidence")
	private String evidence;
	
	@Column(name = "evidence_info")
	private String evidenceInfo;
	
	@Column(name = "evidence_final_url")
	private String evidenceFinalUrl;
	
	@Column(name = "evidence_reason")
	private String evidenceReason;
	
	@Column(name = "evidence_hostname")
	private String evidenceHostname;
	
	@Column(name = "evidence_request")
	private String evidenceRequest;
	
	@Column(name = "evidence_response")
	private String evidenceResponse;
	
	@Column(name = "evidence_url")
	private String evidenceUrl;
	
	@Column(name = "evidence_bindings")
	private String evidenceBindings;
	
	@Column(name = "easm_str1_name")
	private String easmStr1Name;
	
	@Column(name = "easm_str2_name")
	private String easmStr2Name;
	
	@Column(name = "easm_str3_name")
	private String easmStr3Name;
	
	@Column(name = "easm_str4_name")
	private String easmStr4Name;
	
	@Column(name = "easm_str5_name")
	private String easmStr5Name;
	
	@Column(name = "easm_str6_name")
	private String easmStr6Name;
	
	@Column(name = "easm_str7_name")
	private String easmStr7Name;
	
	@Column(name = "easm_str8_name")
	private String easmStr8Name;
	
	@Column(name = "easm_str9_name")
	private String easmStr9Name;
	
	@Column(name = "easm_str10_name")
	private String easmStr10Name;
	
	@Column(name = "easm_str11_name")
	private String easmStr11Name;
	
	@Column(name = "easm_str12_name")
	private String easmStr12Name;
	
	@Column(name = "easm_str13_name")
	private String easmStr13Name;
	
	@Column(name = "easm_str14_name")
	private String easmStr14Name;
	
	@Column(name = "easm_str15_name")
	private String easmStr15Name;
	
	@Column(name = "easm_str1")
	private String easmStr1;
	
	@Column(name = "easm_str2")
	private String easmStr2;
	
	@Column(name = "easm_str3")
	private String easmStr3;
	
	@Column(name = "easm_str4")
	private String easmStr4;
	
	@Column(name = "easm_str5")
	private String easmStr5;
	
	@Column(name = "easm_str6")
	private String easmStr6;
	
	@Column(name = "easm_str7")
	private String easmStr7;
	
	@Column(name = "easm_str8")
	private String easmStr8;
	
	@Column(name = "easm_str9")
	private String easmStr9;
	
	@Column(name = "easm_str10")
	private String easmStr10;
	
	@Column(name = "easm_str11")
	private String easmStr11;
	
	@Column(name = "easm_str12")
	private String easmStr12;
	
	@Column(name = "easm_str13")
	private String easmStr13;
	
	@Column(name = "easm_str14")
	private String easmStr14;
	
	@Column(name = "easm_str15")
	private String easmStr15;
	
	@Transient
	private String tenantName;
	
}
