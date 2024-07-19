package aisaac.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LmDruidFieldsDTO {
	private String id;
	private String label;
	private String type;
	private String input;
	private String operators[];
	private Integer fieldId;
	private String field;
	private String datasourceType;
	private String dataType;
	private String displayName;
	private String threatColumnName;
	private Integer displaySequenceNumber;
	private Boolean isDeleted;
	private Boolean allowSummaryStats;
	private Date createdDate;
	private Date updatedDate;
	private boolean isChecked;
}
