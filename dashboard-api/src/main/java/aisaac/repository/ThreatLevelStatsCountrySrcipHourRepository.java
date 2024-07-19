package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.ThreatLevelStatsCountrySrcipHour;

public interface ThreatLevelStatsCountrySrcipHourRepository extends JpaRepository<ThreatLevelStatsCountrySrcipHour, Long>,JpaSpecificationExecutor<ThreatLevelStatsCountrySrcipHour>{

}
