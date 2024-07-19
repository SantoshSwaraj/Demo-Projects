package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.SysParameterType;

public interface SysParamTypeRepository extends JpaRepository<SysParameterType, Long> {

	Optional<List<SysParameterType>> findAllByParamTypeIn(List<String> paramTypeList);
}
