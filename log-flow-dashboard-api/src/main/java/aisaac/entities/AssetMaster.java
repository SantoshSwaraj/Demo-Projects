package aisaac.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Table(name = "asset_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AssetMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "asset_id", nullable = false)
	private Long assetId;

	@Column(name = "tenant_id", nullable = false)
	private Long tenantId;
	
	@Column(name = "bu_id", nullable = false)
	private Long buId;
	
	
}
