package aisaac.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import aisaac.entities.UbaPgOneUserTile;

public interface UbaPgOneUserTileRepository
		extends JpaRepository<UbaPgOneUserTile, Long>, JpaSpecificationExecutor<UbaPgOneUserTile> {

	@Modifying
	@Query("update UbaPgOneUserTile u set watchlisted=:watchlisted,watchlistedDate=NOW(),watchlistedBy=:watchlistedBy where tenantId=:tenantId and adUserId=:adUserId")
	public void updateWatchListDataInUbaPgOneUserTileTableByTenantIdAndAdUserId(Long tenantId, Integer adUserId,
			Boolean watchlisted,Long watchlistedBy);

	public long countByTenantIdAndCreatedDateGreaterThanAndCreatedDateLessThanEqual(Long tenantId, LocalDateTime fromDate,
			LocalDateTime toDate);
	
}
