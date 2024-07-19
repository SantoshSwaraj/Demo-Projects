package aisaac.payload.request;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.util.CustomDateTimeSerializer;
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
public class ExposuresDetailsRequest extends DataTableRequest {
	
	private String token;
	
	private String searchResult;
	private DataTableRequest datatableInfo;
	private Integer searchType = 0;
	
	private String fileType;
	private Boolean isMultiTenantUser;
	
	private String organization;
	private String title;
	private Integer confidence;
	private String issueType;
	private String mitreTechnique;
	private Integer iPAddress;
	private String summary;
	
	private List<String> searchOrganizationName;
	private String searchTitle;
	private Integer searchConfidence;
	private String searchIssueType;
	private String searchMitreTechnique;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchReportedBetweenFrom;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchReportedBetweenTo;
	
	private List<String> searchSeverityList;
	
	private Integer searchIPAddress;
	
	private String SearchSummary;
	
	private List<Integer> orgIds;
	
	private Object currentSearchFilters;

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
