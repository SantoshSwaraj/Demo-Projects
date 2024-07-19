package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.HourlyThreatCount;
import aisaac.entities.ProductMaster;
import aisaac.payload.response.ThreatByProductNameResponse;
import aisaac.util.DashboardUtils;

public class ThreatByProductNamesMapper {
	
	public static Object map(List<HourlyThreatCount> hourlyThreatCountsList, Map<Long, ProductMaster> prodcutMasterMap) {
		return hourlyThreatCountsList.stream().map(o->new ThreatByProductNameResponse()
				.setCount(o.getSumCount())
				.setProductName(DashboardUtils.getProductNameFromProductMaster(prodcutMasterMap.get(o.getProductId())))
				.setProductVendor(DashboardUtils.getProductVendorFromProductMaster(prodcutMasterMap.get(o.getProductId())))
				).collect(Collectors.toList());
	}

}
