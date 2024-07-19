package aisaac.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.dto.ThreatCountDto;
import aisaac.entities.HourlyThreatCount;

public interface HourlyThreatCountRepository
		extends JpaRepository<HourlyThreatCount, Long>, JpaSpecificationExecutor<HourlyThreatCount> {

	ThreatCountDto findByIntervalDateBetweenAndTenantId(LocalDateTime fromDate, LocalDateTime toDate, Long tenantId);

}
