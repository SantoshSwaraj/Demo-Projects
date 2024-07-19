package aisaac.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_custom_field_list")
public class UserCustomFieldList implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	private Integer recId;
	
	@Column(name = "module_name", nullable = false, length = 255)
	private String moduleName;
	
	@Column(name = "section_name", nullable = false, length = 255)
	private String sectionName;
	
	@Column(name = "field_details", nullable = false)
	private String fieldDetails;
	
	@Column(name = "created_by", nullable = true)
	private Long createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false, length = 19)
	private Date createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date", nullable = true, length = 19)
	private Date updatedDate;
	
}
