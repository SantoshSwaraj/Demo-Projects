package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.dto.ModuleMasterIdOnly;
import aisaac.dto.SysParamDto;

public interface CommonService {
	
	public Map<String, Long> mapSysParamValueByType(String sysParamType);

	public List<SysParamDto> getSysParamValueByType(String paramType);
	
	public ModuleMasterIdOnly getModuleMasterId(String moduleName, String sectionName);

}
