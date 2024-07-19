package aisaac.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aisaac.dto.SysParamDto;
import aisaac.entities.RuleTypeMaster;
import aisaac.entities.SysParameterType;
import aisaac.entities.SysParameterValue;
import aisaac.repository.RuleTypeMasterRepository;
import aisaac.repository.SysParameterTypeRepository;
import aisaac.repository.SysParameterValueRepository;
@Service
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	private SysParameterTypeRepository sysParameterTypeRepo;
	
	@Autowired
	private SysParameterValueRepository sysParameterValueRepo;
	
	@Autowired
	RuleTypeMasterRepository ruleTypeMasterRepo;

	@Override
	public Map<Long,String> mapSysParamValueByType(String sysParamType) {
		List<SysParamDto> list = this.getSysParamValueByType(sysParamType);
		return list.stream()
				   .collect(Collectors.toMap(SysParamDto::getParamValueId,SysParamDto::getParamValue));
	}
	
	@Override
	public Map<Long,String> mapRuleTypeMasters() {
		List<RuleTypeMaster> ruleMasters = ruleTypeMasterRepo.findAll();

		return ruleMasters.stream()
				   .collect(Collectors.toMap(RuleTypeMaster::getRuleTypeId,RuleTypeMaster::getRuleType));
	}
	

	@Override
	public List<SysParamDto> getSysParamValueByType(String paramType) {
		SysParameterType sysParameterType = 
							sysParameterTypeRepo
										.findByParamType(paramType)
										.orElseThrow(()-> new RuntimeException(String.format("No SysParameterType found for sysParamType %s", paramType)));
		
		List<SysParameterValue> sysParamValues = 
							sysParameterValueRepo.findByParamTypeId(sysParameterType.getParaTypeId())
							.orElseThrow(()-> new RuntimeException(String.format("No SysParameterValue(s) found for sysParamType %s", paramType)));
		
		
		return sysParamValues.stream()
							 .map(e->SysParamDto.build(e.getParamValueId(), e.getParamValue()))
							 .collect(Collectors.toList());
	}


}
