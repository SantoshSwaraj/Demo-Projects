package aisaac.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import aisaac.domain.datatable.DatatablePage;
import aisaac.domain.datatable.Search;
import aisaac.dto.TriageRuleListingRequestDto;
import aisaac.dto.TriageRuleListingResponseDto;
import aisaac.entities.Tenant;
import aisaac.entities.TriageRule;
import aisaac.entities.User;
import aisaac.repository.RuleTypeMasterRepository;
import aisaac.repository.SysParameterValueRepository;
import aisaac.repository.TenantRepository;
import aisaac.repository.TriageRuleRepository;
import aisaac.repository.UserRepository;
import aisaac.util.AppConstants;
import aisaac.util.CommonUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class TriageRuleServiceImpl implements TriageRuleService{

	@Autowired
	TriageRuleRepository triageRuleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TenantRepository tenantRepository;

	@Autowired
	RuleTypeMasterRepository ruleTypeMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	SysParameterValueRepository sysParameterValueRepository;

	@Override
	public Object getTriageRuleListing(TriageRuleListingRequestDto request) {
		List<TriageRule> responseRuleList=new ArrayList<>();
		DatatablePage<Object> results = new DatatablePage<>();
		try {
			int pageNumber = request.getStart() / request.getLength(); // Calculate page number
			int length = request.getLength();
			String searchTxt=null;
			List<Long> actionIdList=null;
			List<Long> updatedByUserIdList=null;
			List<Long> statusIdList=null;
			//setting values from epoch to timestamps
			request=setEpochTimestamp(request);
			Pageable pageable = PageRequest.of(pageNumber, length);
			if(request.getSearchType()==1) {
				searchTxt=CheckIsFreeSearch(request.getSearch());
				actionIdList=getRuleActionIdByLikeSearch(searchTxt);
				updatedByUserIdList=getUserIdOnLikeSearch(searchTxt);
				statusIdList=getStatusIdsOnLikeSearch(searchTxt);
			}else if(request.getRuleAction().size()>0) {
				actionIdList=getRuleActionId(request.getRuleAction());
			}
			Specification<TriageRule> specification = prepareTriageRuleQueryFilter(request,actionIdList,searchTxt,updatedByUserIdList,statusIdList);
			Page<TriageRule> page = triageRuleRepository.findAll(specification, pageable);
			responseRuleList = page.getContent();
			results.setRecordsTotal((int)page.getTotalElements());
			results.setRecordsFiltered((int)page.getTotalElements());
			results.setDraw(request.getDraw());
			results.setLength(request.getLength());
			results.setData(prepareTriageRuleListResponse(responseRuleList));
			return results;
		}catch(Exception e) {
			log.error("@Exception in getTriageRuleListing()::",e);
		}
		return results;
	}

	//converting Epoch to timestamp
	private TriageRuleListingRequestDto setEpochTimestamp(TriageRuleListingRequestDto request) {
		try {
			request.setFromDate(CommonUtils.parseEpochToDate(request.getFromDateEpoch()));
			request.setToDate(CommonUtils.parseEpochToDate(request.getToDateEpoch()));
			request.setUpdatedFromDate(CommonUtils.parseEpochToDate(request.getUpdatedFromDateEpoch()));
			request.setUpdatedToDate(CommonUtils.parseEpochToDate(request.getUpdatedToDateEpoch()));
			request.setExpiryDate(CommonUtils.parseEpochToDate(request.getExpiryDateEpoch()));

		}catch(Exception e) {
			log.error("@@Exception in setEpochTimestamp()::",e);
		}
		return request;
	}

	private String CheckIsFreeSearch(Map<Search, String> searchMap) {
		String searchTxt = "";
		try {
			if (searchMap != null && searchMap.size() > 0) {
				searchTxt = searchMap.get(Search.value);
			}
			return searchTxt;
		}catch(Exception e) {
			log.error("@Exception in CheckIsFreeSearch()::",e);
		}
		return null;
	}

	private List<Object> prepareTriageRuleListResponse(List<TriageRule> triageRuleList) {
		List<Object> responseRuleList=new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat(AppConstants.EXPORT_GENERIC_DATETIME_FORMAT_STR);
		try {
			List<User> userList = userRepository.findAll();
			Map<Long, String> userMap = userList.stream()
					.collect(Collectors.toMap(o -> o.getUserId(), o -> o.getDisplayName()));

			List<Tenant> tenants = tenantRepository.findAll();
			Map<Long, String> tenateNameAndIdMap = tenants.stream()
					.collect(Collectors.toMap(o -> o.getTenantId(), o -> o.getTenantName()));

			Map<Long,String> statusMap = commonService.mapSysParamValueByType(AppConstants.SYSPARAM_TYPE_STATUS);
			Map<Long,String> ruleTypeMasterMap = commonService.mapRuleTypeMasters();

			for (TriageRule ruleEntity : triageRuleList) {
				TriageRuleListingResponseDto responseBean=new TriageRuleListingResponseDto();
				responseBean.setRuleExpiryDateStr(null!=ruleEntity.getExpiryDate()?formatter.format(ruleEntity.getExpiryDate()):null);
				responseBean.setStatus(statusMap.getOrDefault(Long.parseLong(String.valueOf(ruleEntity.getRuleStateSysparam())), null));
				responseBean.setStatusId(null!=ruleEntity.getRuleStateSysparam()?ruleEntity.getRuleStateSysparam():0);
				responseBean.setCreatedDtFormtDt(null!=ruleEntity.getCreatedDate()?formatter.format(ruleEntity.getCreatedDate()):null);
				responseBean.setUpdatedDtFormtDt(null!=ruleEntity.getUpdatedDate()?formatter.format(ruleEntity.getUpdatedDate()):null);
				responseBean.setCompilerServiceOn(null!=ruleEntity.getCompilerServiceOn()?ruleEntity.getCompilerServiceOn():null);
				responseBean.setCondition(null!=ruleEntity.getRuleCondition()?ruleEntity.getRuleCondition():null);
				responseBean.setCreatedBy(null!=ruleEntity.getCreatedById()?ruleEntity.getCreatedById():0l);
				responseBean.setCreatedByStr(userMap.getOrDefault(ruleEntity.getCreatedById(), null));
				responseBean.setOrganization(tenateNameAndIdMap.getOrDefault(ruleEntity.getTenantId(), null));
				responseBean.setRuleAction(ruleTypeMasterMap.getOrDefault(ruleEntity.getRuleTypeId(), null));
				responseBean.setRuleCompiled(null!=ruleEntity.getRuleCompiled()?ruleEntity.getRuleCompiled():null);
				responseBean.setRuleId(null!=ruleEntity.getRuleId()?ruleEntity.getRuleId():0l);
				responseBean.setRuleName(null!=ruleEntity.getRuleName()?ruleEntity.getRuleName():null);
				responseBean.setUpdatedBy(null!=ruleEntity.getUpdatedById()?ruleEntity.getUpdatedById():0l);
				responseBean.setUpdatedByStr(userMap.getOrDefault(ruleEntity.getUpdatedById(), null));

				responseBean.setRuleExpiryEpoch(CommonUtils.parseDateToEpoch(ruleEntity.getExpiryDate()));
				responseBean.setCreatedDateEpoch(CommonUtils.parseDateToEpoch(ruleEntity.getCreatedDate()));
				responseBean.setUpdatedDateEpoch(CommonUtils.parseDateToEpoch(ruleEntity.getUpdatedDate()));
				responseRuleList.add(responseBean);
			}
		}catch(Exception e) {
			log.error("@@Exception in prepareTriageRuleListResponse():",e);
		}
		return responseRuleList;

	}


	public static <T> Specification<T> prepareTriageRuleQueryFilter(TriageRuleListingRequestDto request,List<Long> actionIdList, String searchTxt, List<Long> userIdList, List<Long> statusIdList) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			List<Predicate> freeSearchPredicates = new ArrayList<>();

			//If organization = ALL will have to pass as empty array
			if(null!=request.getOrganization() && !request.getOrganization().isEmpty()) {
				predicates.add(criteriaBuilder.in(root.get("tenantId")).value(request.getOrganization()));
			}

			if (request.getSearchType() == (3)) {
				if (StringUtils.isNotBlank(request.getRuleId())) {
					predicates.add(criteriaBuilder.equal(root.get("ruleId"), request.getRuleId()));
				}
				if (StringUtils.isNotBlank(request.getRuleName())) {
					predicates.add(criteriaBuilder.equal(root.get("ruleName"), request.getRuleName()));
				}
				if (null!=actionIdList && actionIdList.size()>0) {
					predicates.add(criteriaBuilder.in(root.get("ruleTypeId")).value(actionIdList));
				}
				if (StringUtils.isNotBlank(request.getConditionInput())) {
					predicates.add(criteriaBuilder.equal(root.get("ruleCondition"), request.getConditionInput()));
				}

				if (StringUtils.isNotBlank(request.getCreatedBy()) && !request.getCreatedBy().equals("0")) {
					predicates.add(criteriaBuilder.equal(root.get("createdById"), request.getCreatedBy()));
				}
				if (StringUtils.isNotBlank(request.getUpdatedBy()) && !request.getUpdatedBy().equals("0")) {
					predicates.add(criteriaBuilder.equal(root.get("updatedById"), request.getUpdatedBy()));
				}
				if (request.getStatus()!=0l) {
					predicates.add(criteriaBuilder.equal(root.get("ruleStateSysparam"), request.getStatus()));
				}

				if (null!=request.getFromDate() && null!=request.getToDate()) {
					predicates.add(criteriaBuilder.between(root.get("createdDate"),request.getFromDate(), request.getToDate()));
				}

				if (null!=request.getUpdatedFromDate() &&null!=request.getUpdatedToDate()) {
					predicates.add(criteriaBuilder.between(root.get("updatedDate"),request.getUpdatedFromDate(), request.getUpdatedToDate()));
				}
				if (StringUtils.isNotBlank(request.getRuleDescription())) {
					predicates.add(criteriaBuilder.like(root.get("ruleDescription"),request.getRuleDescription()));
				}

				if (null!=request.getExpiryDate()) {
					predicates.add(criteriaBuilder.equal(root.get("expiryDate"),request.getExpiryDate()));
				}

			} else if (request.getSearchType() == 1) {
				//Its for free search
				if (StringUtils.isNotEmpty(searchTxt)) {
					freeSearchPredicates.add(criteriaBuilder.like(root.get("ruleId").as(String.class), AppConstants.PERCENTAGE + searchTxt + AppConstants.PERCENTAGE));
					freeSearchPredicates.add(criteriaBuilder.like(root.get("ruleName"), AppConstants.PERCENTAGE + searchTxt + AppConstants.PERCENTAGE));
					freeSearchPredicates.add(criteriaBuilder.like(root.get("ruleCondition"),AppConstants.PERCENTAGE + searchTxt + AppConstants.PERCENTAGE));
					freeSearchPredicates.add(criteriaBuilder.like(root.get("ruleDescription"),AppConstants.PERCENTAGE + searchTxt + AppConstants.PERCENTAGE));
					freeSearchPredicates.add(criteriaBuilder.in(root.get("ruleTypeId")).value(actionIdList));
					freeSearchPredicates.add(criteriaBuilder.in(root.get("updatedById")).value(userIdList));
					freeSearchPredicates.add(criteriaBuilder.in(root.get("createdById")).value(userIdList));
					freeSearchPredicates.add(criteriaBuilder.in(root.get("ruleStateSysparam")).value(statusIdList));
					predicates.add(criteriaBuilder.or(freeSearchPredicates.toArray(new Predicate[0])));
				}
			}
			query.orderBy(criteriaBuilder.desc(root.get("updatedDate")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	public List<Long> getRuleActionId(List<String> list) {
		List<Long> actionIds=new ArrayList<>();
		try {
			actionIds=ruleTypeMasterRepository.findByRuleTypeIn(list).stream().map(e->e.getRuleTypeId()).collect(Collectors.toList());
		}catch(Exception e) {
			log.error("@@Exception in getRuleActionId()::",e);
		}
		return actionIds;
	}

	public List<Long> getRuleActionIdByLikeSearch(String actionName) {
		List<Long> ruleActionIds=new ArrayList<>();
		try {
			ruleActionIds=ruleTypeMasterRepository.findByRuleTypeContaining(actionName).stream().map(e->e.getRuleTypeId()).collect(Collectors.toList());
		}catch(Exception e) {
			log.error("@@Exception in getRuleActionIdByLikeSearch()::",e);
		}
		return ruleActionIds;
	}

	public List<Long> getUserIdOnLikeSearch(String userName) {
		List<Long> userIds=new ArrayList<>();
		try {
			userIds=userRepository.getUserIdsOnLikeSearch(userName);
		}catch(Exception e) {
			log.error("@@Exception in getUserIdOnLikeSearch()::",e);
		}
		return userIds;
	}

	public List<Long> getStatusIdsOnLikeSearch(String status) {
		List<Long> statusIds=new ArrayList<>();
		try {
			statusIds=sysParameterValueRepository.getParamTypeIdsOnLikeSearch(status);
		}catch(Exception e) {
			log.error("@@Exception in getStatusIdsOnLikeSearch()::",e);
		}
		return statusIds;
	}

}
