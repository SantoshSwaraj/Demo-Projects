package aisaac.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "log_stopage_report_notifications")
public class LogStopageReportNotifications {

	@Id
	@Column(name = "tenant_id", nullable = true)
	private Long tenantId;

	@Column(name = "tenant_name")
	private String tenantName;

	@Column(name = "to_email")
	private String toEmail;

	@Column(name = "cc_email")
	private String ccEmail;

	@Column(name = "enable_mail")
	private Integer enableMail;

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(name = "updated_date")
	private LocalDate updatedDate;

	@Column(name = "created_by_id")
	private Long createdBy;

	@Column(name = "updated_by_id")
	private Long updatedBy;

	@Column(name = "mail_triggered_hour")
	private Integer mailTriggeredHour;

	@Column(name = "last_mail_triggered")
	private LocalDate lastMailTriggered;

}
