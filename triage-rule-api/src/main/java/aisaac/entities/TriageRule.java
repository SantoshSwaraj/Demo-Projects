package aisaac.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "rule")
public class TriageRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rule_id")
	private Long ruleId;

	@Column(name = "tenant_id", nullable = false)
	private Long tenantId;

	@Column(name = "rule_name", nullable = false)
	private String ruleName;

	@Column(name = "rule_type_id", nullable = false)
	private Long ruleTypeId;

	@Column(name = "rule_priority")
	private Integer rulePriority;

	@Column(name = "rule_state_sysparam", nullable = false)
	private Integer ruleStateSysparam;

	@Column(name = "expiry_date")
	private Date expiryDate;

	@Column(name = "rule_condition", columnDefinition = "text")
	private String ruleCondition;

	@Column(name = "created_by_name")
	private String createdBy;

	@Column(name = "created_by_id")
	private Long createdById;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_by_id")
	private Long updatedById;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "invstgn_category_sysparam")
	private Integer invstgnCategorySysparam;

	@Column(name = "ticket_assignee")
	private Long ticketAssignee;

	@Column(name = "ticket_category_sysparam")
	private Integer ticketCategorySysparam;

	@Column(name = "ticket_priority_sysparam")
	private Integer ticketPrioritySysparam;

	@Column(name = "rule_description", length = 4500)
	private String ruleDescription;

	@Column(name = "auto_append_applied")
	private Integer autoAppendApplied;

	@Column(name = "threshold_time")
	private Integer thresholdTime;

	@Column(name = "rule_string", columnDefinition = "text")
	private String ruleString;

	@Column(name = "is_base_event_enable")
	private Boolean isBaseEventEnable;

	@Column(name = "ticket_description", length = 1000)
	private String ticketDescription;

	@Column(name = "rule_compiled")
	private String ruleCompiled;

	@Column(name = "compiler_service_on")
	private String compilerServiceOn;

	@Column(name = "threshold_count")
	private Integer thresholdCount;

	@Column(name = "expire_updated_time")
	private Date expireUpdatedTime;

}
