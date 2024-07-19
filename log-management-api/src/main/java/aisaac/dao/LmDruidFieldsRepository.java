package aisaac.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aisaac.model.LmDruidFields;
import aisaac.model.LmDruidFieldsDTO;

@Repository
public interface LmDruidFieldsRepository extends JpaRepository<LmDruidFields, Integer> {

	List<LmDruidFields> findByDatasourceTypeAndIsDeleted(String datasourceType, boolean isDeleted);

	@Query("SELECT fieldId as fieldId," + "lmf.field as field, lmf.datasourceType as datasourceType, "
			+ "lmf.dataType as dataType, lmf.displayName as displayName, "
			+ "lmf.threatColumnName as threatColumnName, " + "lmf.displaySequenceNumber as displaySequenceNumber, "
			+ "lmf.isDeleted as isDeleted, " + "lmf.allowSummaryStats as allowSummaryStats, "
			+ "lmf.createdDate as createdDate, " + "lmf.updatedDate as updatedDate " + "from LmDruidFields lmf "
			+ "where lmf.datasourceType=(:datasourceType) "
			+ "AND lmf.isDeleted=false ORDER BY lmf.displaySequenceNumber")
	public List<LmDruidFields> findByDatasourceType(@Param("datasourceType") String datasourceType);

	@Query("SELECT lmf FROM LmDruidFields lmf "
			+ "WHERE lmf.datasourceType=:datasourceType AND lmf.allowSummaryStats=:allowSummaryStats "
			+ "AND lmf.isDeleted=false ORDER BY lmf.displaySequenceNumber")
	public List<LmDruidFields> getLmDruidFields(@Param("datasourceType") String datasourceType,
			@Param("allowSummaryStats") Boolean allowSummaryStats);
	
	
	@Query("SELECT lmf.fieldId "
			+ "FROM LmDruidFields lmf "
			+ "WHERE lmf.datasourceType=:datasourceType "
			+ "AND lmf.isDeleted=false AND lmf.displayName=:displayName")
	public Integer getLmDruidFieldId(String datasourceType, String displayName);
}
