package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aisaac.entities.RemediationPlaybookName;

public interface RemediationPlaybookNameRepository extends JpaRepository<RemediationPlaybookName, Long>{

	List<RemediationPlaybookName> findAll();

	
	@Query("SELECT e.securityControlId FROM RemediationPlaybookName e WHERE e.playbookName LIKE %:searchRemediationPlaybookName% ")
	List<String> findAllByColumnName(String searchRemediationPlaybookName);

	@Query("SELECT rp.playbookName FROM RemediationPlaybookName rp WHERE rp.securityControlId = :securityControlId")
	Optional<String> findPlaybookNameBySecurityControlId(@Param("securityControlId") String securityControlId);
	
	Optional<List<RemediationPlaybookName>> findByPlaybookNameNotNull();
		
}
