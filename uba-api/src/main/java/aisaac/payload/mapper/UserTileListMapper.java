package aisaac.payload.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.UbaPgOneUserTile;
import aisaac.payload.response.UserTileListResponse;
import aisaac.util.UbaUtil;

public class UserTileListMapper {

	public static List<Object> map(List<UbaPgOneUserTile> data){
		return data.stream().map(o-> new UserTileListResponse()
				.setAccountName(o.getAccountName())
				.setSources(o.getGroupSources())
				.setNoOfAnomalies(o.getAnomalyCount())
				.setNoOfAlerts(o.getAlertCount())
				.setUserName(o.getUserName())
				.setScore(o.getScore())
				.setDepartMent(o.getDepartmentName())
				.setAdUserId(o.getAdUserId())
				.setScoreDiff(o.getScoreDiff())
				.setUserProfileScore(o.getUserProfileScore())
				.setLastUpdatedDate(UbaUtil.getLocalDateTimeInMilliSec(o.getCreatedDate()))
				.setAddedToWatchList(
						Objects.nonNull(o.getWatchlisted()) ? o.getWatchlisted() : false)
				).collect(Collectors.toList());
	}
}
