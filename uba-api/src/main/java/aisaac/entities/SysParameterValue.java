package aisaac.entities;

import java.io.Serializable;

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
@Table(name = "sys_parameter_value")
public class SysParameterValue implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_param_value_id", unique = true, nullable = false, length = 11)
	private Long paramValueId;
	
	@Column(name = "sys_param_type_id", nullable = false, length = 11)
	private Long paramTypeId;
	
	@Column(name = "sys_param_value", nullable = false, length = 255)
	private String paramValue;

}
