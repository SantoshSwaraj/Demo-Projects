package aisaac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TriageRuleListingResponseDto {
	private Long ruleId;
	private String ruleName;
	private String organization;
	private String ruleAction;
	private String condition;
	private String status;
	private Integer statusId;
	private String ruleCompiled;
	private String compilerServiceOn;

	private Long createdBy;
	private Long updatedBy;
	private String createdByStr;
	private String updatedByStr;
	private String createdDtFormtDt;
	private String updatedDtFormtDt;
	private String ruleExpiryDateStr;

	private Long createdDateEpoch;
	private Long updatedDateEpoch;
	private Long ruleExpiryEpoch;



}
