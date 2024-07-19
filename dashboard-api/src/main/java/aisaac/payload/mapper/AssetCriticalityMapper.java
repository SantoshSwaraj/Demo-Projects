package aisaac.payload.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import aisaac.entities.AssetMaster;
import aisaac.util.AppConstants;

public class AssetCriticalityMapper {

	public static Object map(List<AssetMaster> assetMastersList, Long totalAssetCount) {
		Map<String, Long> defaultData = new LinkedHashMap<String, Long>(
				Map.of("Critical", 0L, "High", 0L, "Medium", 0L, "Low", 0L, "Undefined", 0L));
		assetMastersList.forEach(o -> {
			if (Objects.isNull(o.getCriticality()) || o.getCriticality() == 0) {
				defaultData.put("Undefined", defaultData.get("Undefined") + o.getCount());
			} else {
				defaultData.put(AppConstants.ASSET_CRITICAL_STATUS_MAP.get(o.getCriticality()),
						defaultData.get(AppConstants.ASSET_CRITICAL_STATUS_MAP.get(o.getCriticality())) + o.getCount());
			}
		});
		
		Long assetCount=defaultData.values().stream().mapToLong(Long::longValue).sum();
		if (assetCount < totalAssetCount) {
			defaultData.put("Undefined", defaultData.get("Undefined") + (totalAssetCount - assetCount));
		}
		return defaultData;
	}

}
