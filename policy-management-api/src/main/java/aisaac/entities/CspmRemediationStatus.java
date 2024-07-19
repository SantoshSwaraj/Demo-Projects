package aisaac.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cspm_remediation_status")
public class CspmRemediationStatus implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id",insertable = false, updatable = false)
	private BigInteger recId;
	
	@Column(name = "tenant_id")
	private Integer tenantId;
	
	@Column(name = "aisaac_tenant_id")
	private String aisaacTenantId;
	
	@Column(name = "cloud_resource_id")
	private String cloudResourceId;
	
	@Column(name = "security_control_id")
	private String securityControlId;
	
	@Column(name = "playbook_name")
	private String playbookName;

	@Column(name = "status")
	private String status;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "cspm_finding_id")
	private BigInteger cspmFindingId;
	
	@Column(name = "latest")
	private Boolean latest;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date", nullable = false)
	private Date updatedDate;
	
	@Column(name = "creator_user")
	private Integer creatorUser;

	@Column(name = "updator_user")
	private Integer updatorUser;

}
