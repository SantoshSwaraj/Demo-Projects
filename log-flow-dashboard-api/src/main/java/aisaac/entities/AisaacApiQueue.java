package aisaac.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aisaac_api_queue")
public class AisaacApiQueue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_id", nullable = false)
    private Long recId;
	
	@Column(name="tenant_id")
	private Long tenantId;
	
	@Column(name="module_rec_id")
	private Long moduleRecId;
	
	@Column(name="request_details")
    private String requestDetails;
	
	@Column(name="response_details")
    private String responseDetails;
	
	@Column(name="status")
    private Long status;
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="retry_attempt_count")
    private Long retryAttemptCount;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    private LocalDateTime createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    private LocalDateTime updatedDate;
}
