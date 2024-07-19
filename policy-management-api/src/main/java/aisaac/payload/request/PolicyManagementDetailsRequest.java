package aisaac.payload.request;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.Search;
import aisaac.util.CustomDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class PolicyManagementDetailsRequest extends DataTableRequest {

	private String token;
	
	private String searchResult;
	private DataTableRequest datatableInfo;
	private String cloudResourceId;
	private String securityControlId;
	private Integer tenantId;
	private Integer searchType = 0;
	private String finding;
	private String searchFinding;
	private String remPlaybookName;
	private String standards;
	private String fileType;
	private List<String> searchFindingStatus;
	
	private String searchRemediationPlaybookName;
	
	private List<String> searchSecurityControlIds;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchReportedBetweenFrom;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchReportedBetweenTo;
	
	private String searchResourceName;
	
	private String searchSecurityControlId;

	private String searchCloudAccountId;

	private Integer searchtenantId;
	
	private List<String> searchOrganizationName;
	
	private List<Integer> orgIds;

	private List<String> searchSeverityList;
	
	private List<String> searchComplianceStatus;

	private List<String> searchRemediationStatus;
	
	private Object currentSearchFilters;

	private String searchStandards;

	public boolean isDefaultSearch() {
		return this.searchType.equals(0);
	}

	public boolean isFullSearch() {
		return this.searchType.equals(1);
	}

	public boolean isSmartSearch() {
		return this.searchType.equals(2);
	}

	public boolean isAdvancedSearch() {
		return this.searchType.equals(3);
	}
}
