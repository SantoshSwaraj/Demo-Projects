package aisaac.model;

// Generated 2 Feb, 2016 11:55:05 AM by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * GlobalSettings generated by hbm2java
 */
@Entity
@Table(name = "global_settings")
public class GlobalSettings implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public GlobalSettings() {
	}

	public GlobalSettings(String paramName, String paramValue) {
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Getter
	@Setter
	@Column(name = "rec_id", unique = true, nullable = false)
	private Integer recId;

	@Getter
    @Setter
    @Column(name = "param_type", nullable = false, length = 100)
    private String paramType;
	
	@Getter
    @Setter
	@Column(name = "param_name", nullable = false, length = 64)
	private String paramName;
	
	@Getter
    @Setter
    @Column(name = "description", nullable = false, length = 1024)
    private String description ;

	@Getter
    @Setter
	@Column(name = "param_value", nullable = false)
	private String paramValue;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((paramName == null) ? 0 : paramName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GlobalSettings other = (GlobalSettings) obj;
    if (paramName == null) {
      if (other.paramName != null)
        return false;
    } else if (!paramName.equals(other.paramName))
      return false;
    return true;
  }

}
