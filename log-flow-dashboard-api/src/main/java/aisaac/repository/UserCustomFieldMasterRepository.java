package aisaac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.UserCustomFieldMaster;

public interface UserCustomFieldMasterRepository extends JpaRepository<UserCustomFieldMaster, Long>,JpaSpecificationExecutor<UserCustomFieldMaster> {

	public Optional<UserCustomFieldMaster> findByCreatedByAndModuleNameAndSectionName(Integer createdBy, String moduleName,
			String sectionName);
	

}
