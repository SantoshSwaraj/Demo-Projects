package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.AdrEntityScore;
import aisaac.entities.SysParameterValue;
import aisaac.payload.response.EntitiesTileListResponse;
import aisaac.util.AppConstants;
import aisaac.util.EbaUtil;

public class EntitiesTileListMapper {

	public static List<Object> map(List<AdrEntityScore> data, List<SysParameterValue> syParameterValuesList) {
		
		Map<Long, String> sysParamValueMap = syParameterValuesList.stream()
				.collect(Collectors.toMap(SysParameterValue::getParamValueId, SysParameterValue::getParamValue));

		return data.stream().map(o->new EntitiesTileListResponse()
				.setRecId(o.getRecId())
				.setEntityName(o.getEntityId())
				.setEntityScore(o.getEntityScore())
				.setProductVendor(null)
				.setProductName(null)
				.setProductVersion(null)
				.setAssetTags(null)
				.setScoreDiff(o.getEntityScoreDiff())
				.setEntityProfileScore(o.getEntityProfileScore())
				.setLastUpdatedDate(EbaUtil.getLocalDateTimeInMilliSec(o.getUpdatedDate()))
				.setAddedToWatchList(Objects.nonNull(o.getWatchlisted()) ? o.getWatchlisted() : false)
				.setEntityType(sysParamValueMap.getOrDefault(o.getEntityTypeId(),
						AppConstants.CLOUD_RESOURE_ID))
				).collect(Collectors.toList());
	}
	
}
