package aisaac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.dto.EntityScoreDto;
import aisaac.entities.AdrEntityScore;

public interface AdrEntityScoreRepository
		extends JpaRepository<AdrEntityScore, Long>, JpaSpecificationExecutor<AdrEntityScore> {

	public long countByTenantIdAndCreatedDateGreaterThanAndCreatedDateLessThanEqual(Long tenantId,
			LocalDateTime fromDate, LocalDateTime toDate);

	public List<EntityScoreDto> findTop10ByWatchlistedTrueAndTenantIdOrderByEntityScoreDesc(Long tenantId);

	public List<EntityScoreDto> findTop10ByTenantIdAndEntityScoreDiffGreaterThanOrderByEntityScoreDiffDesc(
			Long tenantId, Float entityScoreDiff);
	
	public List<EntityScoreDto> findAllByTenantId(Long tenantId);
}
