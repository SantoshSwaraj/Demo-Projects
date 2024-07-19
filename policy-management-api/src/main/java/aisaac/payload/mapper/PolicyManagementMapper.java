package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.dto.PolicyManagementDTO;
import aisaac.entities.CspmFinding;
import aisaac.payload.response.PolicyManagementResponse;
import aisaac.util.AppConstants;
import aisaac.util.DateUtils;
import aisaac.util.PolicyManagementUtils;

public class PolicyManagementMapper {

	private List<CspmFinding> data=new ArrayList<>();
	
	public PolicyManagementMapper(List<CspmFinding> in) {
		this.data=in;
	}
	
	public List<Object> map(){
		return data.stream().map(o-> new PolicyManagementResponse(
				o.getTenantName(),
				o.getRecId(),
				o.getSecurityControlId(),
				o.getCloudAccountId(),
				o.getTenantId(),
				o.getRawEvent(),
				o.getSourceName(),
				o.getAisaacNormalizedSeverity(),
				o.getExternalId(),
				o.getReceiptTime(),
				o.getSourceProduct(),
				o.getClassification(),
				o.getDescription(),
				o.getComplianceStatus(),
				o.getComplianceClauses(),
				o.getComplianceStandards(),
				o.getStatusReasonsReasonCode(),
				o.getStatusReasonsDescription(),
				o.getProductName(),
				o.getVersion(),
				o.getFirstObservedAt(),
				o.getControlId(),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getCreatedAt()),
				o.getRecordState(),
				o.getFinding(),
				o.getFindingStatus(),
				o.getLastObservedAt(),
				o.getProductNormalizedSeverity(),
				o.getSeverity(),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getUpdatedAt()),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getProcessedAt()),
				o.getVendorName(),
				o.getAnnotation(),
				o.getEvaluationResource(),
				o.getEvaluationResourceType(),
				o.getCloudServiceRegion(),
				o.getFindingId(),
				o.getRemediationInfo(),
				o.getRemediationUrl(),
				PolicyManagementUtils.getLocalDateTimeInMilliSec(o.getCreatedDate()),
				o.getCloudResourceId(),
				o.getResourceType(),
				o.getResourceRegion(),
				o.getRemediationDetails(),
				o.getRemediationPlaybookName(),
				o.getCspmStr1(),
				o.getCspmStr2(),
				o.getCspmStr3(),
				o.getCspmStr4(),
				o.getCspmStr5(),
				o.getCspmStr6(),
				o.getCspmStr7(),
				o.getCspmStr8(),
				o.getCspmStr9(),
				o.getCspmStr10(),
				o.getCspmStr11(),
				o.getCspmStr12(),
				o.getCspmStr13(),
				o.getCspmStr14(),
				o.getCspmStr15(),
				o.getCspmStr1Name(),
				o.getCspmStr2Name(),
				o.getCspmStr3Name(),
				o.getCspmStr4Name(),
				o.getCspmStr5Name(),
				o.getCspmStr6Name(),
				o.getCspmStr7Name(),
				o.getCspmStr8Name(),
				o.getCspmStr9Name(),
				o.getCspmStr10Name(),
				o.getCspmStr11Name(),
				o.getCspmStr12Name(),
				o.getCspmStr13Name(),
				o.getCspmStr14Name(),
				o.getCspmStr15Name(),
				o.getRemediationStatus(),
				o.getCount()
				)).collect(Collectors.toList());
	}
	
	public static List<String[]> export(List<PolicyManagementDTO> resultList) {
		List<String[]> result = new ArrayList<>();
		result.add(AppConstants.POLICY_MANAGEMENT_EXPORT_COLUMNS
				.toArray(new String[AppConstants.POLICY_MANAGEMENT_EXPORT_COLUMNS.size()]));
		
		resultList.forEach(obj -> {
			result.add(new String[] { 
					StringUtils.wrap(obj.getCspmFinding().getTenantName(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getFinding()) ? obj.getCspmFinding().getFinding() : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getCloudResourceId()) ? obj.getCspmFinding().getCloudResourceId() : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getSecurityControlId()) ? obj.getCspmFinding().getSecurityControlId() : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getCloudAccountId())? String.valueOf(obj.getCspmFinding().getCloudAccountId())
							: StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCspmFinding().getSeverity(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getCspmFinding().getUpdatedAt()),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getCspmFinding().getCreatedAt()),
					StringUtils.wrap(obj.getCspmFinding().getComplianceStatus(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCspmFinding().getFindingStatus(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCspmFinding().getStatusReasonsDescription(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getComplianceStandards()) ? obj.getCspmFinding().getComplianceStandards().replace("\"", "") : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getCspmFinding().getCreatedDate()),					
					StringUtils.wrap(obj.getCspmFinding().getRemediationStatus(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					
		      });
		});
		return result;
	}
	
	public static List<String[]> exportForSingleTenant(List<PolicyManagementDTO> resultList) {
		List<String[]> result = new ArrayList<>();
		result.add(AppConstants.POLICY_MANAGEMENT_EXPORT_COLUMNS_SINGLE_USER
				.toArray(new String[AppConstants.POLICY_MANAGEMENT_EXPORT_COLUMNS_SINGLE_USER.size()]));
		
		resultList.forEach(obj -> {
			result.add(new String[] { 
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getFinding()) ? obj.getCspmFinding().getFinding() : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getCloudResourceId()) ? obj.getCspmFinding().getCloudResourceId() : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getSecurityControlId()) ? obj.getCspmFinding().getSecurityControlId() : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getCloudAccountId())? String.valueOf(obj.getCspmFinding().getCloudAccountId())
							: StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCspmFinding().getSeverity(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getCspmFinding().getUpdatedAt()),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getCspmFinding().getCreatedAt()),
					StringUtils.wrap(obj.getCspmFinding().getComplianceStatus(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCspmFinding().getFindingStatus(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCspmFinding().getStatusReasonsDescription(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getCspmFinding().getComplianceStandards()) ? obj.getCspmFinding().getComplianceStandards().replace("\"", "") : StringUtils.EMPTY, AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getCspmFinding().getCreatedDate()),					
					StringUtils.wrap(obj.getCspmFinding().getRemediationStatus(), AppConstants.POLICY_MANAGEMENT_FILENAME_PREFIX),
					
		      });
		});
		return result;
	}
}
