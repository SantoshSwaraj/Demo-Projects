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
import lombok.experimental.Accessors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "ad_user_detail")
public class AdUserDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", nullable = false, unique = true)
	private Long recId;

	@Column(name = "tenant_id", nullable = false)
	private Long tenantId;

	@Column(name = "usn_changed")
	private Integer usnChanged;

	@Column(name = "account_name", nullable = false)
	private String accountName;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "mail_id")
	private String mailId;

	@Column(name = "department_name")
	private String departmentName;

	@Column(name = "country")
	private String country;

	@Column(name = "city")
	private String city;

	@Column(name = "description")
	private String description;

	@Column(name = "member_of")
	private String memberOf;

	@Column(name = "object_guid")
	private String objectGuid;

	@Column(name = "object_sid")
	private String objectSid;

	@Column(name = "distinguished_name")
	private String distinguishedName;

	@Column(name = "status")
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "user_score")
	private Float userScore;

	@Column(name = "is_watchlisted")
	private boolean watchlisted;

	@Column(name = "watchlisted_by")
	private Integer watchlistedBy;

	@Column(name = "profile_score")
	private Float profileScore;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "profile_updated_date")
	private LocalDateTime profileUpdatedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "watchlisted_date")
	private LocalDateTime watchlistedDate ;

}
