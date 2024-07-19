package aisaac.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * LmDruidFieldsDTO
 */
@Getter
@Setter
@Entity
@Table(name = "lm_druid_fields")
public class LmDruidFields implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "field_id", unique = true, nullable = false)
	private Integer fieldId;
	
	@Column(name = "field", nullable = false)
	private String field;

	@Column(name = "datasource_type", nullable = false)
	private String datasourceType;

	@Column(name = "data_type", nullable = false)
	private String dataType;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	@Column(name = "threat_column_name", nullable = false)
	private String threatColumnName;
	
	@Getter
	@Setter
	@Column(name = "display_sequence_number", nullable = false)
	private Integer displaySequenceNumber;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@Column(name = "allow_summary_stats", nullable = false)
	private Boolean allowSummaryStats;

	@Column(name = "created_date", nullable = false)
	private Date createdDate;

	@Column(name = "updated_date", nullable = false)
	private Date updatedDate;
	
	@Override
	public String toString() {
		return "LmDruidFieldsDTO [fieldId=" + fieldId + ", field=" + field + ", datasourceType=" + datasourceType
				+ ", dataType=" + dataType + ", displayName=" + displayName + ", threatColumnName=" + threatColumnName
				+ ", displaySequenceNumber=" + displaySequenceNumber + ", isDeleted=" + isDeleted
				+ ", allowSummaryStats=" + allowSummaryStats + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + "]";
	}
	
	
}
