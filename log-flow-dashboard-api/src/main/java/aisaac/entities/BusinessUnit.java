package aisaac.entities;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "business_unit")
@Data
public class BusinessUnit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bu_id")
	private Long buId;
	
	@Column(name = "bu_name")
	private String buName;
	
	
}
