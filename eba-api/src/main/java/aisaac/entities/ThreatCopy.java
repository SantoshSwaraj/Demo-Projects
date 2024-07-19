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
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "threat_copy")
public class ThreatCopy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "threat_id")
	private Long threatId;
	
	@Column(name = "ad_user_rec_id")
	private Long adUserId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "threat_name")
	private String threatName;
	
	@Column(name = "threat_source")
	private String threatSource;
	
	@Column(name = "source_ip")
	private String sourceIp;
	
	@Column(name = "source_port")
	private Integer sourcePort;
	
	@Column(name = "destination_ip")
	private String destinationIp;
	
	@Column(name = "destination_port")
	private Integer destinationPort;
	
	@Column(name = "source_host_name")
	private String sourceHostName;
	
	@Column(name = "destination_host_name")
	private String destinationHostName;
	
	@Column(name = "destination_url")
	private String destinationUrl;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "event_time")
	private LocalDateTime eventTime;
	
	@Column(name = "source_location")
	private String sourceLocation;
	
	@Column(name = "destination_location")
	private String destinationLocation;
	
	@Column(name = "severity")
	private Integer severity;
	
	@Column(name = "device_product")
	private String productName;
	
	@Column(name = "device_vendor")
	private String productVendor;
	
	@Column(name = "src_adr_entity_rec_id")
	private Integer srcAdrEntityRecId;
	
	@Column(name = "dest_adr_entity_rec_id")
	private Integer destAdrEntityRecId;

	@Column(name = "src_adr_entity_score")
	private Float srcAdrEntityScore;

	@Column(name = "dest_adr_entity_score")
	private Float destAdrEntityScore;
}
