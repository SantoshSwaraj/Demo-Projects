package aisaac.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tenant_settings")
public class TenantSettings implements java.io.Serializable {

	  private static final long serialVersionUID = 1L;

	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "rec_id", unique = true, nullable = false)
	  private Integer recId;


	  
	  @Column(name = "tenant_id", nullable = false)
	  private Integer tenantId;

	  @Column(name = "param_type", nullable = false)
	  private String paramType;

	  @Column(name = "param_name", nullable = false)
	  private String paramName;

	  @Column(name = "param_value", nullable = false)
	  private String paramValue;

	  @Column(name = "description", nullable = false)
	  private String description;

	}
