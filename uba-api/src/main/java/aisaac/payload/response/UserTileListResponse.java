package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserTileListResponse {

	private String accountName;
	private String sources;
	private Integer noOfAnomalies;
	private Integer noOfAlerts;
	private String userName;
	private Float score;
	private String departMent;
	private Integer adUserId;
	private Float scoreDiff;
	private Float userProfileScore;
	private Long lastUpdatedDate;
	private boolean addedToWatchList;
	
}
