package aisaac.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.util.CustomDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TriageRuleListingRequestDto extends DataTableRequest {
	private int searchType = 0;
	private List<Long> organization;
	private String ruleId;
	private String ruleName;
	private List<String> ruleAction;
	private String conditionInput;
	private String createdBy;
	private String updatedBy;
	private int status;
	private String ruleDescription;

	//	have to pass in the request in Epoch time format
	private Long fromDateEpoch;
	private Long toDateEpoch;
	private Long updatedFromDateEpoch;
	private Long updatedToDateEpoch;
	private Long expiryDateEpoch;

	//	No need to pass these in the request ,will assign these after the conversion
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date fromDate;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date toDate;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date updatedFromDate;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date updatedToDate;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date expiryDate;

}
