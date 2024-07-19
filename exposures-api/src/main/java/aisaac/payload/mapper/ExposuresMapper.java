package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.entities.EasmIssue;
import aisaac.payload.response.ExposuresResponse;
import aisaac.util.AppConstants;
import aisaac.util.DateUtils;
import aisaac.util.ExposuresUtils;

public class ExposuresMapper {
	
private List<EasmIssue> data=new ArrayList<>();
	
	public ExposuresMapper(List<EasmIssue> in) {
		this.data=in;
	}
	
	public List<Object> map(){
		return data.stream().map(o-> new ExposuresResponse(
				o.getRecId(),
				o.getTenantId(),
				o.getIssueId(),
				o.getIssueStatus(),
				o.getIssueCategory(),
				o.getIssueSubcategory(),
				o.getIssueTitle(),
				o.getResourceIp(),
				o.getResourceId(),
				o.getResourceType(),
				o.getSeverity(),
				o.getVendorName(),
				o.getProductName(),
				o.getSeverityScore(),
				o.getConfidence(),
				o.getCveId(),
				o.getAssetStatus(),
				o.getInvestigationStatus(),
				o.getMitreName(),
				o.getMitreNextName(),
				o.getDetectionComplexity(),
				o.getExploitationMethod(),
				ExposuresUtils.getLocalDateTimeInMilliSec(o.getLastDetectedAt()),
				ExposuresUtils.getLocalDateTimeInMilliSec(o.getFirstDetectedAt()),
				o.getResolvedAt(),
				o.getCreatedDate(),
				o.getIssueComment(),
				o.getRawEvent(),
				o.getReference(),
				o.getAssetOwnerDetails(),
				o.getPotentialThreat(),
				o.getAssetTags(),
				o.getSummary(),
				o.getLocation(),
				o.getRegion(),
				o.getPlatform(),
				o.getOrganization(),
				o.getRemediationSteps(),
				o.getPotentialImpact(),
				o.getEvidence(),
				o.getEvidenceInfo(),
				o.getEvidenceFinalUrl(),
				o.getEvidenceReason(),
				o.getEvidenceHostname(),
				o.getEvidenceRequest(),
				o.getEvidenceResponse(),
				o.getEvidenceUrl(),
				o.getEvidenceBindings(),
				o.getEasmStr1Name(),
				o.getEasmStr2Name(),
				o.getEasmStr3Name(),
				o.getEasmStr4Name(),
				o.getEasmStr5Name(),
				o.getEasmStr6Name(),
				o.getEasmStr7Name(),
				o.getEasmStr8Name(),
				o.getEasmStr9Name(),
				o.getEasmStr10Name(),
				o.getEasmStr11Name(),
				o.getEasmStr12Name(),
				o.getEasmStr13Name(),
				o.getEasmStr14Name(),
				o.getEasmStr15Name(),
				o.getEasmStr1(),
				o.getEasmStr2(),
				o.getEasmStr3(),
				o.getEasmStr4(),
				o.getEasmStr5(),
				o.getEasmStr6(),
				o.getEasmStr7(),
				o.getEasmStr8(),
				o.getEasmStr9(),
				o.getEasmStr10(),
				o.getEasmStr11(),
				o.getEasmStr12(),
				o.getEasmStr13(),
				o.getEasmStr14(),
				o.getEasmStr15(),
				o.getTenantName()			    
				)).collect(Collectors.toList());
	}
	
	public static List<String[]> export(List<EasmIssue> data) {
		List<String[]> result = new ArrayList<>();
		result.add(AppConstants.EXPOSURES_EXPORT_COLUMNS
				.toArray(new String[AppConstants.EXPOSURES_EXPORT_COLUMNS.size()]));
		
		data.forEach(obj -> {
			result.add(new String[] { 
					StringUtils.wrap(obj.getTenantName(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getIssueTitle(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getConfidence()) ? String.valueOf(obj.getConfidence())
							: StringUtils.EMPTY, AppConstants.EXPOSURES_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getLastDetectedAt()),
					StringUtils.wrap(obj.getIssueCategory(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getIssueId(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getIssueSubcategory(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getMitreNextName(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getSeverity(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getSeverityScore()) ? String.valueOf(obj.getSeverityScore())
							: StringUtils.EMPTY, AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getResourceIp(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCveId(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getPotentialImpact(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getSummary(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getAssetOwnerDetails(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getLocation(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getFirstDetectedAt()),					
		      });
		});
		return result;
	}
	
	public static List<String[]> exportForSingleTenant(List<EasmIssue> data) {
		List<String[]> result = new ArrayList<>();
		result.add(AppConstants.EXPOSURES_EXPORT_COLUMNS_SINGLE_USER
				.toArray(new String[AppConstants.EXPOSURES_EXPORT_COLUMNS_SINGLE_USER.size()]));
		
		data.forEach(obj -> {
			result.add(new String[] { 
					StringUtils.wrap(obj.getTenantName(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getIssueTitle(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getConfidence()) ? String.valueOf(obj.getConfidence())
							: StringUtils.EMPTY, AppConstants.EXPOSURES_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getLastDetectedAt()),
					StringUtils.wrap(obj.getIssueCategory(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getIssueId(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getIssueSubcategory(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getMitreNextName(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getSeverity(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(Objects.nonNull(obj.getSeverityScore()) ? String.valueOf(obj.getSeverityScore())
							: StringUtils.EMPTY, AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getResourceIp(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getCveId(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getPotentialImpact(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getSummary(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getAssetOwnerDetails(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					StringUtils.wrap(obj.getLocation(), AppConstants.EXPOSURES_FILENAME_PREFIX),
					DateUtils.fetchLocalDateTimeInStringFormat(obj.getFirstDetectedAt()),
					
		      });
		});
		return result;
	}

}
