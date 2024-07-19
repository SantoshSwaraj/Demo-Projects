package aisaac.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aisaac.entities.CspmRemediationStatus;

public interface CspmRemediationStatusRepository extends JpaRepository<CspmRemediationStatus, BigInteger> {
	
	@Modifying
	@Query("UPDATE CspmRemediationStatus crs SET crs.latest = false WHERE crs.cspmFindingId = :complianceFindingId")
	public int updateLatestToFalseByCspmFindingId(@Param("complianceFindingId") BigInteger cspmFindingId);
	
	public Optional<String> findTop1StatusByCspmFindingIdAndLatestOrderByCreatedDateDesc(
            BigInteger complianceFindingId, boolean latest);

	@Query("SELECT status FROM CspmRemediationStatus WHERE cspmFindingId = :complianceFindingId AND latest = :latest ORDER BY createdDate DESC")
	public Optional<String> findStatusByCspmFindingIdAndLatestOrderByCreatedDateDesc(@Param("complianceFindingId") BigInteger complianceFindingId, @Param("latest") boolean latest);

	

}
