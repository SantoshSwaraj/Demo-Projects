package aisaac.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import aisaac.model.LmHistoricalLog;
import aisaac.utils.LmHistoricalStatus;


public interface HistoricalLogRepository extends JpaRepository<LmHistoricalLog, BigInteger>,
	JpaSpecificationExecutor<LmHistoricalLog>{
	
	public long countByStatus(LmHistoricalStatus status);

	public List<LmHistoricalLog> findByTenantIdAndStatusNotIn(Integer tenantId, List<LmHistoricalStatus> statuses);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE LmHistoricalLog lh SET lh.status=:status WHERE lh.queryId=:queryId")
	public void updateByQueryId(String queryId, LmHistoricalStatus status);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE LmHistoricalLog lh SET lh.statusMessage=:statusMessage WHERE lh.queryId=:queryId")
	public void updateStatusMessageByQueryId(String queryId, String statusMessage);

	@Modifying
	@Query("UPDATE LmHistoricalLog lh SET lh.isDeleted=true WHERE lh.queryId=:queryId")
	public void deleteQuery(String queryId);

	public Optional<LmHistoricalLog> findByQueryId(String queryId);

}
