package aisaac.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

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
@Table(name = "ticket_asset_mapping")
@AllArgsConstructor
@NoArgsConstructor
public class TicketAssetMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id")
	private Long recId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "product_id")
	private Long productId;
	
	@Column(name = "asset_id")
	private Long assetId;
	
	@Column(name = "priority")
	private Long priority;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ticket_created_Date")
	private LocalDateTime ticketCreatedDate;
	
	@Formula("count(*)")
	private Long count;
	
}
