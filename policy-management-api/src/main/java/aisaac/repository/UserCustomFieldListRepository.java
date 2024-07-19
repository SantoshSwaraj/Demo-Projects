package aisaac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import aisaac.entities.UserCustomFieldList;

public interface UserCustomFieldListRepository extends JpaRepository<UserCustomFieldList, Long> {
	
	@Query("SELECT u FROM UserCustomFieldList u WHERE u.moduleName=:moduleName AND u.sectionName=:sectionName AND (u.createdBy=:createdBy OR u.createdBy IS NULL)")
	Optional<List<UserCustomFieldList>> findUserCustomization(Long createdBy, String moduleName, String sectionName);

	Optional<UserCustomFieldList> findByCreatedByAndModuleNameAndSectionName(Long createdBy, String moduleName, String sectionName);

}
