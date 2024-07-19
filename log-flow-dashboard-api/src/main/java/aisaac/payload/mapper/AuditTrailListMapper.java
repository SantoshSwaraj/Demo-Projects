package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.dto.AuditTrailsListDto;
import aisaac.entities.AuditTrail;
import aisaac.payload.response.AuditTrailListResponse;
import aisaac.util.LogFlowDashboardUtils;

public class AuditTrailListMapper {

	public static List<Object> map(List<AuditTrail> auditTrailList, Map<Long, String> userIdMap,
			Map<Long, String> auditTrailMasterIdMap) {

		ObjectMapper objectMapper = new ObjectMapper();

		return auditTrailList.stream().map(o -> {

			AuditTrailsListDto auditTrailsListDto = new AuditTrailsListDto();
			AuditTrailListResponse auditTrailListResponse = new AuditTrailListResponse();
			try {
				JsonNode details = objectMapper.readTree(o.getDetails());

				if (Objects.nonNull(details.get("data")))
					auditTrailsListDto = objectMapper.readValue(details.get("data").toString(),
							AuditTrailsListDto.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (Objects.nonNull(auditTrailsListDto)) {
				auditTrailListResponse.setOrganization(auditTrailsListDto.getCustomerName().getNewValue());
				auditTrailListResponse.setProductVendor(auditTrailsListDto.getDeviceVendor().getNewValue());
				auditTrailListResponse.setProductName(auditTrailsListDto.getDeviceProduct().getNewValue());
			}
			auditTrailListResponse.setDetails(o.getDetails());
			auditTrailListResponse.setActionDesc(auditTrailMasterIdMap.get(o.getAuditTrailMasterId()));
			auditTrailListResponse.setCreatorDisplayName(userIdMap.get(o.getCreatedBy()));
			auditTrailListResponse
					.setCreatedDateTime(LogFlowDashboardUtils.getLocalDateTimeInMilliSec(o.getCreatedDate()));
			auditTrailListResponse.setRecId(o.getRecId());
			return auditTrailListResponse;
		}).collect(Collectors.toList());
	}
	
}
