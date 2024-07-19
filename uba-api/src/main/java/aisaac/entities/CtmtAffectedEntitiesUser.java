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

@Entity
@Table(name = "ctmt_affected_entities_user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CtmtAffectedEntitiesUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rec_id")
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "ticket_id")
	private Long ticketId;
	
	@Column(name = "user_type")
	private String userType;
	
	@Column(name = "username")
	private String username;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
}
