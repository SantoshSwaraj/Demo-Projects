package aisaac.domain;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.domain.datatable.Order;
import aisaac.domain.datatable.Search;
import aisaac.dto.AdrEnitityScoreAuditTrail;
import aisaac.dto.AdrEntityScoreAuditTrailJsonResponse;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AuditTrailNewOldValueDto;
import aisaac.entities.AdrEntityScore;
import aisaac.entities.ThreatCopy;
import aisaac.payload.request.EntitiesTileListRequest;
import aisaac.payload.request.ThreatListRequest;
import aisaac.util.AppConstants;
import aisaac.util.AuditTrailLableConstants;
import jakarta.persistence.criteria.Predicate;

public class EBADomain {

	public static <T> Specification<T> getEbaEntitiesTileFilter(EntitiesTileListRequest request) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), request.getTenantId()));

			if (request.isIpSelected() && request.isHostNameSelected() && request.isCloudResourcIdSelected()) {
				// to ignore the condition if ip, hostname and cloud resource id selected
			} else if (request.isIpSelected() && request.isHostNameSelected()) {
				predicates.add(criteriaBuilder.notEqual(root.get("entityTypeId"),
						AppConstants.SYS_PARAM_VALUE_CLOUD_RESOURCE_ID_VALUE));
			} else if (request.isIpSelected() && request.isCloudResourcIdSelected()) {
				predicates.add(criteriaBuilder.notEqual(root.get("entityTypeId"),
						AppConstants.SYS_PARAM_VALUE_HOSTANAME_VALUE));
			} else if (request.isHostNameSelected() && request.isCloudResourcIdSelected()) {
				predicates
						.add(criteriaBuilder.notEqual(root.get("entityTypeId"), AppConstants.SYS_PARAM_VALUE_IP_VALUE));
			} else if (request.isIpSelected()) {
				predicates.add(criteriaBuilder.equal(root.get("entityTypeId"), AppConstants.SYS_PARAM_VALUE_IP_VALUE));
			} else if (request.isHostNameSelected()) {
				predicates.add(
						criteriaBuilder.equal(root.get("entityTypeId"), AppConstants.SYS_PARAM_VALUE_HOSTANAME_VALUE));
			} else if (request.isCloudResourcIdSelected()) {
				predicates.add(
						criteriaBuilder.equal(root.get("entityTypeId"), AppConstants.SYS_PARAM_VALUE_CLOUD_RESOURCE_ID_VALUE));
			}
			
			
			if (request.getSearchType() == (3)) {

				if (StringUtils.isNotBlank(request.getSearchEntityName())) {
					predicates.add(criteriaBuilder.equal(root.get("entityId"), request.getSearchEntityName()));
				}
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("entityScore"), request.getMinScore()));
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("entityScore"), request.getMaxScore()));
				if (Objects.nonNull(request.getSearchShow())) {
					predicates.add(criteriaBuilder.equal(root.get("watchlisted"), request.getSearchShow()));
				}
			} else if (request.getSearchType() == 1) {
				Map<Search, String> searchMap = request.getSearch();
				String searchTxt = "";
				if (searchMap != null && searchMap.size() > 0) {
					searchTxt = searchMap.get(Search.value);
				}
				if (StringUtils.isNotEmpty(searchTxt)) {
					predicates.add(criteriaBuilder.like(root.get("entityId"),
							AppConstants.PERCENTAGE + searchTxt + AppConstants.PERCENTAGE));
				}
			}
