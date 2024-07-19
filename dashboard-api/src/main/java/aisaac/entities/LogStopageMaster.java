package aisaac.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

import aisaac.util.AppConstants;
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
@Table(name = "log_stopage_master")
public class LogStopageMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id")
	private Long recId;

	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "asset_id")
	private Long assetId;
	
	@Column(name = "product_id")
	private Long productId;
	
	@Column(name = "disabled")
	private boolean disabled;

	@Column(name = "suppressed")
	private boolean suppressed;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_receive_time")
	private LocalDateTime logReceiveTime;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "device_status")
	private Integer deviceStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_stoppage_time")
	private LocalDateTime logStoppageTime;
	
	@Formula("count(rec_id)")
	private Long count;
	
	@Formula("min(log_receive_time)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime minLogReceiveTime;
	
	@Formula("max(log_receive_time)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime maxLogReceiveTime;
	
	@Formula("min(log_stoppage_time)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime minLogStopageTime;
	
	@Formula("max(log_stoppage_time)")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime maxLogStopageTime;

}
