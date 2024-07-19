package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.dto.PolicyManagementAzureDTO;
import aisaac.entities.CspmFinding;
import aisaac.payload.response.PolicyManagementAzureResponse;
import aisaac.util.PolicyManagementUtils;

public class PolicyManagementAzureMapperList {

	private List<PolicyManagementAzureDTO> data=new ArrayList<>();
	
	public PolicyManagementAzureMapperList(List<PolicyManagementAzureDTO> pml) {
		this.data=pml;
	}
	
	public List<Object> map(){
		return data.stream().map(o-> new PolicyManagementAzureResponse(
				o.getCspmFinding().getTenantName(),
				o.getCspmFinding().getRecId(),
				o.getCspmFinding().getSecurityControlId(),
				o.getCspmFinding().getCloudAccountId(),
				o.getCspmFinding().getTenantId(),
				o.getCspmFinding().getRawEvent(),
				o.getCspmFinding().getSourceName(),
				o.getCspmFinding().getAisaacNormalizedSeverity(),
				o.getCspmFinding().getExternalId(),
				o.getCspmFinding().getReceiptTime(),
				o.getCspmFinding().getSourceProduct(),
				o.getCspmFinding().getClassification(),
				o.getCspmFinding().getDescription(),
				o.getCspmFinding().getComplianceStatus(),
				o.getCspmFinding().getComplianceClauses(),
				o.getCspmFinding().getComplianceStandards(),
				o.getCspmFinding().getStatusReasonsReasonCode(),
				o.getCspmFinding().getStatusReasonsDescription(),
				o.getCspmFinding().getProductName(),
				o.getCspmFinding().getVersion(),
				o.getCspmFinding().getFirstObservedAt(),
				o.getCspmFinding().getControlId(),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getCspmFinding().getCreatedAt()),
				o.getCspmFinding().getRecordState(),
				o.getCspmFinding().getFinding(),
				o.getCspmFinding().getFindingStatus(),
				o.getCspmFinding().getLastObservedAt(),
				o.getCspmFinding().getProductNormalizedSeverity(),
				o.getCspmFinding().getSeverity(),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getCspmFinding().getUpdatedAt()),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getCspmFinding().getProcessedAt()),
				o.getCspmFinding().getVendorName(),
				o.getCspmFinding().getAnnotation(),
				o.getCspmFinding().getEvaluationResource(),
				o.getCspmFinding().getEvaluationResourceType(),
				o.getCspmFinding().getCloudServiceRegion(),
				o.getCspmFinding().getFindingId(),
				o.getCspmFinding().getRemediationInfo(),
				o.getCspmFinding().getRemediationUrl(),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getCspmFinding().getCreatedDate()),
				o.getCspmFinding().getCloudResourceId(),
				o.getCspmFinding().getResourceType(),
				o.getCspmFinding().getResourceRegion(),
				o.getCspmFinding().getRemediationDetails(),
				o.getCspmFinding().getCspmStr1(),
				o.getCspmFinding().getCspmStr2(),
				o.getCspmFinding().getCspmStr3(),
				o.getCspmFinding().getCspmStr4(),
				o.getCspmFinding().getCspmStr5(),
				o.getCspmFinding().getCspmStr6(),
				o.getCspmFinding().getCspmStr7(),
				o.getCspmFinding().getCspmStr8(),
				o.getCspmFinding().getCspmStr9(),
				o.getCspmFinding().getCspmStr10(),
				o.getCspmFinding().getCspmStr11(),
				o.getCspmFinding().getCspmStr12(),
				o.getCspmFinding().getCspmStr13(),
				o.getCspmFinding().getCspmStr14(),
				o.getCspmFinding().getCspmStr15(),
				o.getCspmFinding().getCspmStr1Name(),
				o.getCspmFinding().getCspmStr2Name(),
				o.getCspmFinding().getCspmStr3Name(),
				o.getCspmFinding().getCspmStr4Name(),
				o.getCspmFinding().getCspmStr5Name(),
				o.getCspmFinding().getCspmStr6Name(),
				o.getCspmFinding().getCspmStr7Name(),
				o.getCspmFinding().getCspmStr8Name(),
				o.getCspmFinding().getCspmStr9Name(),
				o.getCspmFinding().getCspmStr10Name(),
				o.getCspmFinding().getCspmStr11Name(),
				o.getCspmFinding().getCspmStr12Name(),
				o.getCspmFinding().getCspmStr13Name(),
				o.getCspmFinding().getCspmStr14Name(),
				o.getCspmFinding().getCspmStr15Name(),
				o.getCspmFinding().getRemediationStatus(),
				o.getCount()
				)).collect(Collectors.toList());
	}
}
