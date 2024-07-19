package aisaac.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@Table(name = "tag_value")
@AllArgsConstructor
@NoArgsConstructor
public class TagValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long tagId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "tag_type")
	private String tagType;
	
	@Column(name = "tag_value")
	private String tagValue;
	
}
