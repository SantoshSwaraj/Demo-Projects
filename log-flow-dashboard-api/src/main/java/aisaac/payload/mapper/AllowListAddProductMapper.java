package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.payload.request.AllowListRequestAddProduct;

public class AllowListAddProductMapper {

	private List<AllowListRequestAddProduct> data =new ArrayList<>();
	
	public AllowListAddProductMapper(List<AllowListRequestAddProduct> in) {
		this.data=in;
	}
	
	public List<LogStoppageWhitelistDevices> map() {
		//to do validation
		return data.stream().map(o-> new LogStoppageWhitelistDevices()
				.setTenantId(o.getTenantId())
				.setProductId(o.getProductId())
				.setAllowListedDate(LocalDateTime.now())
				.setCreatedById(o.getUserId())
				.setDescription(o.getNote())
				).collect(Collectors.toList());
	}
}
