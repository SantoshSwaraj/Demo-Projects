package aisaac.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_parameter_type")
public class SysParameterType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_param_type_id", unique = true, nullable = false, length = 11)
	private Long paraTypeId;
	
	@Column(name = "sys_param_type", nullable = false, length = 255)
	private String paramType;
}
