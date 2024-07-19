package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.SysParameterValue;

public interface SysParameterValueRepository extends JpaRepository<SysParameterValue, Long>  {
	Optional<List<SysParameterValue>> findByParamTypeId(Long paramTypeId);
	
	Optional<List<SysParameterValue>> findByParamTypeIdIn(List<Long> paramTypeId);
	
    Optional<SysParameterValue> findByParamTypeIdAndParamValue(Long paramTypeId, String paramValue);
    
    Optional<SysParameterValue> findByParamTypeIdAndParamValueId(Long paramTypeId, Long paramValueId);
}
