package aisaac.entities;

import java.time.LocalDateTime;

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
@Table(name = "application_settings_master")
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationSettingsMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "settings_id", unique = true, nullable = false)
	private Integer settingsId;

	@Column(name = "settings_type", nullable = false)
	private String settingsType;

	@Column(name = "settings_name", nullable = false)
	private String settingsName;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "data_list")
	private String dataList;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_date")
	private LocalDateTime updateDate;
}
