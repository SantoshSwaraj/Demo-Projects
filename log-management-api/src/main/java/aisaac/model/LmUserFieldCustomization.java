package aisaac.model;

//Generated Feb 11, 2021 2:36:13 PM by Hibernate Tools 4.3.5.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "lm_user_field_customization")
public class LmUserFieldCustomization implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Integer recId;
	private LmDruidFields lmDruidFields;
	private User user;
	private String customizeType;
	private Date createdDate;
	private String datasourceType;

	public LmUserFieldCustomization() {}

	public LmUserFieldCustomization(LmDruidFields lmDruidFields, User user, String customizeType) {
		this.lmDruidFields = lmDruidFields;
		this.user = user;
		this.customizeType = customizeType;
	}

	public LmUserFieldCustomization(LmDruidFields lmDruidFields, User user, String customizeType, Date createdDate, String datasourceType) {
		this.lmDruidFields = lmDruidFields;
		this.user = user;
		this.customizeType = customizeType;
		this.createdDate = createdDate;
		this.datasourceType = datasourceType;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "rec_id", unique = true, nullable = false)
	public Integer getRecId() {
		return this.recId;
	}

	public void setRecId(Integer recId) {
		this.recId = recId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "field_id", nullable = false)
	public LmDruidFields getLmDruidFields() {
		return this.lmDruidFields;
	}

	public void setLmDruidFields(LmDruidFields lmDruidFields) {
		this.lmDruidFields = lmDruidFields;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "customize_type", nullable = false, length = 50)
	public String getCustomizeType() {
		return this.customizeType;
	}

	public void setCustomizeType(String customizeType) {
		this.customizeType = customizeType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 19)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	@Column(name = "datasource_type", nullable = false, length = 50)
	public String getDatasourceType() {
		return this.datasourceType;
	}

	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}
}

