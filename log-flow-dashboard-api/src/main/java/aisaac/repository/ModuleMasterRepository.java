package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.ModuleMaster;

public interface ModuleMasterRepository extends JpaRepository<ModuleMaster, Long> {
	
	<T> T findByModuleNameAndSectionName(String moduleName, String sectionName, Class<T> type);

}
