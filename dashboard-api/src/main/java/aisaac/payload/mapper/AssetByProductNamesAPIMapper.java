package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;

import aisaac.entities.LogStopageMaster;
import aisaac.entities.ProductMaster;
import aisaac.payload.response.AssetByProductNameAPIResponse;
import aisaac.util.DashboardUtils;
import aisaac.util.ResponseOrMessageConstants;

public class AssetByProductNamesAPIMapper {

	public static Object map(List<LogStopageMaster> logStopageMasters,Map<Long, ProductMaster> productMap, Long totalAssetsCount, Long filteredAssetCount) {

		List<AssetByProductNameAPIResponse> responses= logStopageMasters.stream().map(o->new AssetByProductNameAPIResponse()
				.setProductName(DashboardUtils.getProductNameFromProductMaster(productMap.get(o.getProductId())))
				.setProductVendor(DashboardUtils.getProductVendorFromProductMaster(productMap.get(o.getProductId())))
				.setProductCount(o.getCount())
				.setProductPercentage(DashboardUtils.calculatePercentage(o.getCount(), totalAssetsCount))
				).toList();
				return Map
						.of(ResponseOrMessageConstants.TOTAL_ASSET_COUNT, totalAssetsCount,
								ResponseOrMessageConstants.PRODUCTS_MESSAGE,
								filteredAssetCount > 30 ? ResponseOrMessageConstants.MORE_THAN_30
										: ResponseOrMessageConstants.LESS_THAN_30,
								ResponseOrMessageConstants.DATA, responses);
			}
}
