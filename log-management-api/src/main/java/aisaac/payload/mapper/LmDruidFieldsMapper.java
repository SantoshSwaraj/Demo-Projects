package aisaac.payload.mapper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import aisaac.dto.LmDruidFieldsDTO;
import aisaac.model.LmDruidFields;

public class LmDruidFieldsMapper {
	
	public static LmDruidFieldsDTO map(LmDruidFields model) {
		return LmDruidFieldsDTO.builder()
				.fieldId(model.getFieldId())
				.field(model.getField())
				.datasourceType(model.getDatasourceType())
				.dataType(model.getDataType())
				.displayName(model.getDisplayName())
				.threatColumnName(model.getThreatColumnName())
				.displaySequenceNumber(model.getDisplaySequenceNumber())
				.isDeleted(model.getIsDeleted())
				.allowSummaryStats(model.getAllowSummaryStats())
				.createdDate(model.getCreatedDate())
				.updatedDate(model.getUpdatedDate())
				.build();
	}
	
	public static List<LmDruidFieldsDTO> map(List<LmDruidFields> models) {
		if(CollectionUtils.isEmpty(models))
			return new LinkedList<>();
		
		return models.stream().map(LmDruidFieldsMapper::map).collect(Collectors.toList());
	}

}
