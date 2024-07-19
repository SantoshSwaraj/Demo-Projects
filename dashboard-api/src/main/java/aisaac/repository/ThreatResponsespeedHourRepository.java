package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.dto.SpeedOfResponseForValidationTrendDataDto;
import aisaac.entities.ThreatResponsespeedHour;

public interface ThreatResponsespeedHourRepository
		extends JpaRepository<ThreatResponsespeedHour, Long>, JpaSpecificationExecutor<ThreatResponsespeedHour> {

	List<SpeedOfResponseForValidationTrendDataDto> findAllByCreatedDateBetweenAndTenantId(LocalDateTime fromDate,
			LocalDateTime toDate, Long tenantId);
}
