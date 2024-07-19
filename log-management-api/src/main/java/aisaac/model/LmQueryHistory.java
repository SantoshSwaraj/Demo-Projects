package aisaac.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.utils.CustomDateTimeSerializer;
import aisaac.utils.StringHtmlEscapeSerializer;

@Entity
@Table(name = "lm_query_history")
public class LmQueryHistory implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private BigInteger recId;
	private User executed_by;
	private String applicableTenantIds;
	private String modelName;
	private String datasourceType;
	private String queryText;
	private Date createdDate;

	public LmQueryHistory() {
	}

	public LmQueryHistory(User user, String applicableTenantIds, 
			String modelName, String datasourceType, 
			String queryText, Date createdDate) {
		this.executed_by = user;		
		this.applicableTenantIds = applicableTenantIds;
		this.modelName = modelName;
		this.datasourceType = datasourceType;
		this.queryText = queryText;
		this.createdDate = createdDate;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	public BigInteger getRecId() {
		return recId;
	}

	public void setRecId(BigInteger recId) {
		this.recId = recId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "executed_by")
	public User getUser() {
		return this.executed_by;
	}

	public void setUser(User user) {
		this.executed_by = user;
	}
	
	@JsonSerialize(using = StringHtmlEscapeSerializer.class)
	@Column(name = "applicable_tenant_ids")
	public String getApplicableTenantIds() {
		return applicableTenantIds;
	}

	public void setApplicableTenantIds(String applicableTenantIds) {
		this.applicableTenantIds = applicableTenantIds;
	}
	
	@Column(name = "model_name", nullable = true)
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	@Column(name = "datasource_type", nullable = false, length = 50)
	public String getDatasourceType() {
		return datasourceType;
	}

	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}

	@JsonSerialize(using = StringHtmlEscapeSerializer.class)
	@Column(name = "query_text")
	public String getQueryText() {
		return this.queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 19)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
    
}