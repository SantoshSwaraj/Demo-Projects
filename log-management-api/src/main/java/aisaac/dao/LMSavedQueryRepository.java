package aisaac.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aisaac.model.LmSavedQuery;
import aisaac.model.LogMgmtGetSavedQueryDTO;

@Repository
public interface LMSavedQueryRepository extends JpaRepository<LmSavedQuery, Integer>{

	@Query(value = "SELECT lm.rec_id AS recId,lm.tenant_id AS tenantId,lm.created_by AS createdBy,lm.query_name AS queryName, " 
			+ "lm.query_text AS queryText,lm.created_date AS createdDate,tn.tenant_name AS tenantName, " 
			+ "u.display_name AS displayName FROM (SELECT * FROM lm_saved_query WHERE datasource_type =:datasourceType ) lm " 
			+ "LEFT JOIN tenant tn on lm.tenant_id=tn.tenant_id " 
			+ "LEFT JOIN user u on lm.created_by=u.user_id " 
			+ "WHERE lm.tenant_id IN (:tenantIds) ORDER BY lm.created_date DESC limit :maxLimit", nativeQuery= true)
	public ArrayList<Object> getSavedQueriesForNonPalUsers(
			@Param("datasourceType") String datasourceType,
			@Param("tenantIds") List<Integer> tenantIds,
			@Param("maxLimit") Long maxLimit);
	
	@Query(value = "SELECT lm.rec_id AS recId,lm.tenant_id AS tenantId,lm.created_by AS createdBy,lm.query_name AS queryName, "
		 	+ "lm.query_text AS queryText,lm.created_date AS createdDate,tn.tenant_name AS tenantName, " 
		 	+ "u.display_name AS displayName FROM (SELECT * FROM lm_saved_query WHERE datasource_type =:datasourceType ) lm  " 
		 	+ "LEFT JOIN tenant tn ON lm.tenant_id=tn.tenant_id " 
		 	+ "LEFT JOIN user u ON lm.created_by=u.user_id " 
		 	+ "WHERE lm.tenant_id IS NULL OR lm.tenant_id IN (:tenantIds) ORDER BY lm.created_date DESC limit :maxLimit", nativeQuery = true)
	public ArrayList<Object> getSavedQueries(
			@Param("datasourceType") String datasourceType,
			@Param("tenantIds") List<Integer> tenantIds,
			@Param("maxLimit") Long maxLimit);
	
	public Integer countByQueryName(String queryName);
}
