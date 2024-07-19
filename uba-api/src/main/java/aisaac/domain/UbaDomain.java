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
import aisaac.dto.AdUserDetailAuditTrail;
import aisaac.dto.AdUserDetailsAuditTrailJsonResponse;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AuditTrailNewOldValueDto;
import aisaac.entities.AdUserDetail;
import aisaac.entities.Threat;
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.UserTileListRequest;
import aisaac.util.AppConstants;
import aisaac.util.AuditTrailLableConstants;
import jakarta.persistence.criteria.Predicate;

public class UbaDomain {

	public static String getAuditTrailJsonResponseByAdUserDetail(String action, AdUserDetail adUserDetails,
			AuditTrailDetailsOnly auditTrailDetailsOnly,Long userId) {
		AdUserDetailsAuditTrailJsonResponse detailsJson = new AdUserDetailsAuditTrailJsonResponse();
		AdUserDetailAuditTrail auditTrailDto = null;

		boolean isExist = false;
		if (Objects.nonNull(auditTrailDetailsOnly) && Objects.nonNull(auditTrailDetailsOnly.details())) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				detailsJson = mapper.readValue(auditTrailDetailsOnly.details(),
						AdUserDetailsAuditTrailJsonResponse.class);
			} catch (JsonParseException | JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			auditTrailDto = detailsJson.getData();
		} else {
			isExist = true;
			auditTrailDto = new AdUserDetailAuditTrail();
		}

		AuditTrailNewOldValueDto tenantId = Objects.nonNull(auditTrailDto.getTenantId()) ? auditTrailDto.getTenantId()
				: new AuditTrailNewOldValueDto();
		tenantId.setLabel(AuditTrailLableConstants.tenantId);
		if (Objects.nonNull(tenantId.getNewValue())) {
			tenantId.setOldValue(tenantId.getNewValue());
		}
		if (isExist && Objects.nonNull(adUserDetails)) {
			tenantId.setOldValue(String.valueOf(adUserDetails.getTenantId()));
		}
		tenantId.setNewValue(String.valueOf(adUserDetails.getTenantId()));
		if (Objects.nonNull(tenantId.getNewValue()))
			tenantId.setEdited(!tenantId.getNewValue().equals(tenantId.getOldValue()));
		else
			tenantId.setEdited(false);
		auditTrailDto.setTenantId(tenantId);

		AuditTrailNewOldValueDto performedBy = Objects.nonNull(auditTrailDto.getPerformedBy())
				? auditTrailDto.getPerformedBy()
				: new AuditTrailNewOldValueDto();
		performedBy.setLabel(AuditTrailLableConstants.performedBy);
		if (Objects.nonNull(performedBy.getNewValue())) {
			performedBy.setOldValue(performedBy.getNewValue());
		}
		if (isExist && Objects.nonNull(adUserDetails)) {
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
		if (isExist && Objects.nonNull(adUserDetails)) {
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

	public static AdUserDetailAuditTrail getAdUserDetailAuditTrailFromAuditTrailJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(json, AdUserDetailsAuditTrailJsonResponse.class).getData();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new AdUserDetailAuditTrail();
	}

	public static <T> Specification<T> getUbaUserTileFilter(UserTileListRequest request) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			
			if (StringUtils.isNotBlank(request.getDateType()) && request.getDateType().equalsIgnoreCase("1")) {
				LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"), oneHourAgo.atZone(ZoneId.of("UTC"))
						.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), LocalDateTime.now()
						.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			} else if (StringUtils.isNotBlank(request.getDateType()) && request.getDateType().equalsIgnoreCase("2")) {
				LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(2);
				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"), oneHourAgo.atZone(ZoneId.of("UTC"))
						.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), LocalDateTime.now()
						.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			} else if (StringUtils.isNotBlank(request.getDateType()) && request.getDateType().equalsIgnoreCase("7")) {
				LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(7);
				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"), oneHourAgo.atZone(ZoneId.of("UTC"))
						.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), LocalDateTime.now()
						.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			} else if (StringUtils.isNotBlank(request.getDateType())
					&& request.getDateType().equalsIgnoreCase("custom")) {
				if (Objects.nonNull(request.getSearchFrom()) && Objects.nonNull(request.getSearchTo())) {
					predicates.add(criteriaBuilder.between(root.get("createdDate"), request.getSearchFrom(),
							request.getSearchTo()));
				}
			}
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), request.getTenantId()));
			
			if (request.getSearchType() == (3)) {

				if (StringUtils.isNotBlank(request.getSearchUserName())) {
					predicates.add(criteriaBuilder.equal(root.get("accountName"), request.getSearchUserName()));
				}
				if (StringUtils.isNotBlank(request.getSearchSource())&&!request.getSearchSource().equalsIgnoreCase("all")) {
					predicates.add(criteriaBuilder.equal(root.get("sources"), request.getSearchSource()));
				}
				if (StringUtils.isNotBlank(request.getSearchDepartment())) {
					predicates.add(criteriaBuilder.equal(root.get("departmentName"), request.getSearchDepartment()));
				}
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("score"), request.getMinScore()));
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("score"), request.getMaxScore()));
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
					predicates.add(criteriaBuilder.like(root.get("accountName"),
							AppConstants.PERCENTAGE + searchTxt + AppConstants.PERCENTAGE));
				}
			}
				
			query.groupBy(root.get("accountName"));
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
					if (AppConstants.USER_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder
										.asc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.asc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("createdDate")));
					} else {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder.asc(root.get(sortColumn)));
					}
				} else {
					if (AppConstants.USER_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(
								criteriaBuilder
										.asc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.asc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("createdDate")));
					} else {
						query.orderBy(criteriaBuilder.asc(root.get(sortColumn)));
					}
				}

				break;
			}
			case AppConstants.DESC: {
				if (request.isWatchlistSelected()) {
					if (AppConstants.USER_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder
										.desc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.desc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("createdDate")));
					} else {
						query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),
								criteriaBuilder.desc(root.get(sortColumn)));
					}
				} else {
					if (AppConstants.USER_SCORE_DIFF.equalsIgnoreCase(sortColumn)) {
						query.orderBy(
								criteriaBuilder
										.desc(criteriaBuilder.function("SIGN", Integer.class, root.get(sortColumn))),
								criteriaBuilder
										.desc(criteriaBuilder.function("ABS", Integer.class, root.get(sortColumn))),
								criteriaBuilder.desc(root.get("createdDate")));
					} else {
						query.orderBy(criteriaBuilder.desc(root.get(sortColumn)));
					}
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + sortOrderBy);
			}
