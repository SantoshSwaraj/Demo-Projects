package aisaac.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import aisaac.entities.EasmIssue;
import aisaac.payload.request.ExposuresDetailsRequest;
import jakarta.persistence.criteria.Predicate;

public class ExposuresDomain {
	
	public static <T> Specification<T> getAdvanceOrFullSearchFilterSpecification(
			Map<String, Object> collectedMap, Boolean useLogicalOr, ExposuresDetailsRequest request, Class<T> EntityName){
		
		if(collectedMap ==null) {
			collectedMap=new HashMap<>();
		}
		String issueTitle = (String) collectedMap.get("title");
		Object confidenceObj = collectedMap.get("confidence");
		if(confidenceObj == null) {
			confidenceObj = "";
		 }
		String confidence   = ObjectUtils.isNotEmpty(confidenceObj) ?  String.valueOf(confidenceObj) : null;
		String issueCategory = (String)collectedMap.get("issueType");
		String mitreName = (String)collectedMap.get("mitreTechnique");
		List<String> severity = (List<String>) collectedMap.get("severity");
		String resourceIp = (String)collectedMap.get("ipAddress");
		String summary = (String) collectedMap.get("summary");
		String issueId = (String) collectedMap.get("issueId");
		String issueSubcategory = (String) collectedMap.get("issueSubcategory");
		Object severityScoreObj = collectedMap.get("severityScore");
		if(severityScoreObj == null) {
			severityScoreObj = "";
		 }
		String severityScore   = ObjectUtils.isNotEmpty(severityScoreObj) ?  String.valueOf(severityScoreObj) : null;
		String cveId = (String) collectedMap.get("cveId");
		String potentialImpact = (String) collectedMap.get("potentialImpact");
		String assetOwnerDetails = (String) collectedMap.get("assetOwner");
		String location = (String) collectedMap.get("location");
		Object firstDetectedBetween = collectedMap.get("detectedBetween");
		 if(firstDetectedBetween == null) {
			 firstDetectedBetween = new HashMap<>();
		 }
		 
		 Map<String, Object> firstDetected = ((Map<String, String>) firstDetectedBetween).entrySet()
			        .stream()
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Object reportedBetween = collectedMap.get("reportedBetween");
		 if(reportedBetween == null) {
			 reportedBetween = new HashMap<>();
		 }
		 
		 Map<String, Object> reportedDate = ((Map<String, String>) reportedBetween).entrySet()
			        .stream()
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<Integer> tenantIds= request.getOrgIds();
		String reportedBetweenFrom = ObjectUtils.isNotEmpty(reportedDate.get("from")) ? String.valueOf(reportedDate.get("from")): null;
		String reportedBetweenTo   = ObjectUtils.isNotEmpty(reportedDate.get("to")) ?  String.valueOf(reportedDate.get("to")) : null;
		String firstDetectedBetweenFrom = ObjectUtils.isNotEmpty(firstDetected.get("from")) ? String.valueOf(firstDetected.get("from")): null;
		String firstDetectedBetweenTo   = ObjectUtils.isNotEmpty(firstDetected.get("to")) ?  String.valueOf(firstDetected.get("to")) : null;	
		
		return (root, query, criteriaBuilder) -> {
			Specification<T> specification = Specification.where(null);

				// for full search
					
			if ((StringUtils.isNotBlank(issueTitle)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("issueTitle"), "%" + issueTitle + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}

			if ((StringUtils.isNotBlank(confidence)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.equal(subroot.get("confidence"), confidence);
				specification = createCondition(specification, condition, useLogicalOr);
			}

			/*
			 * if (CollectionUtils.isNotEmpty(tenantIds) &&
			 * EntityName.equals(EasmIssue.class)) { Specification<T> condition = (subroot,
			 * subquery, builder) -> criteriaBuilder
			 * .in(subroot.get("tenantId")).value(tenantIds); specification =
			 * createCondition(specification, condition, useLogicalOr); }
			 */

			if ((StringUtils.isNotBlank(issueCategory)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("issueCategory"), "%" + issueCategory + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			 
			if (StringUtils.isNotBlank(mitreName) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("mitreName"), "%" + mitreName + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}

			if ((CollectionUtils.isNotEmpty(severity)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("severity")).value(severity);
				specification = createCondition(specification, condition, useLogicalOr);
			}

			if (StringUtils.isNotBlank(resourceIp) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("resourceIp"), "%" + resourceIp + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}

			if (reportedBetweenFrom != null && reportedBetweenTo != null && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("lastDetectedAt"), reportedBetweenFrom, reportedBetweenTo);
				specification = createCondition(specification, condition, useLogicalOr);
			}

			if ((StringUtils.isNotBlank(summary)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("summary"), "%" + summary + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(issueId)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("issueId"), "%" + issueId + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(issueSubcategory)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("issueSubcategory"), "%" + issueSubcategory + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(severityScore)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.equal(subroot.get("severityScore"), severityScore);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(cveId)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("cveId")).value(cveId);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(potentialImpact)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("potentialImpact"), "%" + potentialImpact + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(assetOwnerDetails)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("assetOwnerDetails"), "%" + assetOwnerDetails + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if ((StringUtils.isNotBlank(location)) && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("location"), "%" + location + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			if (firstDetectedBetweenFrom != null && firstDetectedBetweenTo != null && EntityName.equals(EasmIssue.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("firstDetectedAt"), firstDetectedBetweenFrom, firstDetectedBetweenTo);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			Specification<T> recordStateSpecification = (subroot, subquery, subcriteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                predicates
                .add(criteriaBuilder
						.in(root.get("tenantId")).value(tenantIds));
			
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            specification = createCondition(recordStateSpecification, specification, false);

			// specification = createCondition(specification, specification, false);
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
