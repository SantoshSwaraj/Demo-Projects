package aisaac.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * Tenant generated by hbm2java
 */
@Entity
@Table(name = "tenant")
public class Tenant implements java.io.Serializable {

	private Integer tenantId;
	private String tenantName;
	private String customerName;
	private Date createdDate;
	private Date updatedDate;
	private boolean useAdAuth;
	
	public Tenant() {
	}
    
    public Tenant(Integer tenantId) {
        this.tenantId = tenantId;
    }
	
	public Tenant(Integer tenantId, String tenantName) {
		this.tenantId = tenantId;
		this.tenantName = tenantName;
	}
	
	public Tenant(Integer tenantId, String tenantName, String customerName) {
		this.tenantId = tenantId;
		this.tenantName = tenantName;
		this.customerName = customerName;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "tenant_id", unique = true, nullable = false)
	public Integer getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "tenant_name")
	public String getTenantName() {
		return this.tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	@Column(name = "customer_name")
	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 19)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date", length = 19)
	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Column(name = "use_ad_auth")
	public boolean isUseAdAuth() {
		return useAdAuth;
	}

	public void setUseAdAuth(boolean useAdAuth) {
		this.useAdAuth = useAdAuth;
	}
	
	@Setter
	@Getter
	@Column(name = "enabled", nullable = false)
	private boolean enabled;


}
