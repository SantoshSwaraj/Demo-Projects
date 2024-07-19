package aisaac.payload.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.UbaPgOneUserTileEntity;
import aisaac.payload.response.UserScoreResponse;

public class UserScoreMapper {

	public static List<Object> mapUserScore(List<UbaPgOneUserTileEntity> ubaPgOneUserTileEntitiesList) {
		return ubaPgOneUserTileEntitiesList	.stream()
				.map(o -> new UserScoreResponse()
						.setUserName(o.getAccountName())
						.setScore(Objects.nonNull(o.getScore()) ? o.getScore() : 0))
				.collect(Collectors.toList());
	}
	
	public static List<Object> mapUserScoreDiff(List<UbaPgOneUserTileEntity> ubaPgOneUserTileEntitiesList) {
		return ubaPgOneUserTileEntitiesList.stream()
				.map(o -> new UserScoreResponse()
						.setUserName(o.getAccountName())
						.setScore(Objects.nonNull(o.getScoreDiff()) ? o.getScoreDiff() : 0))
				.collect(Collectors.toList());
	}
}
