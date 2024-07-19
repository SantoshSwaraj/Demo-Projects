package aisaac.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.utils.LmHistoricalStatus;
import aisaac.utils.StringHtmlEscapeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "lm_historical_logs")
public class LmHistoricalLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	private BigInteger recId;
	
	@Column(name = "tenant_id", nullable = true)
	private Integer tenantId;
	
	@JsonSerialize(using = StringHtmlEscapeSerializer.class)
	@Column(name = "query_text")
	private String queryText;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "from_date", length = 19)
	private Date fromDate;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "to_date", length = 19)
	private Date toDate;
	
	@JoinColumn(name = "executed_by")
	private Integer executedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "executed_on", length = 19)
	private Date executedOn;
	
	@Column(name = "query_id")
	private String queryId;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private LmHistoricalStatus status;
	
	@Column(name = "status_message")
	private String statusMessage;
	
	@Column(name="is_deleted")
	private boolean isDeleted;

}
