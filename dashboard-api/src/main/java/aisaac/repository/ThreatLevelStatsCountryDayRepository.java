package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.dto.CountryCodeAndLevelDto;
import aisaac.entities.ThreatLevelStatsCountryDay;

public interface ThreatLevelStatsCountryDayRepository extends JpaRepository<ThreatLevelStatsCountryDay, Long> {

	List<CountryCodeAndLevelDto> findAllByCreatedDateBetweenAndTenantId(LocalDateTime fromDate,
			LocalDateTime toDate, Long tenantId);
}
