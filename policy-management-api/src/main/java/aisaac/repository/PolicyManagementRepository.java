package aisaac.repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aisaac.dto.PolicyManagementDTO;
import aisaac.entities.CspmFinding;
import jakarta.transaction.Transactional;

public interface PolicyManagementRepository
		extends JpaRepository<CspmFinding, Long>, JpaSpecificationExecutor<CspmFinding> {

	@Transactional
	@Query("Select u from CspmFinding u  "
			+ "where u.recordState = 'Active'  AND u.sourceName = 'CSPM' "
			+ "Group by u.cloudAccountId , u.securityControlId "
			+ "order by u.updatedAt desc")
	public Page<CspmFinding> findAllByGroupingBy(Pageable pageable);
	
	public List<CspmFinding> findBySecurityControlIdAndCloudResourceIdAndTenantId( String securityControlId,String cloudResourceId, Integer tenantId, Pageable pageable );

	@Query("SELECT e FROM CspmFinding e WHERE e.tenantId IN :orgId ")
    List<CspmFinding> findByColumns(List<Integer> orgId,Pageable pageable);

	public Optional<CspmFinding> findByRecId(BigInteger complianceFindingId);
	
	//<T> Page<PolicyManagementDTO> findAllGroupByCount(Specification<PolicyManagementDTO> specification,Pageable pageable);

	
	public CspmFinding findByTenantIdAndCloudResourceIdAndSecurityControlIdAndRemediationStatus(Integer tenantId,
			String cloudResourceId, String securityControlId, String cspmFindingStatusProcessing);
	
	// 17011 changes
	@Query("SELECT e FROM CspmFinding e WHERE e.cloudResourceId=:cloudResourceId and recordState=:recordState and sourceName=:sourceName and tenantId=:tenantId")
	public Page<CspmFinding> findFirstNCspmFindingsLimitedTo(String cloudResourceId, String recordState,
			String sourceName,Integer tenantId, Pageable pageble);

	public Integer countByCloudResourceIdAndRecordStateAndSourceNameAndTenantId(String cloudResourceId, String recordState,
			String sourceName,Integer tenantId);


}

