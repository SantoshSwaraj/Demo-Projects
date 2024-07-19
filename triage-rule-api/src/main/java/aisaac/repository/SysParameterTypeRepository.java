package aisaac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.SysParameterType;

public interface SysParameterTypeRepository extends JpaRepository<SysParameterType, Long> {

	Optional<SysParameterType> findByParamType(String paramType);

}
