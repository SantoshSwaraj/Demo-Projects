package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aisaac.entities.RuleTypeMaster;

@Repository
public interface RuleTypeMasterRepository extends JpaRepository<RuleTypeMaster, Long>{

    List<RuleTypeMaster> findByRuleTypeIn(List<String> list);

    List<RuleTypeMaster> findByRuleTypeContaining(String name);

	Optional<List<RuleTypeMaster>> findByRuleType(String typeName);

}
	