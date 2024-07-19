package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.TriageRule;

public interface TriageRuleRepository extends JpaRepository<TriageRule, Long>, JpaSpecificationExecutor<TriageRule> {



}
