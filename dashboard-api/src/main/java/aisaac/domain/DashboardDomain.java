package aisaac.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import aisaac.dto.DashBoardCountDto;
import aisaac.entities.AgeOfTicketModel;
import aisaac.entities.AssetMaster;
import aisaac.entities.AvgResponseTimeForTicketCategoryModel;
import aisaac.entities.AvgResponseTimeModel;
import aisaac.entities.HourlyThreatCount;
import aisaac.entities.InvestigationSave;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.ThreatLevelStatsCountrySrcipHour;
import aisaac.entities.Ticket;
import aisaac.entities.TicketAssetMapping;
import aisaac.entities.TicketWithOutFormula;
import aisaac.payload.request.CountryMapAlertRequest;
import aisaac.payload.request.DashboardCountRequest;
import aisaac.util.AppConstants;
import aisaac.util.DashboardUtils;
import jakarta.persistence.criteria.Predicate;

public class DashboardDomain {

	public static Specification<Ticket> getTicketCountFilterSpecification(DashboardCountRequest countRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			List<Predicate> createdDatepredicates = new ArrayList<>();
			List<Predicate> reopenedDatepredicates = new ArrayList<>();
			List<Predicate> closedDatepredicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));
				
				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));
				
				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days7Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days7Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), days7Ago));

				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"), now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days15Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days15Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), days15Ago));

				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"), now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days30Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days30Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), days30Ago));

				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"), now));

				break;
			}
			case "custom": {

				createdDatepredicates
						.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), countRequest.getFromDate()));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), countRequest.getToDate()));

				reopenedDatepredicates.add(
						criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), countRequest.getFromDate()));

				reopenedDatepredicates
						.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), countRequest.getToDate()));

				closedDatepredicates
						.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), countRequest.getFromDate()));

				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"), countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}
			List<Predicate> orPredicates = new ArrayList<>();
			orPredicates.add(criteriaBuilder.and(createdDatepredicates.toArray(new Predicate[0])));
			orPredicates.add(criteriaBuilder.and(reopenedDatepredicates.toArray(new Predicate[0])));
			orPredicates.add(criteriaBuilder.and(closedDatepredicates.toArray(new Predicate[0])));
			
			predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.notEqual(root.get("status"), AppConstants.TICKET_STATUS));
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}

	public static Specification<Ticket> getTicketDeltaCountFilterSpecification(DashboardCountRequest countRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			List<Predicate> createdDatepredicates = new ArrayList<>();
			List<Predicate> reopenedDatepredicates = new ArrayList<>();
			List<Predicate> closedDatepredicates = new ArrayList<>();
			
			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hours48Ago = LocalDateTime.now().minusHours(48);
				LocalDateTime hours24Ago = LocalDateTime.now().minusHours(24);
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						LocalDateTime.of(hours48Ago.getYear(), hours48Ago.getMonth(), hours48Ago.getDayOfMonth(),
								hours48Ago.getHour(), 0, 0)));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), LocalDateTime.of(hours24Ago.getYear(),
						hours24Ago.getMonth(), hours24Ago.getDayOfMonth(), hours24Ago.getHour(), 0, 0)));
				
				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						LocalDateTime.of(hours48Ago.getYear(), hours48Ago.getMonth(), hours48Ago.getDayOfMonth(),
								hours48Ago.getHour(), 0, 0)));
				
				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), LocalDateTime.of(hours24Ago.getYear(),
						hours24Ago.getMonth(), hours24Ago.getDayOfMonth(), hours24Ago.getHour(), 0, 0)));
				
				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						LocalDateTime.of(hours48Ago.getYear(), hours48Ago.getMonth(), hours48Ago.getDayOfMonth(),
								hours48Ago.getHour(), 0, 0)));
				
				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"), LocalDateTime.of(hours24Ago.getYear(),
						hours24Ago.getMonth(), hours24Ago.getDayOfMonth(), hours24Ago.getHour(), 0, 0)));
				
				break;
			}
			case "7": {

				LocalDateTime days14Ago = LocalDateTime.now().minusDays(14);
				days14Ago = LocalDateTime.of(days14Ago.getYear(), days14Ago.getMonth(), days14Ago.getDayOfMonth(),
						days14Ago.getHour(), 0, 0);
				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						days14Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
						days7Ago));
				
				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						days14Ago));
				
				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),
						days7Ago));
				
				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						days14Ago));
				
				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						days7Ago));
				break;
			}
			case "15": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						days30Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
						days15Ago));
				
				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						days30Ago));
				
				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),
						days15Ago));
				
				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						days30Ago));
				
				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						days15Ago));
				
				break;
			}
			case "30": {

				LocalDateTime days60Ago = LocalDateTime.now().minusDays(60);
				days60Ago = LocalDateTime.of(days60Ago.getYear(), days60Ago.getMonth(), days60Ago.getDayOfMonth(),
						days60Ago.getHour(), 0, 0);
				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						days60Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
						days30Ago));
				
				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						days60Ago));
				
				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),
						days30Ago));
				
				closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						days60Ago));
				
				closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						days30Ago));
				break;
			}
			case "custom": {
				if (Objects.nonNull(countRequest.getFromDate()) && Objects.nonNull(countRequest.getToDate())) {
					Long days = DashboardUtils.calculateDaysGapByLocalDateTime(countRequest.getFromDate(),
							countRequest.getToDate());
					if (days == 0L)
						days = 1L;
					LocalDateTime daysAgo = LocalDateTime.now().minusDays(days);
					createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), daysAgo));

					createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), countRequest.getFromDate()));
					
					reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), daysAgo));
					
					reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), countRequest.getFromDate()));
					
					closedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), daysAgo));
					
					closedDatepredicates.add(criteriaBuilder.lessThan(root.get("closedDate"), countRequest.getFromDate()));
					
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}
			List<Predicate> orPredicates = new ArrayList<>();
			orPredicates.add(criteriaBuilder.and(createdDatepredicates.toArray(new Predicate[0])));
			orPredicates.add(criteriaBuilder.and(reopenedDatepredicates.toArray(new Predicate[0])));
			orPredicates.add(criteriaBuilder.and(closedDatepredicates.toArray(new Predicate[0])));
			
			predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.notEqual(root.get("status"), AppConstants.TICKET_STATUS));
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<LogStopageMaster> getAssetsByProductNamesFilter(Long tenantId) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.equal(root.get("deleted"), AppConstants.FALSE));
			predicates.add(criteriaBuilder.equal(root.get("disabled"), AppConstants.FALSE));
			predicates.add(criteriaBuilder.equal(root.get("suppressed"), AppConstants.FALSE));
			
			query.groupBy(root.get("productId")).orderBy(criteriaBuilder.desc(root.get("count")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static Specification<TicketWithOutFormula> getOpenAndReopenedTicketCountFilterSpecification(DashboardCountRequest countRequest,
			boolean isItReopened) {
		return (root, query, criteriaBuilder) -> {
			String dateColumnName = "createdDate";
			if (isItReopened)
				dateColumnName = "reOpenedDate";

			List<Predicate> predicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateColumnName), LocalDateTime.of(
						hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0)));

				predicates.add(criteriaBuilder.lessThan(root.get(dateColumnName),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateColumnName), days7Ago));

				predicates.add(criteriaBuilder.lessThan(root.get(dateColumnName), now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateColumnName),
						days15Ago));

				predicates.add(criteriaBuilder.lessThan(root.get(dateColumnName),
						now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateColumnName),
						days30Ago));

				predicates.add(criteriaBuilder.lessThan(root.get(dateColumnName),
						now));

				break;
			}
			case "custom": {

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateColumnName),
						countRequest.getFromDate()));

				predicates.add(criteriaBuilder.lessThan(root.get(dateColumnName),
						countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_70));

			if (isItReopened)
				predicates.add(criteriaBuilder.isNotNull(root.get("reOpenedDate")));
			else
				predicates.add(criteriaBuilder.isNull(root.get("reOpenedDate")));
			
			query.groupBy(root.get("priority"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<TicketWithOutFormula> getTicketByCategoryFilterSpecification(DashboardCountRequest countRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			List<Predicate> createdDatepredicates = new ArrayList<>();
			List<Predicate> reopenedDatepredicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days7Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days7Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days15Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days15Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days30Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days30Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				break;
			}
			case "custom": {

				createdDatepredicates
						.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), countRequest.getFromDate()));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),

						countRequest.getToDate()));

				reopenedDatepredicates.add(
						criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), countRequest.getFromDate()));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),

						countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}
			List<Predicate> orPredicates = new ArrayList<>();
			orPredicates.add(criteriaBuilder.and(createdDatepredicates.toArray(new Predicate[0])));
			orPredicates.add(criteriaBuilder.and(reopenedDatepredicates.toArray(new Predicate[0])));

			predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_70));

			query.groupBy(root.get("category"), root.get("priority"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<Ticket> getTicketListSpecification(DashboardCountRequest countRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			List<Predicate> createdDatepredicates = new ArrayList<>();
			List<Predicate> reopenedDatepredicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();
				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"),
						LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(),
								hoursAgo.getHour(), 0, 0)));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days7Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days7Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days15Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days15Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				createdDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), days30Ago));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), now));

				reopenedDatepredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), days30Ago));

				reopenedDatepredicates.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), now));

				break;
			}
			case "custom": {

				createdDatepredicates
						.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), countRequest.getFromDate()));

				createdDatepredicates.add(criteriaBuilder.lessThan(root.get("createdDate"), countRequest.getToDate()));

				reopenedDatepredicates.add(
						criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), countRequest.getFromDate()));

				reopenedDatepredicates
						.add(criteriaBuilder.lessThan(root.get("reOpenedDate"), countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}
			List<Predicate> orPredicates = new ArrayList<>();
			orPredicates.add(criteriaBuilder.and(createdDatepredicates.toArray(new Predicate[0])));
			orPredicates.add(criteriaBuilder.and(reopenedDatepredicates.toArray(new Predicate[0])));
			predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_70));
			predicates.add(criteriaBuilder.notEqual(root.get("priority"), AppConstants.TICKET_PRIORITY_69));

			query.orderBy(criteriaBuilder.desc(root.get("createdDate")), criteriaBuilder.desc(root.get("reOpenedDate")),
					criteriaBuilder.asc(root.get("priority")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	public static DashBoardCountDto getTicketListRequestDto(DashboardCountRequest countRequest) {
		DashBoardCountDto dashBoardCountDto = new DashBoardCountDto();

		switch (countRequest.getDateType()) {
		case "1": {

			LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
			LocalDateTime now = LocalDateTime.now();
			
			dashBoardCountDto.setFromDate(LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(),
					hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0));

			dashBoardCountDto.setToDate(
					LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0));

			break;
		}
		case "7": {

			LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
			days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
					days7Ago.getHour(), 0, 0);
			LocalDateTime now = LocalDateTime.now();
			now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

			dashBoardCountDto.setFromDate(days7Ago);
			dashBoardCountDto.setToDate(now);

			break;
		}
		case "15": {

			LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
			days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
					days15Ago.getHour(), 0, 0);
			LocalDateTime now = LocalDateTime.now();
			now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
					now.getHour(), 0, 0);
			
			dashBoardCountDto.setFromDate(days15Ago);
			dashBoardCountDto.setToDate(now);
			break;
		}
		case "30": {
			LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
			days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
					days30Ago.getHour(), 0, 0);
			LocalDateTime now = LocalDateTime.now();
			now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
					now.getHour(), 0, 0);
			
			dashBoardCountDto.setFromDate(days30Ago);
			dashBoardCountDto.setToDate(now);

			break;
		}
		case "custom": {
			dashBoardCountDto.setFromDate(countRequest.getFromDate());
			dashBoardCountDto.setToDate(countRequest.getToDate());

			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
		}
		return dashBoardCountDto;
	}
	
	public static Specification<AssetMaster> getAssetsByCriticalityFilter(Long tenantId,List<Long> assetIdList) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.in(root.get("assetId")).value(assetIdList));
			predicates.add(criteriaBuilder.equal(root.get("isDeleted"), AppConstants.FALSE));
			predicates.add(
					criteriaBuilder.equal(root.get("assetStatus"), AppConstants.ASSET_MASTER_ASSET_STATE_SYSPARAM));

			query.groupBy(root.get("criticality"));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static Specification<AgeOfTicketModel> getAgeOfTicketsFilter(Long tenantId,Long maxSearchDays) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			LocalDateTime maxSearchDateTime = LocalDateTime.now().minusDays(maxSearchDays).truncatedTo(ChronoUnit.DAYS);
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), maxSearchDateTime));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_70));

			LocalDateTime dayDifferences1 = LocalDateTime.now().minusDays(1);
			dayDifferences1 = LocalDateTime.of(dayDifferences1.getYear(), dayDifferences1.getMonth(),
					dayDifferences1.getDayOfMonth(), dayDifferences1.getHour(), 0, 0);
			LocalDateTime dayDifferences7 = LocalDateTime.now().minusDays(7);
			dayDifferences7 = LocalDateTime.of(dayDifferences7.getYear(), dayDifferences7.getMonth(),
					dayDifferences7.getDayOfMonth(), dayDifferences7.getHour(), 0, 0);
			LocalDateTime dayDifferences15 = LocalDateTime.now().minusDays(15);
			dayDifferences15 = LocalDateTime.of(dayDifferences15.getYear(), dayDifferences15.getMonth(),
					dayDifferences15.getDayOfMonth(), dayDifferences15.getHour(), 0, 0);
			LocalDateTime dayDifferences30 = LocalDateTime.now().minusDays(30);
			dayDifferences30 = LocalDateTime.of(dayDifferences30.getYear(), dayDifferences30.getMonth(),
					dayDifferences30.getDayOfMonth(), dayDifferences30.getHour(), 0, 0);
			
			Predicate case1 = criteriaBuilder.or(
					criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences1),
							criteriaBuilder.isNull(root.get("reOpenedDate"))),
					criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), dayDifferences1));
			
			Predicate case2 = criteriaBuilder.or(
					criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences7),
							criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences1),
							criteriaBuilder.isNull(root.get("reOpenedDate"))),
					criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), dayDifferences7),
							criteriaBuilder.lessThan(root.get("reOpenedDate"), dayDifferences1)));
			
			Predicate case3 = criteriaBuilder.or(
					criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences15),
							criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences7),
							criteriaBuilder.isNull(root.get("reOpenedDate"))),
					criteriaBuilder.and(
							criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), dayDifferences15),
							criteriaBuilder.lessThan(root.get("reOpenedDate"), dayDifferences7)));

			Predicate case4 = criteriaBuilder.or(
					criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences30),
							criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences15),
							criteriaBuilder.isNull(root.get("reOpenedDate"))),
					criteriaBuilder.and(
							criteriaBuilder.greaterThanOrEqualTo(root.get("reOpenedDate"), dayDifferences30),
							criteriaBuilder.lessThan(root.get("reOpenedDate"), dayDifferences15)));
			
			Predicate case5 = criteriaBuilder.or(
					criteriaBuilder.and(criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences30),
							criteriaBuilder.isNull(root.get("reOpenedDate"))),
					criteriaBuilder.lessThan(root.get("reOpenedDate"), dayDifferences30));

			query.groupBy(criteriaBuilder.selectCase()
					.when(case1, AppConstants.HOURS_DIFFERENCE_0_TO_24)
					.when(case2, AppConstants.DAYS_DIFFERENCE_2_TO_7)
					.when(case3, AppConstants.DAYS_DIFFERENCE_8_TO_15)
					.when(case4, AppConstants.DAYS_DIFFERENCE_16_TO_30)
					.when(case5, AppConstants.DAYS_DIFFERENCE_30),
					root.get("priority"));
			
