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
@Entity
@Data
@Table(name = "asset_tag")
@AllArgsConstructor
@NoArgsConstructor
public class AssetTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "asset_tag_id")
	private Long assetTagId;
	
	@Column(name = "tenant_id")
	private Long tenantId;
	
	@Column(name = "asset_id")
	private Long assetId;
	
	@Column(name = "tag_id")
	private Long tagId;
}
