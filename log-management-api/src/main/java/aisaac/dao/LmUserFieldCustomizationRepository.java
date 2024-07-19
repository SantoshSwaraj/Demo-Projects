package aisaac.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import aisaac.model.LmUserFieldCustomization;
import aisaac.model.User;

public interface LmUserFieldCustomizationRepository extends JpaRepository<LmUserFieldCustomization, Integer> {
	
	public List<LmUserFieldCustomization> findByUserAndDatasourceTypeAndCustomizeType(User user, String datasourceType, String customizeType);
	
	public void deleteByUserAndDatasourceTypeAndCustomizeType(User user, String datasourceType, String customizeType);

}