//			query.groupBy(criteriaBuilder.selectCase()
//					.when(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences1),
//							AppConstants.HOURS_DIFFERENCE_0_TO_24)
//					.when(criteriaBuilder.and(
//							criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences7),
//							criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences1)),
//							AppConstants.DAYS_DIFFERENCE_2_TO_7)
//					.when(criteriaBuilder.and(
//							criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences15),
//							criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences7)),
//							AppConstants.DAYS_DIFFERENCE_8_TO_15)
//					.when(criteriaBuilder.and(
//							criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), dayDifferences30),
//							criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences15)),
//							AppConstants.DAYS_DIFFERENCE_16_TO_30)
//					.when(criteriaBuilder.lessThan(root.get("createdDate"), dayDifferences30),
//							AppConstants.DAYS_DIFFERENCE_30),
//					root.get("priority"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<TicketWithOutFormula> getClosedTicketsFilterSpecification(DashboardCountRequest countRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), LocalDateTime.of(
						hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0)));

				predicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						days7Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						days15Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						days30Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						now));

				break;
			}
			case "custom": {

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"),
						countRequest.getFromDate()));

				predicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
						countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_71));

			List<Predicate> orConditionPredicates = new ArrayList<>();
			orConditionPredicates.add(criteriaBuilder.equal(root.get("incident"), true));
			orConditionPredicates.add(criteriaBuilder.equal(root.get("relevant"), true));
			orConditionPredicates.add(criteriaBuilder.equal(root.get("whitelist"), true));
			orConditionPredicates.add(criteriaBuilder.equal(root.get("info"), true));
			
			predicates.add(criteriaBuilder.or(orConditionPredicates.toArray(new Predicate[0])));
			
			query.groupBy(root.get("incident"),root.get("relevant"),root.get("whitelist"),root.get("info"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<HourlyThreatCount> getThreatCountByProductNameFilterSpecification(DashboardCountRequest countRequest) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalDate"), LocalDateTime.of(
						hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0)));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalDate"), now));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalDate"), days7Ago));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalDate"), now));

				break;
			}
			case "15": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalDate"), days7Ago));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalDate"), LocalDateTime.now()));

				break;
			}
			case "30": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalDate"), days7Ago));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalDate"), LocalDateTime.now()));

				break;
			}
			case "custom": {
				Long days = DashboardUtils.calculateDaysGapByLocalDateTime(countRequest.getFromDate(),
						LocalDateTime.now());
				if (days > 7) {

					LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
					days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
							days7Ago.getHour(), 0, 0);
					predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalDate"), days7Ago));

					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalDate"), LocalDateTime.now()));

					break;
				} else {
					predicates.add(
							criteriaBuilder.greaterThanOrEqualTo(root.get("intervalDate"), countRequest.getFromDate()));

					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalDate"), countRequest.getToDate()));
				}

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			
			query.groupBy(root.get("productId")).orderBy(criteriaBuilder.desc(root.get("sumCount")));
			
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<TicketAssetMapping> getAssetByTicketPriorityFilterSpecification(
			DashboardCountRequest countRequest, List<Long> assetIdList) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"), LocalDateTime.of(
						hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0)));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"),
						days7Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),
						now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"),
						days15Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),
						now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"),
						days30Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),
						now));

				break;
			}
			case "custom": {

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"),
						countRequest.getFromDate()));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),
						countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}
			
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.in(root.get("assetId")).value(assetIdList));

			query.groupBy(root.get("priority"));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static DashBoardCountDto getThreatCountFilterDto(DashboardCountRequest countRequest) {
		DashBoardCountDto dashBoardCountDto = new DashBoardCountDto();
		switch (countRequest.getDateType()) {
		case "1": {

			LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
			LocalDateTime now = LocalDateTime.now();

			dashBoardCountDto.setFromDate(LocalDateTime.of(hoursAgo.getYear(), hoursAgo.getMonth(),
					hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0));

			dashBoardCountDto.setToDate(now);

			break;
		}
		case "7": {

			LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
			days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
					days7Ago.getHour(), 0, 0);
			LocalDateTime now = LocalDateTime.now();

			dashBoardCountDto.setFromDate(days7Ago);
			dashBoardCountDto.setToDate(now);

			break;
		}
		case "15": {

			LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
			days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
					days7Ago.getHour(), 0, 0);
			dashBoardCountDto.setFromDate(days7Ago);
			dashBoardCountDto.setToDate(LocalDateTime.now());

			break;
		}
		case "30": {

			LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
			days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
					days7Ago.getHour(), 0, 0);
			dashBoardCountDto.setFromDate(days7Ago);
			dashBoardCountDto.setToDate(LocalDateTime.now());

			break;
		}
		case "custom": {
			Long days = DashboardUtils.calculateDaysGapByLocalDateTime(countRequest.getFromDate(),
					LocalDateTime.now());
			if (days > 7) {
				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				System.out.println(days7Ago);
				dashBoardCountDto.setFromDate(days7Ago);
				dashBoardCountDto.setToDate(LocalDateTime.now());
				break;
			} else {
				dashBoardCountDto.setFromDate(countRequest.getFromDate());
				dashBoardCountDto.setToDate(countRequest.getToDate());
			}

			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
		}

		return dashBoardCountDto;
	};
	
	public static DashBoardCountDto getThreatDeltaCountFilterDto(DashboardCountRequest countRequest) {
		DashBoardCountDto dashBoardCountDto = new DashBoardCountDto();
		switch (countRequest.getDateType()) {
		case "1": {

			LocalDateTime hours48Ago = LocalDateTime.now().minusHours(48);
			LocalDateTime hours24Ago = LocalDateTime.now().minusHours(24);
			dashBoardCountDto.setFromDate(LocalDateTime.of(hours48Ago.getYear(), hours48Ago.getMonth(),
					hours48Ago.getDayOfMonth(), hours48Ago.getHour(), 0, 0));
			dashBoardCountDto.setToDate(hours24Ago);

			break;
		}
		case "7": {

			LocalDateTime days14Ago = LocalDateTime.now().minusDays(14);
			LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
			days14Ago = LocalDateTime.of(days14Ago.getYear(), days14Ago.getMonth(), days14Ago.getDayOfMonth(),
					days14Ago.getHour(), 0, 0);
			dashBoardCountDto.setFromDate(days14Ago);
			dashBoardCountDto.setToDate(days7Ago);
			break;
		}
		case "custom": {
			if (Objects.nonNull(countRequest.getFromDate()) && Objects.nonNull(countRequest.getToDate())) {
				Long days = DashboardUtils.calculateDaysGapByLocalDateTime(countRequest.getFromDate(),
						countRequest.getToDate());
				if (days == 0L) {
					days = 1L;
				}
				LocalDateTime daysAgo = LocalDateTime.now().minusDays(days);
				daysAgo = LocalDateTime.of(daysAgo.getYear(), daysAgo.getMonth(), daysAgo.getDayOfMonth(),
						daysAgo.getHour(), 0, 0);
				dashBoardCountDto.setFromDate(daysAgo);
				dashBoardCountDto.setToDate(countRequest.getFromDate());
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
		}

		return dashBoardCountDto;
	};
	
	public static Specification<LogStopageMaster> getAssetsByTicketPriorityTotalCountFilter(Long tenantId) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.equal(root.get("deleted"), AppConstants.FALSE));
			predicates.add(criteriaBuilder.equal(root.get("disabled"), AppConstants.FALSE));
			predicates.add(criteriaBuilder.equal(root.get("suppressed"), AppConstants.FALSE));
			
			query.groupBy(root.get("productId")).orderBy(criteriaBuilder.desc(root.get("count")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static Specification<TicketAssetMapping> getAssetsByTicketPriorityLevelCountFilter(
			DashboardCountRequest countRequest, List<Long> assetIdList) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime hoursAgo = LocalDateTime.now().minusHours(24);
				LocalDateTime now = LocalDateTime.now();
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"), LocalDateTime.of(
						hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0)));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),
						LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"), days7Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"), now));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"), days15Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"), now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
						now.getHour(), 0, 0);
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"), days30Ago));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"),now));

				break;
			}
			case "custom": {

				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ticketCreatedDate"),
						countRequest.getFromDate()));

				predicates.add(criteriaBuilder.lessThan(root.get("ticketCreatedDate"), countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.in(root.get("assetId")).value(assetIdList));
			
			query.groupBy(root.get("productId"), root.get("priority")).orderBy(criteriaBuilder.desc(root.get("count")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	public static Specification<ThreatLevelStatsCountrySrcipHour> getThreatCountriesSpecificationFileter(DashboardCountRequest countRequest,Set<String> countryCodeList) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			switch (countRequest.getDateType()) {
			case "1": {

				LocalDateTime now = LocalDateTime.now();
				LocalDateTime hoursAgo = now.minusHours(24);
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalEndTime"), LocalDateTime.of(
						hoursAgo.getYear(), hoursAgo.getMonth(), hoursAgo.getDayOfMonth(), hoursAgo.getHour(), 0, 0)));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalEndTime"), now));

				break;
			}
			case "7": {

				LocalDateTime days7Ago = LocalDateTime.now().minusDays(7);
				days7Ago = LocalDateTime.of(days7Ago.getYear(), days7Ago.getMonth(), days7Ago.getDayOfMonth(),
						days7Ago.getHour(), 0, 0);
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalEndTime"), days7Ago));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalEndTime"), LocalDateTime.now()));

				break;
			}
			case "15": {

				LocalDateTime days15Ago = LocalDateTime.now().minusDays(15);
				days15Ago = LocalDateTime.of(days15Ago.getYear(), days15Ago.getMonth(), days15Ago.getDayOfMonth(),
						days15Ago.getHour(), 0, 0);
				LocalDateTime now = LocalDateTime.now();
				
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalEndTime"), days15Ago));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalEndTime"), now));

				break;
			}
			case "30": {

				LocalDateTime days30Ago = LocalDateTime.now().minusDays(30);
				days30Ago = LocalDateTime.of(days30Ago.getYear(), days30Ago.getMonth(), days30Ago.getDayOfMonth(),
						days30Ago.getHour(), 0, 0);
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalEndTime"), days30Ago));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalEndTime"), LocalDateTime.now()));

				break;
			}
			case "custom": {

				predicates.add(
						criteriaBuilder.greaterThanOrEqualTo(root.get("intervalEndTime"), countRequest.getFromDate()));

				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalEndTime"), countRequest.getToDate()));

				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + countRequest.getDateType());
			}

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countRequest.getTenantId()));
			predicates.add(criteriaBuilder.in(root.get("countryCode")).value(countryCodeList));

			query.groupBy(root.get("countryCode")).having(criteriaBuilder.notEqual(root.get("priorityCount"), 0));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<ThreatLevelStatsCountrySrcipHour> getThreatCountriesAlertSpecificationFileter(
			CountryMapAlertRequest countryMapAlertRequest, LocalDateTime hours24Ago, LocalDateTime now) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("intervalEndTime"),
					LocalDateTime.of(hours24Ago.getYear(), hours24Ago.getMonth(), hours24Ago.getDayOfMonth(),
							hours24Ago.getHour(), 0, 0)));

			predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("intervalEndTime"), now));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), countryMapAlertRequest.getTenantId()));
			predicates.add(criteriaBuilder.equal(root.get("countryCode"), countryMapAlertRequest.getCountryCode()));

			query.groupBy(root.get("hourIntervalEndTime"), root.get("sourceIp")).orderBy(
					criteriaBuilder.asc(root.get("sourceIp")), criteriaBuilder.asc(root.get("destinationIpCount")));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<LogStopageMaster> getAssetByLogFlowStatusFilterSpecification(Long tenantId,
			String dateFieldName, Integer deviceStatus) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
