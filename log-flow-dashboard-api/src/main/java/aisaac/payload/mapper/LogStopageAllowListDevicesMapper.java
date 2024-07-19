package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.dto.NotesFormatDto;
import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.entities.ProductMaster;
import aisaac.payload.response.AllowListResponse;
import aisaac.util.LogFlowDashboardUtils;

public class LogStopageAllowListDevicesMapper {

	private List<LogStoppageWhitelistDevices> data=new ArrayList<>();
	
	private Map<Long, ProductMaster> productMasterMap=new HashMap<>();
	
	public LogStopageAllowListDevicesMapper(List<LogStoppageWhitelistDevices> in,Map<Long, ProductMaster> productMasterMap) {
		this.data=in;
		this.productMasterMap=productMasterMap;
	}
	
	public List<Object> map(){
		return data.stream().map(o-> new AllowListResponse(
				o.getTenantName(),
				LogFlowDashboardUtils.getProductVendorById(o.getProductId()!=null?o.getProductId().intValue():null,productMasterMap),
				LogFlowDashboardUtils.getProductNameById(o.getProductId()!=null?o.getProductId().intValue():null,productMasterMap),
				o.getDescription(),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getAllowListedDate()),
				o.getRecId(),
				o.getProductId()
				)).collect(Collectors.toList());
	}
}
