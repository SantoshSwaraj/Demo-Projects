package aisaac.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
@Table(name = "log_stopage_detected_devices")
public class LogStopageDetectedDeviceEmbeddedId implements Serializable {

	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "device_address")
	private String productIP;

	@Column(name = "device_host_name")
	private String productHostName;

	@Column(name = "tenant_id")
	private Integer customerId;

}