//			LocalDateTime maxSearchDateTime = LocalDateTime.now().minusDays(maxSearchDays).truncatedTo(ChronoUnit.DAYS);
//			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), maxSearchDateTime));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.equal(root.get("deleted"), AppConstants.BOOLEAN_FALSE));
			predicates.add(criteriaBuilder.equal(root.get("disabled"), AppConstants.FALSE));
			predicates.add(criteriaBuilder.equal(root.get("suppressed"), AppConstants.FALSE));
			predicates.add(criteriaBuilder.equal(root.get("deviceStatus"), deviceStatus));

			LocalDateTime dayDifferences1 = LocalDateTime.now().minusDays(1);
			dayDifferences1 = LocalDateTime.of(dayDifferences1.getYear(), dayDifferences1.getMonth(),
					dayDifferences1.getDayOfMonth(), dayDifferences1.getHour(), 0, 0);
			LocalDateTime dayDifferences7 = LocalDateTime.now().minusDays(7);
			dayDifferences7 = LocalDateTime.of(dayDifferences7.getYear(), dayDifferences7.getMonth(),
					dayDifferences7.getDayOfMonth(), dayDifferences7.getHour(), 0, 0);
			LocalDateTime dayDifferences15 = LocalDateTime.now().minusDays(15);
			dayDifferences15 = LocalDateTime.of(dayDifferences15.getYear(), dayDifferences15.getMonth(),
					dayDifferences15.getDayOfMonth(), dayDifferences15.getHour(), 0, 0);
			LocalDateTime dayDifferences30 = LocalDateTime.now().minusDays(30);
			dayDifferences30 = LocalDateTime.of(dayDifferences30.getYear(), dayDifferences30.getMonth(),
					dayDifferences30.getDayOfMonth(), dayDifferences30.getHour(), 0, 0);

			query.groupBy(criteriaBuilder.selectCase()
					.when(criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), dayDifferences1),
							AppConstants.HOURS_DIFFERENCE_0_TO_24)
					.when(criteriaBuilder.and(
							criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), dayDifferences7),
							criteriaBuilder.lessThan(root.get(dateFieldName), dayDifferences1)),
							AppConstants.DAYS_DIFFERENCE_2_TO_7)
					.when(criteriaBuilder.and(
							criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), dayDifferences15),
							criteriaBuilder.lessThan(root.get(dateFieldName), dayDifferences7)),
							AppConstants.DAYS_DIFFERENCE_8_TO_15)
					.when(criteriaBuilder.and(
							criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldName), dayDifferences30),
							criteriaBuilder.lessThan(root.get(dateFieldName), dayDifferences15)),
							AppConstants.DAYS_DIFFERENCE_16_TO_30)
					.when(criteriaBuilder.lessThan(root.get(dateFieldName), dayDifferences30),
							AppConstants.DAYS_DIFFERENCE_30));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<AvgResponseTimeForTicketCategoryModel> getAverageResponseTimeForTicketCategorySpecificationFileter(
			Long tenantId, LocalDateTime hours24Ago, LocalDateTime now) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(
					criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), LocalDateTime.of(hours24Ago.getYear(),
							hours24Ago.getMonth(), hours24Ago.getDayOfMonth(), hours24Ago.getHour(), 0, 0)));

			predicates.add(criteriaBuilder.lessThan(root.get("closedDate"),
					LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0)));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_71));

			query.groupBy(root.get("category"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<AvgResponseTimeModel> getSpeedOfResponseForAvgResponseTrendFilterSpecification(
			Long tenantId, LocalDateTime hours24Ago, LocalDateTime now) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), hours24Ago));

			predicates.add(criteriaBuilder.lessThan(root.get("closedDate"), now));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));
			predicates.add(criteriaBuilder.equal(root.get("status"), AppConstants.TICKET_STATUS_71));

			query.groupBy(root.get("hourClosedDate"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
	
	public static Specification<InvestigationSave> getSpeedOfResponseForInvestigationTrendFilterSpecification(
			Long tenantId, LocalDateTime hours24Ago, LocalDateTime now) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("closedDate"), hours24Ago));

			predicates.add(criteriaBuilder.lessThan(root.get("closedDate"), now));

			predicates.add(criteriaBuilder.equal(root.get("tenantId"), tenantId));

			query.groupBy(root.get("hourClosedDate"));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

	}
}
