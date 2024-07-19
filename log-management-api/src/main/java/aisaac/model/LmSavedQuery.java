package aisaac.model;

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
@Table(name = "lm_saved_query")
public class LmSavedQuery implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer recId;
	private User created_by;
//	@Column(name ="tenant_id")
	private Integer tenantId;
	private String modelName;
	private String datasourceType;
	private String queryName;
	private String queryText;
	private Date createdDate;
	
	public LmSavedQuery() {
	}

	public LmSavedQuery(Integer tenant) {
		this.tenantId = tenant;
	}
	
	public LmSavedQuery(User user, Integer tenant, 
			String modelName, String datasourceType,
			String queryName,String queryText,
			Date createdDate) {
		this.created_by = user;		
		this.tenantId = tenant;
		this.modelName = modelName;
		this.datasourceType = datasourceType;
		this.queryName = queryName;
		this.queryText = queryText;
		this.createdDate = createdDate;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	public Integer getRecId() {
		return recId;
	}

	public void setRecId(Integer recId) {
		this.recId = recId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	public User getUser() {
		return this.created_by;
	}

	public void setUser(User user) {
		this.created_by = user;
	}
	
//	@ManyToOne(fetch = FetchType.LAZY)
	@Column(name = "tenant_id", nullable = true)
	public Integer getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(Integer tenant) {
		this.tenantId = tenant;
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
	@Column(name = "query_name")
	public String getQueryName() {
		return this.queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
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