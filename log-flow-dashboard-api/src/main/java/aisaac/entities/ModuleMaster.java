package aisaac.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Basic;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "module_master")
public class ModuleMaster implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "rec_id", nullable = false)
	@Getter
	@Setter
	private Long recId;
	
	@Column(name = "module_name", nullable = false)
	@Getter
	@Setter
	private String moduleName;
	
	@Column(name = "section_name", nullable = false)
	@Getter
	@Setter
	private String sectionName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 19)
	@Getter
	@Setter
	private LocalDateTime createdDate;
}
