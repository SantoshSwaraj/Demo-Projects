package aisaac.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.jpa.domain.Specification;

import aisaac.domain.datatable.Order;
import aisaac.entities.CspmFinding;
import aisaac.entities.RemediationPlaybookName;
import aisaac.payload.request.PolicyManagementDetailsRequest;
import aisaac.util.AppConstants;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class PolicyManagementDomain {
	
	public static <T> Specification<T> getAdvanceOrFullSearchFilterSpecification(
			Map<String, Object> collectedMap, Boolean useLogicalOr,PolicyManagementDetailsRequest request, Class<T> EntityName){
		
		if(collectedMap ==null) {
			collectedMap=new HashMap<>();
		}
		String complianceStandards = (String) collectedMap.get("standards");
		String finding = (String)collectedMap.get("finding");
		String cloudAccountId = (String)collectedMap.get("tenantId");
		String securityControlId = (String)collectedMap.get("securityControlId");
		String remPlaybookName = (String)collectedMap.get("remPlaybookName");
		String cloudResourceId = (String)collectedMap.get("cloudResourceId");
		List<String> severity = (List<String>) collectedMap.get("severity");
		List<String> complianceStatus = (List<String>)collectedMap.get("complianceStatus");
		List<String> workFlowStatus = (List<String>)collectedMap.get("workflowStatus");
		List<String> remediationStatus =(List<String>) collectedMap.get("remediationStatus");
		 Object reportedBetween = collectedMap.get("reportedBetween");
		 if(reportedBetween == null) {
			 reportedBetween = new HashMap<>();
		 }
		 Map<String, Object> reportedDate = ((Map<String, String>) reportedBetween).entrySet()
			        .stream()
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		 Object createdBetween = collectedMap.get("createdBetween");
		 if(createdBetween == null) {
			 createdBetween = new HashMap<>();
		 }
		 Map<String, Object> createdDate = ((Map<String, String>) createdBetween).entrySet()
			        .stream()
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		 
		 Object updatedBetween = collectedMap.get("updatedBetween");
		 if(updatedBetween == null) {
			 updatedBetween = new HashMap<>();
		 }
		 Map<String, Object> updatedDate = ((Map<String, String>) updatedBetween).entrySet()
			        .stream()
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<Integer> tenantIds= request.getOrgIds();
		List<String> securityControlIds= request.getSearchSecurityControlIds();
		String reportedBetweenFrom = ObjectUtils.isNotEmpty(reportedDate.get("from")) ? String.valueOf(reportedDate.get("from")): null;
		String reportedBetweenTo   = ObjectUtils.isNotEmpty(reportedDate.get("to")) ?  String.valueOf(reportedDate.get("to")) : null;
		String createdBetweenFrom = ObjectUtils.isNotEmpty(createdDate.get("from")) ? String.valueOf(createdDate.get("from")): null;
		String createdBetweenTo   = ObjectUtils.isNotEmpty(createdDate.get("to")) ?  String.valueOf(createdDate.get("to")) : null;
		String updatedBetweenFrom = ObjectUtils.isNotEmpty(updatedDate.get("from")) ? String.valueOf(updatedDate.get("from")): null;
		String updatedBetweenTo   = ObjectUtils.isNotEmpty(updatedDate.get("to")) ?  String.valueOf(updatedDate.get("to")) : null;
		
		return (root, query, criteriaBuilder) -> {
			Specification<T> specification = Specification.where(null);

				// for full search
					
					if ((StringUtils.isNotBlank(finding)) && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.like(subroot.get("finding"), "%" + finding + "%");
						specification = createCondition(specification, condition, useLogicalOr);
					}
					
					if ((StringUtils.isNotBlank(cloudAccountId)) && EntityName.equals(CspmFinding.class)) {
						Specification<T> cloudAccountIdCondition = (subroot, subquery, builder) -> criteriaBuilder
								.like(subroot.get("cloudAccountId"), "%" + cloudAccountId + "%");
						specification = createCondition(specification, cloudAccountIdCondition, useLogicalOr);
					}
					
					if (StringUtils.isNotBlank(securityControlId) && EntityName.equals(CspmFinding.class)) {
						Specification<T> securityControlIdCondition = (subroot, subquery, builder) -> criteriaBuilder
						        .like(subroot.get("securityControlId"), "%" + securityControlId + "%");
						specification = createCondition(specification, securityControlIdCondition, useLogicalOr);
					}
					
//					if (CollectionUtils.isNotEmpty(tenantIds) && EntityName.equals(CspmFinding.class)) {
//						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
//								.in(subroot.get("tenantId")).value(tenantIds);
//						specification = createCondition(specification, condition, useLogicalOr);
//					}
					
					if (StringUtils.isNotBlank(cloudResourceId) && EntityName.equals(CspmFinding.class)) {
						Specification<T> cloudAccountIdCondition = (subroot, subquery, builder) -> criteriaBuilder
								.like(subroot.get("cloudResourceId"), "%" + cloudResourceId + "%");
						specification = createCondition(specification, cloudAccountIdCondition, useLogicalOr);
					}
					
					if (StringUtils.isNotBlank(complianceStandards) && EntityName.equals(CspmFinding.class)) {
						Specification<T> complianceStandardsCondition = (subroot, subquery, builder) -> criteriaBuilder
								.like(subroot.get("complianceStandards"), "%" + complianceStandards + "%");
						specification = createCondition(specification, complianceStandardsCondition, useLogicalOr);
					}
					
					/*
					 * if (StringUtils.isNotBlank(sourceName) &&
					 * EntityName.equals(PolicyManagement.class)) { Specification<T> condition =
					 * (subroot, subquery, builder) -> criteriaBuilder
					 * .like(subroot.get("sourceName"), "%" + sourceName + "%"); specification =
					 * createCondition(specification, condition, useLogicalOr); }
					 */
					
					if (CollectionUtils.isNotEmpty(securityControlIds) && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.in(subroot.get("securityControlId")).value(securityControlIds);
						specification = createCondition(specification, condition, useLogicalOr);
					}
					
					
					if (CollectionUtils.isNotEmpty(complianceStatus) && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.in(subroot.get("complianceStatus")).value(complianceStatus);
						specification = createCondition(specification, condition, useLogicalOr);
					}
					
					if (CollectionUtils.isNotEmpty(workFlowStatus) && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.in(subroot.get("findingStatus")).value(workFlowStatus);
						specification = createCondition(specification, condition, useLogicalOr);
					}
					
					if ((CollectionUtils.isNotEmpty(severity)) && EntityName.equals(CspmFinding.class)) {
						Specification<T> severityCondition = (subroot, subquery, builder) -> criteriaBuilder
								.in(subroot.get("severity")).value(severity);
						specification = createCondition(specification, severityCondition, useLogicalOr);
					}
					
		
					if ( reportedBetweenFrom != null && reportedBetweenTo != null && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.between(subroot.get("createdAt"), reportedBetweenFrom, reportedBetweenTo);
						specification = createCondition(specification, condition, useLogicalOr);
					}
					
					if ( updatedBetweenFrom != null && updatedBetweenTo != null && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.between(subroot.get("updatedAt"), updatedBetweenFrom, updatedBetweenTo);
						specification = createCondition(specification, condition, useLogicalOr);
					}
					
					if ( createdBetweenFrom != null && createdBetweenTo != null && EntityName.equals(CspmFinding.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.between(subroot.get("createdDate"), createdBetweenFrom, createdBetweenTo);
						specification = createCondition(specification, condition, useLogicalOr);
					}
						
				Specification<T> recordStateSpecification = (subroot, subquery, subcriteriaBuilder) -> {
	                List<Predicate> predicates = new ArrayList<>();
	                predicates
	                        .add(criteriaBuilder.equal(root.get(AppConstants.POLICY_MANAGEMENT_RECORD_STATE_IN_ENTITY),
	                                AppConstants.POLICY_MANAGEMENT_RECORD_STATE));
	                predicates
	                .add(criteriaBuilder.equal(root.get(AppConstants.POLICY_MANAGEMENT_SOURCE_NAME_IN_ENTITY),
	                        AppConstants.POLICY_MANAGEMENT_SOURCE_NAME));
	                predicates
	                .add(criteriaBuilder
							.in(root.get("tenantId")).value(tenantIds));
				/*
				 * query.multiselect(subroot.get("securityControlId"),
				 * criteriaBuilder.count(root));
				 * query.groupBy(subroot.get("securityControlId"),subroot.get("cloudAccountId"))
				 * ;
				 */
	                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	            };
	            specification = createCondition(recordStateSpecification, specification, false);
	            
	        	String sortColumn="",sortOrderBy="";
	    		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

				if (orderList != null && orderList.size() > 0) {
					Map<Order, String> orderMap = orderList.get(0);
					sortColumn = orderMap.get(Order.name);
					sortOrderBy = orderMap.get(Order.dir);
				}
				if ( AppConstants.PLAYBOOK_NAME.equalsIgnoreCase(sortColumn)) {
					Subquery<String> subquery = query.subquery(String.class);
					if (AppConstants.PLAYBOOK_NAME.equalsIgnoreCase(sortColumn)) {
						Root<RemediationPlaybookName> subRoot = subquery.from(RemediationPlaybookName.class);
						subquery.select(subRoot.get("playbookName"))
								.where(criteriaBuilder.equal(root.get("securityControlId"),subRoot.get("securityControlId") ));

					}

					if (AppConstants.SORTING_ORDER_ASC.equalsIgnoreCase(sortOrderBy)) {
						query.orderBy(criteriaBuilder.asc(subquery));
					} else {
						query.orderBy(criteriaBuilder.desc(subquery));
					}
					criteriaBuilder.conjunction();
				}
	        
			//specification = createCondition(deleteSpecification, specification, false);
			return specification.toPredicate(root, query, criteriaBuilder);
		};
	}
	
	private static <T> Specification<T> createCondition(Specification<T> specification, Specification<T> condition,
			boolean useLogicalOr) {
		if (useLogicalOr) {
			return specification.or(condition);
		} else {
			return specification.and(condition);
		}
	}
	
}
