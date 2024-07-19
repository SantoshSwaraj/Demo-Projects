package aisaac.payload.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.AssetMaster;
import aisaac.payload.response.AssetListResponse;

public class AssetListMapper {

	@SuppressWarnings("unchecked")
	public static List<Object> map(List<AssetMaster> assetMasters ,Map<Long, List<Long>> assetIdAndTagIdMap, Map<Long, String> tagMap) {
	
		return	assetMasters.stream().map(o->new AssetListResponse()
				.setAssetId(o.getAssetId())
				.setAssetName(o.getAssetName())
				.setAssetTags(getAssetTagsByTagId(
						assetIdAndTagIdMap.getOrDefault(o.getAssetId(), Collections.EMPTY_LIST), tagMap))
				.setProductId(o.getProductId())
				.setProductName(o.getDeviceProduct())
				.setProductVendor(o.getDeviceVendor())
				.setHostName(o.getHostName())
				.setIpAddress(o.getIpAddress())
				.setProductTypeId(o.getProductTypeId())
				.setCloudResourceId(o.getCloudResourceID())
				).collect(Collectors.toList());
	}
	
	private static List<String> getAssetTagsByTagId(List<Long> tagIdList, Map<Long, String> tagMap) {
		return tagIdList.stream().map(o -> tagMap.get(o)).collect(Collectors.toList());
	}
}
