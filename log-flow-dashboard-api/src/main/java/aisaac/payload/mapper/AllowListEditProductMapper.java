package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.payload.request.AllowListRequestEditProduct;

public class AllowListEditProductMapper {

	private List<AllowListRequestEditProduct> allowListFromUi=new ArrayList<>();
	private List<LogStoppageWhitelistDevices> allowListFromDB=new ArrayList<>();
	private Map<Long, AllowListRequestEditProduct> mapRecIdWithUiData=new HashMap<>();
	
	public AllowListEditProductMapper(List<LogStoppageWhitelistDevices> dbData,List<AllowListRequestEditProduct> uiData) {
		this.allowListFromDB=dbData;
		this.allowListFromUi=uiData;
		mapRecIdWithUiData= allowListFromUi.stream().collect(Collectors.toMap(AllowListRequestEditProduct::getRecId,  obj -> obj));
	}
	
	public List<LogStoppageWhitelistDevices> map(){
		return allowListFromDB.stream().map(o-> mapEditFieldsToEntity(o)).collect(Collectors.toList());
	}
	
	private LogStoppageWhitelistDevices mapEditFieldsToEntity(LogStoppageWhitelistDevices log) {
		AllowListRequestEditProduct edit=mapRecIdWithUiData.get(log.getRecId());
		
		if(edit!=null) {
			log.setTenantId(ObjectUtils.isNotEmpty(edit.getTenantId())?edit.getTenantId():log.getTenantId());
			log.setProductId(ObjectUtils.isNotEmpty(edit.getProductId())?edit.getProductId():log.getProductId());
			log.setDescription(edit.getNote());
			log.setUpdatedDate(LocalDateTime.now());
			log.setUpdatedById(edit.getUserId());
		}
		return log;
	}
}
