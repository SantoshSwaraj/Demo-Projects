package aisaac.entities;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;

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
@Table(name = "global_settings")
@DynamicInsert
public class GlobalSettings implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	private Long recId;

    @Column(name = "param_type", nullable = false, length = 100)
    private String paramType;
	
	@Column(name = "param_name", nullable = false, length = 64)
	private String paramName;

	@Column(name = "param_value", nullable = false)
	private String paramValue;
}

