package aisaac.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aisaac.entities.ProductMaster;
import aisaac.entities.ProductTypeMaster;
import aisaac.repository.ProductMasterRepository;
import aisaac.repository.ProductTypeMasterRepository;
import aisaac.util.AppConstants;

@Service
public class ProductMasterServiceImpl implements ProductMasterService {

	@Autowired
	private ProductMasterRepository productMasterRepository;

	@Autowired
	private ProductTypeMasterRepository productTypeMasterRepository;

	@Override
	public Object getProductMasterFieldListByName(String productFieldName) {

		List<ProductMaster> productMasterList = Optional.ofNullable(productMasterRepository.findAllByDeletedFalse())
				.get();

		switch (productFieldName) {
		case AppConstants.PRODUCT_VENDOR_FIELD_IN_PRODUCT_MASTER: {
			return productMasterList.stream().map(ProductMaster::getProductVendor).collect(Collectors.toSet());
		}
		case AppConstants.PRODUCT_NAME_FIELD_IN_PRODUCT_MASTER: {
			return productMasterList.stream().map(ProductMaster::getProductName).collect(Collectors.toSet());
		}
		case AppConstants.PRODUCT_TYPE_FIELD_IN_PRODUCT_MASTER: {
			List<Long> productTypeId = productMasterList.stream().map(ProductMaster::getProductTypeId)
					.collect(Collectors.toList());
			List<ProductTypeMaster> productTypeMasters = Optional
					.ofNullable(productTypeMasterRepository.findAllByDeletedFalseAndProductTypeIdIn(productTypeId))
					.get();
			return productTypeMasters.stream().map(ProductTypeMaster::getProductType).collect(Collectors.toSet());
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + productFieldName);
		}
	}

	@Override
	public Map<String, Long> getProductTypeMap() {

		List<ProductTypeMaster> productTypeMasters = productTypeMasterRepository.findAllByDeletedFalse();
		return productTypeMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductType(), o -> o.getProductTypeId(), (existing, replacement) -> existing));
	}

	@Override
	public List<ProductMaster> getAllProductMasterWithProductType() {
		List<ProductTypeMaster> productTypeMasters = productTypeMasterRepository.findAllByDeletedFalse();
		
		Map<Long, String> productTypeMap = productTypeMasters.stream().collect(Collectors
				.toMap(o -> o.getProductTypeId(), o -> o.getProductType(), (existing, replacement) -> existing));

		List<ProductMaster> productMasterList = Optional.ofNullable(productMasterRepository.findAllByDeletedFalse())
				.get();
		
		return productMasterList.stream().map(o -> {
			o.setProductType(productTypeMap.get(o.getProductTypeId()));
			return o;
		}).collect(Collectors.toList());
	}
	
	

}
