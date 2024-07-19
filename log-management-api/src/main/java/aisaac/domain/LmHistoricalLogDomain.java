package aisaac.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import aisaac.model.LmHistoricalLog;
import aisaac.payload.request.HistoricalLogsDetailsRequest;
import javax.persistence.criteria.Predicate;


public class LmHistoricalLogDomain {
	
	public static Specification<LmHistoricalLog> getLmHistoricalLogFilterSpecification(
			HistoricalLogsDetailsRequest request) {
		
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			List<Predicate> tenantPredicates = new ArrayList<>();
			List<Predicate> datePredicates = new ArrayList<>();
			List<Predicate> deletedPredicates = new ArrayList<>();
			if(request.isDefaultSearch()) {
				tenantPredicates
					.add(criteriaBuilder.equal(root.get("tenantId"), request.getTenantId()));
				
				deletedPredicates
					.add(criteriaBuilder.equal(root.get("isDeleted"), false));
				
				/*
				 * datePredicates
				 * .add(criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate"),
				 * request.getFromDate()));
				 * 
				 * datePredicates .add(criteriaBuilder.lessThan(root.get("toDate"),
				 * request.getToDate()));
				 */
				
				predicates.add(criteriaBuilder.and(tenantPredicates.toArray(new Predicate[0])));
				predicates.add(criteriaBuilder.and(deletedPredicates.toArray(new Predicate[0])));
				//predicates.add(criteriaBuilder.and(datepredicates.toArray(new Predicate[0])));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
