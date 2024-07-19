package aisaac.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisaac.dao.LmDruidFieldsRepository;
import aisaac.dao.LmUserFieldCustomizationRepository;
import aisaac.dao.UserRepository;
import aisaac.dto.LmDruidFieldsDTO;
import aisaac.exception.ValidationException;
import aisaac.model.LmDruidFields;
import aisaac.model.LmUserFieldCustomization;
import aisaac.model.User;
import aisaac.payload.mapper.LmDruidFieldsMapper;
import aisaac.utils.LMConstants;

@Service
@Transactional
public class LmUserFieldCustomizationServiceImpl implements LmUserFieldCustomizationService {
	
	private LmUserFieldCustomizationRepository lmUserFieldCustomizationRepo;
	private LmDruidFieldsRepository lmDruidFieldsRepository;
	private UserRepository userRepository; 
	
	public LmUserFieldCustomizationServiceImpl(
			LmUserFieldCustomizationRepository lmUserFieldCustomizationRepo,
			LmDruidFieldsRepository lmDruidFieldsRepository,
			UserRepository userRepository) {
		this.lmUserFieldCustomizationRepo = lmUserFieldCustomizationRepo;
		this.lmDruidFieldsRepository = lmDruidFieldsRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public List<LmDruidFieldsDTO> getLmUserFieldsCustomizationMainTable(Integer userId, String datasourceType) {
		List<LmDruidFieldsDTO> list = new LinkedList<>();
		
		User user = this.userRepository.findById(userId).orElse(null);
	
		if(Objects.isNull(user))
			user = this.userRepository.findById(LMConstants.DEFAULT_USER_ID)
						.orElseThrow(()-> new ValidationException(
								String.format("Default User having userId %d not present. Please check with admin",
										LMConstants.DEFAULT_USER_ID)));
		
		List<LmUserFieldCustomization> userCustomize = this.lmUserFieldCustomizationRepo
									.findByUserAndDatasourceTypeAndCustomizeType(
											user, datasourceType,
												LMConstants.LOG_MANAGEMENT_CUSTOMIZATION_TYPE_DATATABLE_CUSTOMIZE);
		
		if(CollectionUtils.isEmpty(userCustomize))
			return list;
		
		List<Integer> lmUserFieldsList = userCustomize.stream()
												.map(LmUserFieldCustomization::getLmDruidFields)
												.map(LmDruidFields::getFieldId)
												.toList();
		
		List<LmDruidFields> totaldruidFieldsForMostUsedModels = lmDruidFieldsRepository.getLmDruidFields(LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS, true);
		List<LmDruidFieldsDTO> totaldruidFieldsForMostUsed = LmDruidFieldsMapper.map(totaldruidFieldsForMostUsedModels);
		
		for(LmDruidFieldsDTO druidFieldDto : totaldruidFieldsForMostUsed) {
			if(lmUserFieldsList.contains(druidFieldDto.getFieldId())){
				druidFieldDto.setChecked(true);
			}else {
				druidFieldDto.setChecked(false);
			}
		}
			
		return totaldruidFieldsForMostUsed;
	}

	@Override
	public List<LmDruidFieldsDTO> saveCustomizeColumnsForMainTable(
			Integer userId, String[] fields, String datasourceType) {
		
		User user = this.userRepository.findById(userId)
						.orElseThrow(()-> new ValidationException(String.format("User not exist for userId %d", userId)));
		
		this.lmUserFieldCustomizationRepo.deleteByUserAndDatasourceTypeAndCustomizeType(user,
				datasourceType, LMConstants.LOG_MANAGEMENT_CUSTOMIZATION_TYPE_DATATABLE_CUSTOMIZE);
		
		for(String field :fields) {
			
			Integer fieldId = lmDruidFieldsRepository.getLmDruidFieldId(LMConstants.LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS, field);
			LmUserFieldCustomization lmUserFieldCustom= new LmUserFieldCustomization();
			lmUserFieldCustom.setCustomizeType(LMConstants.LOG_MANAGEMENT_CUSTOMIZATION_TYPE_DATATABLE_CUSTOMIZE);
			lmUserFieldCustom.setCreatedDate(new Date());
			lmUserFieldCustom.setDatasourceType(datasourceType);
			LmDruidFields lmDruidFields = new LmDruidFields();
			lmDruidFields.setFieldId(fieldId);		
			lmUserFieldCustom.setLmDruidFields(lmDruidFields);
			lmUserFieldCustom.setUser(user);
			
			lmUserFieldCustomizationRepo.save(lmUserFieldCustom);
		}
		
		return getLmUserFieldsCustomizationMainTable(user.getUserId(), datasourceType);
	}

}
