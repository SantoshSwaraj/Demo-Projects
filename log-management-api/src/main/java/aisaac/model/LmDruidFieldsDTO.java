package aisaac.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

/**
 * LmDruidFieldsDTO
 */
public class LmDruidFieldsDTO {
	
	@Transient
	@Setter
	@Getter
	private String id;
	
	@Transient
	@Setter
	@Getter
	private String label;
	
	 // @Column(name = "type", nullable = false)
	@Transient
	@Getter
	@Setter
	private String type;
	  
	//  @Column(name = "input", nullable = false)
	@Transient
	@Getter
	@Setter
	private String input;
	
	
    
	@Setter
	@Getter
	private String operators[];
    
	
	@Getter
	@Setter
	private Integer fieldId;
	
	@Getter
	@Setter
	private String field;
	
	@Getter
	@Setter
	private String datasourceType;
	
	@Getter
	@Setter
	private String dataType;
	
	@Getter
	@Setter
	private String displayName;
	
	@Getter
	@Setter
	private String threatColumnName;
	
	@Getter
	@Setter
	private Integer displaySequenceNumber;
	
	@Getter
	@Setter
	private Boolean isDeleted;
	
	@Getter
	@Setter
	private Boolean allowSummaryStats;
	
	@Getter
	@Setter
	private Date createdDate;
	
	@Getter
	@Setter
	private Date updatedDate;
	
	@Getter
	@Setter
	private boolean isChecked;
	
	public LmDruidFieldsDTO() {
		
	}
	
	public LmDruidFieldsDTO(LmDruidFields model) {
		this.fieldId = model.getFieldId();
		this.field = model.getField();
		this.dataType = model.getDataType();
		this.displayName = model.getDisplayName();
		this.threatColumnName = model.getThreatColumnName();
		this.displaySequenceNumber = model.getDisplaySequenceNumber();
		this.isDeleted = model.getIsDeleted();
		this.allowSummaryStats = model.getAllowSummaryStats();
		this.createdDate = model.getCreatedDate();
		this.updatedDate = model.getUpdatedDate();
	}

	@Override
	public String toString() {
		return "LmDruidFieldsDTO [fieldId=" + fieldId + ", id=" + id + ", field=" + field + ", datasourceType=" + datasourceType
				+ ", dataType=" + dataType + ", displayName=" + displayName + ", threatColumnName=" + threatColumnName
				+ ", displaySequenceNumber=" + displaySequenceNumber + ", isDeleted=" + isDeleted
				+ ", allowSummaryStats=" + allowSummaryStats + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", isChecked=" + isChecked + "]";
	}
	
	
}
