package aisaac.payload.response;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class PolicyManagementPrismaCloudResponse {
	private String tenantName;
	private BigInteger recId;
	private String securityControlId;
	private String cloudAccountId;
	private Integer tenantId;
	private String rawEvent;
	private String sourceName;
	private Integer aisaacNormalizedSeverity;
	private String externalId;
	private Date receiptTime;
	private String sourceProduct;
	private String classification;
	private String description;
	private String complianceStatus;
	private String complianceClauses;
	private String complianceStandards;
	private String statusReasonsReasonCode;
	private String statusReasonsDescription;
	private String productName;
	private String version;
	private Date firstObservedAt;
	private String controlId;
	private Long createdAt;
	private String recordState;
	private String finding;
	private String findingStatus;
	private Date lastObservedAt;
	private Integer productNormalizedSeverity;
	private String severity;
	private Long updatedAt;
	private Long processedAt;
	private String vendorName;
	private String annotation;
	private String evaluationResource;
	private String evaluationResourceType;
	private String awsRegion;
	private String findingId;
	private String remediationInfo;
	private String remediationUrl;
	private Long createdDate;
	private String cloudResourceId;
	private String resourceType;
	private String resourceRegion;
	private String resourceDetails;
	private String cspmStr1;
	private String cspmStr2;
	private String cspmStr3;
	private String cspmStr4;
	private String cspmStr5;
	private String cspmStr6;
	private String cspmStr7;
	private String cspmStr8;
	private String cspmStr9;
	private String cspmStr10;
	private String cspmStr11;
	private String cspmStr12;
	private String cspmStr13;
	private String cspmStr14;
	private String cspmStr15;
	private String cspmStr1Name;
	private String cspmStr2Name;
	private String cspmStr3Name;
	private String cspmStr4Name;
	private String cspmStr5Name;
	private String cspmStr6Name;
	private String cspmStr7Name;
	private String cspmStr8Name;
	private String cspmStr9Name;
	private String cspmStr10Name;
	private String cspmStr11Name;
	private String cspmStr12Name;
	private String cspmStr13Name;
	private String cspmStr14Name;
	private String cspmStr15Name;
	private String remediationStatus;
	private Long count;
}
