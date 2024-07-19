package aisaac.dao;

import java.math.BigInteger;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aisaac.model.LmQueryHistory;

@Repository
public interface LmQueryHistoryRepository extends JpaRepository<LmQueryHistory, BigInteger>{

	@Query(value = "SELECT lm.rec_id as recId, (select tenant_name from tenant where tenant_id IN(lm.applicable_tenant_ids)) as tenantNames, "
			+ "lm.applicable_tenant_ids as tenantIds, lm.executed_by as executedBy, lm.query_text as queryText, "
			+ "lm.created_date as createdDateValue from lm_query_history lm where lm.executed_by=(:userId)  "
			+ "and lm.datasource_type =:datasourceType order by lm.created_date DESC limit :maxLimit", nativeQuery = true)
	public ArrayList<Object> getQueryHistory(
			@Param("userId") Integer userId,
			@Param("datasourceType") String datasourceType,
			@Param("maxLimit") Long maxLimit);
	
}
