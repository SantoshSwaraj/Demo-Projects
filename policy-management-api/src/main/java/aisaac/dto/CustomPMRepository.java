package aisaac.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import aisaac.entities.CspmFinding;
import aisaac.exception.BadParameterException;
import aisaac.exception.ResourceNotFoundException;
import aisaac.util.AppConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Component
public class CustomPMRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public <T> List<CspmFinding> getDataByColumnNameAndValue(String columnName, String value, Class<T> className) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CspmFinding> dtoQuery = criteriaBuilder.createQuery(CspmFinding.class);
			Root<CspmFinding> employeeRoot = dtoQuery.from(CspmFinding.class);

			dtoQuery.select(employeeRoot);

			// Add any predicates if needed
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.like(employeeRoot.get(columnName), '%' + value + '%'));
			predicates.add(criteriaBuilder.isNotNull(employeeRoot.get(columnName)));
			predicates
					.add(criteriaBuilder.equal(employeeRoot.get(AppConstants.POLICY_MANAGEMENT_RECORD_STATE_IN_ENTITY),
							AppConstants.POLICY_MANAGEMENT_RECORD_STATE));
			predicates.add(criteriaBuilder.equal(employeeRoot.get(AppConstants.POLICY_MANAGEMENT_SOURCE_NAME_IN_ENTITY),
					AppConstants.POLICY_MANAGEMENT_SOURCE_NAME));
			Predicate predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			dtoQuery.where(predicate);

			// Perform the query and return the result
			TypedQuery<CspmFinding> typedQuery = entityManager.createQuery(dtoQuery);
			return typedQuery.getResultList();
		} catch (Exception e) {
			throw new BadParameterException(e.getMessage());
		}
	}

	public <T> List<String> getDataByColumnNameWithOptionalOrganizationIds(List<Integer> orgIds,
			Class<T> className) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> dtoQuery = criteriaBuilder.createQuery(String.class);
			Root<T> employeeRoot = dtoQuery.from(className);
			
			List<Predicate> predicates = new ArrayList<>();
			if (!orgIds.isEmpty()) {
				predicates.add(criteriaBuilder.in(employeeRoot.get("tenantId")).value(orgIds));
			}
			Predicate predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			dtoQuery.where(predicate);

			// Perform the query and return the result
			TypedQuery<String> typedQuery = entityManager.createQuery(dtoQuery);
			return typedQuery.getResultList();
		} catch (Exception e) {
			throw new BadParameterException(e.getMessage());
		}
	}

}
