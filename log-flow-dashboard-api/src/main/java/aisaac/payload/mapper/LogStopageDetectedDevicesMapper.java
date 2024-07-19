package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.dto.NotesFormatDto;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.ProductMaster;
import aisaac.payload.response.AddToLFMResponse;
import aisaac.util.LogFlowDashboardUtils;

public class LogStopageDetectedDevicesMapper {

private List<LogStoppageDetectedDevices> data=new ArrayList<>();
private Map<Long, List<NotesFormatDto>> noteMap=new HashMap<>();
private Map<Long, ProductMaster> productMasterMap=new HashMap<>();
	
	public LogStopageDetectedDevicesMapper(List<LogStoppageDetectedDevices> in,Map<Long, List<NotesFormatDto>> noteMap,Map<Long, ProductMaster> productMasterMap) {
		this.data=in;
		this.noteMap=noteMap;
		this.productMasterMap=productMasterMap;
	}
	
	public List<Object> map(){
		return data.stream().map(o->new AddToLFMResponse(
				o.getRecId(),
				o.getTenantName(),
				o.getProductIP(),
				o.getProductHostName(),
				LogFlowDashboardUtils.getProductVendorById(o.getProductId(),productMasterMap),
				LogFlowDashboardUtils.getProductNameById(o.getProductId(),productMasterMap),
				o.getCollectorAddress(),
				o.getCollectorHostName(),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getDetectedDate()),
				LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getLastEventReceived()),
				o.getProductId(),
				o.getMdrScannerCode(),
				noteMap.get(o.getRecId())
				)).collect(Collectors.toList());
	}
}