//			if (StringUtils.isNotBlank(request.getDateType()) && request.getDateType().equalsIgnoreCase("1")) {
//				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"),
//						LocalDateTime.parse(
//								LocalDateTime.now().minusHours(1).atZone(ZoneId.of("UTC")).toLocalDateTime()
//										.format(AppConstants.DATE_TIME_HOUR_FORMATTER),
//								AppConstants.DATE_TIME_HOUR_FORMATTER)));
//
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"),
//						LocalDateTime.parse(
//								LocalDateTime.now().atZone(ZoneId.of("UTC")).toLocalDateTime()
//										.format(AppConstants.DATE_TIME_HOUR_FORMATTER),
//								AppConstants.DATE_TIME_HOUR_FORMATTER)));
//				
//			} else if (StringUtils.isNotBlank(request.getDateType()) && request.getDateType().equalsIgnoreCase("2")) {
//				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"),
//						LocalDateTime.parse(
//								LocalDateTime.now().minusHours(2).atZone(ZoneId.of("UTC")).toLocalDateTime()
//										.format(AppConstants.DATE_TIME_HOUR_FORMATTER),
//								AppConstants.DATE_TIME_HOUR_FORMATTER)));
//
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"),
//						LocalDateTime.parse(
//								LocalDateTime.now().atZone(ZoneId.of("UTC")).toLocalDateTime()
//										.format(AppConstants.DATE_TIME_HOUR_FORMATTER),
//								AppConstants.DATE_TIME_HOUR_FORMATTER)));
//				
//			} else if (StringUtils.isNotBlank(request.getDateType()) && request.getDateType().equalsIgnoreCase("7")) {
//				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"),
//						LocalDateTime.parse(
//								LocalDateTime.now().minusHours(7).atZone(ZoneId.of("UTC")).toLocalDateTime()
//										.format(AppConstants.DATE_TIME_HOUR_FORMATTER),
//								AppConstants.DATE_TIME_HOUR_FORMATTER)));
//
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"),
//						LocalDateTime.parse(
//								LocalDateTime.now().atZone(ZoneId.of("UTC")).toLocalDateTime()
//										.format(AppConstants.DATE_TIME_HOUR_FORMATTER),
//								AppConstants.DATE_TIME_HOUR_FORMATTER)));
//				
//			} else if (StringUtils.isNotBlank(request.getDateType())
//					&& request.getDateType().equalsIgnoreCase("custom")) {
//				if (Objects.nonNull(request.getSearchFrom()) && Objects.nonNull(request.getSearchTo())) {
//					predicates.add(criteriaBuilder.between(root.get("createdDate"), request.getSearchFrom(),
//							request.getSearchTo()));
//				}
//			}

			String sortColumn = "", sortOrderBy = "";

			List<Map<Order, String>> orderList = request.getOrder();

			if (orderList != null && orderList.size() > 0) {
				Map<Order, String> orderMap = orderList.get(0);
				sortColumn = orderMap.get(Order.name);
				sortOrderBy = orderMap.get(Order.dir);
			}

			switch (sortOrderBy) {
			case AppConstants.ASC: {
				if (request.isWatchlistSelected()) {
					if (AppConstants.ENTITY_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder
										.asc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.asc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("updatedDate")));
					} else {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder.asc(root.get(sortColumn)));
					}
				} else {
					if (AppConstants.ENTITY_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(
								criteriaBuilder
										.asc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.asc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("updatedDate")));
					} else {
						query.orderBy(criteriaBuilder.asc(root.get(sortColumn)));
					}
				}

				break;
			}
			case AppConstants.DESC: {
				if (request.isWatchlistSelected()) {
					if (AppConstants.ENTITY_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder
										.desc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.desc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("updatedDate")));
					} else {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder.desc(root.get(sortColumn)));
					}
				} else {
					if (AppConstants.ENTITY_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(
								criteriaBuilder
										.desc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.desc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("updatedDate")));
					} else {
						query.orderBy(criteriaBuilder.desc(root.get(sortColumn)));
					}
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + sortOrderBy);
			}
