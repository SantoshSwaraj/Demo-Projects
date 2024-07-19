package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.ApplicationSettingsMaster;

public interface ApplicationSettingsMasterRepository extends JpaRepository<ApplicationSettingsMaster, Long>{
	
	public ApplicationSettingsMaster findBySettingsType(String settingType);

}
