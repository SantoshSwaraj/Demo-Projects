package aisaac.service;

import java.util.List;

import aisaac.dto.LmDruidFieldsDTO;

public interface LmUserFieldCustomizationService {
	
	public List<LmDruidFieldsDTO> getLmUserFieldsCustomizationMainTable(Integer userId, String datasourceType);

	public List<LmDruidFieldsDTO> saveCustomizeColumnsForMainTable(Integer userId, String[] customizedFields, String datasourceType);

}
