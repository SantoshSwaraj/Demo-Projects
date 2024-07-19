package aisaac.payload.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.dto.EntityScoreDto;
import aisaac.payload.response.EntityScoreResponse;

public class EntityScoreMapper {

	public static List<Object> mapEntityScore(List<EntityScoreDto> adrEntityScoresList) {
		return adrEntityScoresList.stream()
				.map(o -> new EntityScoreResponse()
						.setEntityName(o.getEntityId())
						.setScore(Objects.nonNull(o.getEntityScore()) ? o.getEntityScore() : 0))
				.collect(Collectors.toList());
	}
	
	public static List<Object> mapEntityScoreDiff(List<EntityScoreDto> adrEntityScoresList) {
		return adrEntityScoresList.stream()
				.map(o -> new EntityScoreResponse()
						.setEntityName(o.getEntityId())
						.setScore(Objects.nonNull(o.getEntityScoreDiff()) ? o.getEntityScoreDiff() : 0))
				.collect(Collectors.toList());
	}
}