//			query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),criteriaBuilder.desc(root.get("entityScore")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static String getAuditTrailJsonResponseByAdrEntities(String action, AdrEntityScore adrEntityScore,
			AuditTrailDetailsOnly auditTrailDetailsOnly,Long userId) {
		AdrEntityScoreAuditTrailJsonResponse detailsJson = new AdrEntityScoreAuditTrailJsonResponse();
		AdrEnitityScoreAuditTrail auditTrailDto = null;

		boolean isExist = false;
		if (Objects.nonNull(auditTrailDetailsOnly) && Objects.nonNull(auditTrailDetailsOnly.details())) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				detailsJson = mapper.readValue(auditTrailDetailsOnly.details(),
						AdrEntityScoreAuditTrailJsonResponse.class);
			} catch (JsonParseException | JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			auditTrailDto = detailsJson.getData();
		} else {
			isExist = true;
			auditTrailDto = new AdrEnitityScoreAuditTrail();
		}

		AuditTrailNewOldValueDto performedBy = Objects.nonNull(auditTrailDto.getPerformedBy())
				? auditTrailDto.getPerformedBy()
				: new AuditTrailNewOldValueDto();
		performedBy.setLabel(AuditTrailLableConstants.performedBy);
		if (Objects.nonNull(performedBy.getNewValue())) {
			performedBy.setOldValue(performedBy.getNewValue());
		}
		if (isExist && Objects.nonNull(userId)) {
			performedBy.setOldValue(String.valueOf(userId));
		}
		performedBy.setNewValue(String.valueOf(userId));
		if (Objects.nonNull(performedBy.getNewValue()))
			performedBy.setEdited(!performedBy.getNewValue().equals(performedBy.getOldValue()));
		else
			performedBy.setEdited(false);
		auditTrailDto.setPerformedBy(performedBy);

		AuditTrailNewOldValueDto performedOn = Objects.nonNull(auditTrailDto.getPerformedOn())
				? auditTrailDto.getPerformedOn()
				: new AuditTrailNewOldValueDto();
		performedOn.setLabel(AuditTrailLableConstants.performedOn);
		if (Objects.nonNull(performedOn.getNewValue())) {
			performedOn.setOldValue(performedOn.getNewValue());
		}
		if (isExist && Objects.nonNull(adrEntityScore)) {
			performedOn.setOldValue(String.valueOf(LocalDateTime.now().format(AuditTrailLableConstants.formatter)));
		}
		performedOn.setNewValue(String.valueOf(LocalDateTime.now().format(AuditTrailLableConstants.formatter)));
		if (Objects.nonNull(performedOn.getNewValue()))
			performedOn.setEdited(!performedOn.getNewValue().equals(performedOn.getOldValue()));
		else
			performedOn.setEdited(false);
		auditTrailDto.setPerformedOn(performedOn);

		detailsJson.setType(action);
		detailsJson.setData(auditTrailDto);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			return objectMapper.writeValueAsString(detailsJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static AdrEnitityScoreAuditTrail getAdrEnitityScoreAuditTrailFromAuditTrailJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, AdrEntityScoreAuditTrailJsonResponse.class).getData();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new AdrEnitityScoreAuditTrail();
	}
	
	public static Specification<ThreatCopy> getEbaThreatListSpecification(ThreatListRequest threatListRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			LocalDateTime yesterdayDate = LocalDateTime.now().minusDays(1);
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), yesterdayDate.atZone(ZoneId.of("UTC"))
					.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventTime"), LocalDateTime.now()
					.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), threatListRequest.getTenantId()));
			
			List<Predicate> subPredicats=new ArrayList<>();
			subPredicats.add(criteriaBuilder.equal(root.get("srcAdrEntityRecId"), threatListRequest.getAdrEntityRecId()));
			subPredicats.add(criteriaBuilder.equal(root.get("destAdrEntityRecId"), threatListRequest.getAdrEntityRecId()));
			
			predicates.add(criteriaBuilder.or(subPredicats.toArray(new Predicate[0])));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static String getEntityScoreTextByThreatUserScore(Float entityScore) {

		if (Objects.isNull(entityScore)) {
			return StringUtils.EMPTY;
		}
		if (entityScore <= 25) {
			return AppConstants.LOW;
		} else if (entityScore > 25 && entityScore <= 50) {
			return AppConstants.MEDIUM;
		} else if (entityScore > 50 && entityScore <= 75) {
			return AppConstants.HIGH;
		} else {
			return AppConstants.CRITICAL;
		}
	}
}
