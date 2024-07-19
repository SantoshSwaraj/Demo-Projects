package aisaac.service;

import java.util.List;
import java.util.Map;

import aisaac.entities.ProductMaster;

public interface ProductMasterService {

	public Object getProductMasterFieldListByName(String productFieldName);
	
	public Map<String, Long> getProductTypeMap();
	
	public List<ProductMaster> getAllProductMasterWithProductType();
}
