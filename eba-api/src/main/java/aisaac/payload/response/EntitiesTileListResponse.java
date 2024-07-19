package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class EntitiesTileListResponse {

	private String entityName;
	private Float entityScore;
	private String productVendor;
	private String productName;
	private String productVersion;
	private String assetTags;
	private Float scoreDiff;
	private Float entityProfileScore;
	private Long lastUpdatedDate;
	private boolean addedToWatchList;
	private Long recId;
	private String entityType;
}
