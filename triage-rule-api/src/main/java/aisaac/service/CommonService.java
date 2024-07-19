package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.dto.SysParamDto;

public interface CommonService {
	
	public Map<Long,String> mapSysParamValueByType(String sysParamType);

	public List<SysParamDto> getSysParamValueByType(String paramType);

	Map<Long, String> mapRuleTypeMasters();

}