//			query.orderBy(criteriaBuilder.desc(root.get("watchlisted")),criteriaBuilder.desc(root.get("score")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static Specification<Threat> getUbaThreatListSpecification(ThreatListRequest threatListRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			LocalDateTime yesterdayDate = LocalDateTime.now().minusDays(1);
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), yesterdayDate
					.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventTime"), LocalDateTime.now()
					.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

			predicates.add(criteriaBuilder
					.not(root.get("threatStageSysparam").in(AppConstants.SYSPARAM_THREAT_STAGE_DROPPED_ID)));
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), threatListRequest.getTenantId()));

			predicates.add(criteriaBuilder.equal(root.get("adUserId"), threatListRequest.getAdUserId()));
			query.orderBy(criteriaBuilder.desc(root.get("eventTime")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static String getUserScoreTextByThreatUserScore(Float userScore) {
		if(Objects.isNull(userScore)) {
			return StringUtils.EMPTY;
		}
		if(userScore<=25) {
			return AppConstants.LOW;
		}else if(userScore>25 && userScore <= 50) {
			return AppConstants.MEDIUM;
		}else if(userScore >50 && userScore <=75){
			return AppConstants.HIGH;
		}else {
			return AppConstants.CRITICAL;
		}
	}
	
	public static <T> Specification<T> getRiskScoreDonutFilter(Long tenantId) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
			predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"), oneHourAgo.atZone(ZoneId.of("UTC"))
					.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), LocalDateTime.now()
					.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));

			query.groupBy(root.get("accountName"));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static <T> Specification<T> getWatchListScoreFilter(Long tenantId) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
			predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"), oneHourAgo.atZone(ZoneId.of("UTC"))
					.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), LocalDateTime.now()
					.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			predicates.add(criteriaBuilder.equal(root.get("watchlisted"), true));
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));

			query.groupBy(root.get("accountName"));
			query.orderBy(criteriaBuilder.desc(root.get("score")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static <T> Specification<T> getRiskUsersFilter(Long tenantId) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
			predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"), oneHourAgo.atZone(ZoneId.of("UTC"))
					.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));

			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), LocalDateTime.now()
					.atZone(ZoneId.of("UTC")).toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER)));
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.greaterThan(root.get("scoreDiff"), 0));

			query.groupBy(root.get("accountName"));
			query.orderBy(criteriaBuilder.desc(root.get("scoreDiff")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
