package aisaac.payload.response;

import java.math.BigInteger;
import java.util.Date;

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
public class ExposuresResponse {
	private BigInteger recId;
	private Integer tenantId;
	private String issueId;
	private String issueStatus;
	private String issueCategory;
	private String issueSubcategory;
	private String issueTitle;
	private String resourceIp;
	private String resourceId;
	private String resourceType;
	private String severity;
	private String vendorName;
	private String productName;
	private Integer severityScore;
	private Integer confidence;
	private String cveId;
	private String assetStatus;
	private String investigationStatus;
	private String mitreName;
	private String mitreNextName;
	private String detectionComplexity;
	private String exploitationMethod;
	private Long lastDetectedAt;
	private Long firstDetectedAt;
	private Date resolvedAt;
	private Date createdDate;
	private String issueComment;
	private String rawEvent;
	private String reference;
	private String assetOwnerDetails;
	private String potentialThreat;
	private String assetTags;
	private String summary;
	private String location;
	private String region;
	private String platform;
	private String organization;
	private String remediationSteps;
	private String potentialImpact;
	private String evidence;
	private String evidenceInfo;
	private String evidenceFinalUrl;
	private String evidenceReason;
	private String evidenceHostname;
	private String evidenceRequest;
	private String evidenceResponse;
	private String evidenceUrl;
	private String evidenceBindings;
	private String easmStr1Name;
	private String easmStr2Name;
	private String easmStr3Name;
	private String easmStr4Name;
	private String easmStr5Name;
	private String easmStr6Name;
	private String easmStr7Name;
	private String easmStr8Name;
	private String easmStr9Name;
	private String easmStr10Name;
	private String easmStr11Name;
	private String easmStr12Name;
	private String easmStr13Name;
	private String easmStr14Name;
	private String easmStr15Name;
	private String easmStr1;
	private String easmStr2;
	private String easmStr3;
	private String easmStr4;
	private String easmStr5;
	private String easmStr6;
	private String easmStr7;
	private String easmStr8;
	private String easmStr9;
	private String easmStr10;
	private String easmStr11;
	private String easmStr12;
	private String easmStr13;
	private String easmStr14;
	private String easmStr15;
	private String tenantName;

}
