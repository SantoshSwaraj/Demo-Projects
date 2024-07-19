package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.SysParameterType;
import aisaac.entities.SysParameterValue;

public interface SysParameterValueRepository extends JpaRepository<SysParameterValue, Long>  {
	
	Optional<List<SysParameterValue>> findByParamTypeId(Long paramTypeId);

	Optional<List<SysParameterValue>> findAllByParamTypeIdIn(List<Long> paramTypeIdList);
}

