package aisaac.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import aisaac.exception.BadParameterException;
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
public class CustomLFDRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public <T> List<String> getDataByColumnNameAndValue(String columnName, String value, Class<T> className) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> dtoQuery = criteriaBuilder.createQuery(String.class);
			Root<T> employeeRoot = dtoQuery.from(className);

			dtoQuery.select(criteriaBuilder.construct(String.class, employeeRoot.get(columnName).alias(columnName)))
					.distinct(true);

			// Add any predicates if needed
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.like(employeeRoot.get(columnName), '%' + value + '%'));
			predicates.add(criteriaBuilder.isNotNull(employeeRoot.get(columnName)));
			predicates.add(
					criteriaBuilder.equal(employeeRoot.get(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_NAME_IN_ENTITY),
							AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT));
			Predicate predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			dtoQuery.where(predicate);

			// Perform the query and return the result
			TypedQuery<String> typedQuery = entityManager.createQuery(dtoQuery);
			return typedQuery.getResultList();
		} catch (Exception e) {
			throw new BadParameterException(e.getMessage());
		}
	}

	public <T> List<String> getDataByColumnNameWithOptionalOrganizationIds(String columnName, List<Integer> orgIds,
			Class<T> className) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> dtoQuery = criteriaBuilder.createQuery(String.class);
			Root<T> employeeRoot = dtoQuery.from(className);

			dtoQuery.select(criteriaBuilder.construct(String.class, employeeRoot.get(columnName).alias(columnName)))
					.distinct(true);

			// Add any predicates if needed
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(
					criteriaBuilder.equal(employeeRoot.get(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_NAME_IN_ENTITY),
							AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT));
			
			predicates.add(criteriaBuilder.isNotNull(employeeRoot.get(columnName)));
			
			if (!orgIds.isEmpty()) {
				predicates.add(criteriaBuilder.in(employeeRoot.get(AppConstants.COLUMN_NAME_CUSTOMER_ID)).value(orgIds));
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

	@Transactional(propagation = Propagation.REQUIRED)
	public <T> int updateUserActionSatutsFromGivenList(String userAction, List<?> data, String note, Class<T> className,
			Long userId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaUpdate<T> update = cb.createCriteriaUpdate(className);
		Root<T> root = update.from(className);

		switch (userAction) {
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE_VALUE: {
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DISABLE),
					AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE);
			break;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE_VALUE: {
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DISABLE),
					AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE);
			break;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME_VALUE: {
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_SUPPRESS),
					AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME);
			break;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS_VALUE: {
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_SUPPRESS),
					AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS);
			break;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_NOTE), note);
			break;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE: {
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DELETE),
					AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE);
			update.set(root.get(AppConstants.LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DESCRIPTION), note);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + userAction);
		}
		update.set(root.get("updatedBy"), userId);
		update.set(root.get("updatedDate"), LocalDateTime.now());

		Predicate predicate = cb.in(root.get("recId")).value(data);
		update.where(predicate);

		return entityManager.createQuery(update).executeUpdate();
	}
}
