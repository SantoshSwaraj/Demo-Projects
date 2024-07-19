package aisaac.service;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisaac.client.PolicyManagementRemediationClient;
import aisaac.domain.PolicyManagementDomain;
import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.domain.datatable.Order;
import aisaac.domain.datatable.Search;
import aisaac.dto.AwsConfigResponseDto;
import aisaac.dto.CustomPMRepository;
import aisaac.dto.PolicyManagementDTO;
import aisaac.dto.PolicyManagementAzureDTO;
import aisaac.dto.PolicyManagementPrismaCloudDTO;
import aisaac.entities.CspmFinding;
import aisaac.entities.CspmRemediationStatus;
import aisaac.entities.GlobalSettings;
import aisaac.entities.RemediationPlaybookName;
import aisaac.entities.Tenant;
import aisaac.entities.TenantSettings;
import aisaac.entities.UserCustomFieldList;
import aisaac.entities.UserTenant;
import aisaac.exception.ValidationException;
import aisaac.payload.mapper.PolicyManagementAzureMapper;
import aisaac.payload.mapper.PolicyManagementAzureMapperList;
import aisaac.payload.mapper.PolicyManagementPrismaCloudMapper;
import aisaac.payload.mapper.PolicyManagementPrismaCloudMapperList;
import aisaac.payload.mapper.PolicyManagementMapper;
import aisaac.payload.mapper.PolicyManagementMapperList;
import aisaac.payload.request.PolicyManagementDetailsRequest;
import aisaac.payload.response.PolicyManagementFindingRemediationResponse;
import aisaac.payload.response.PolicyManagementListingResponse;
import aisaac.payload.response.PolicyManagementRemediationRefreshResponse;
import aisaac.payload.response.PolicyManagementResponse;
import aisaac.payload.response.PolicyManagementAzureResponse;
import aisaac.payload.response.PolicyManagementPrismaCloudResponse;
import aisaac.repository.CspmRemediationStatusRepository;
import aisaac.repository.GlobalSettingsRepository;
import aisaac.repository.PolicyManagementRepository;
import aisaac.repository.RemediationPlaybookNameRepository;
import aisaac.repository.TenantRepository;
import aisaac.repository.TenantSettingsRepository;
import aisaac.repository.UserCustomFieldListRepository;
import aisaac.repository.UserCustomFieldMasterRepository;
import aisaac.repository.UserTenantRepository;
import aisaac.util.AppConstants;
import aisaac.util.ExcelUtils;
import aisaac.util.PolicyManagementAzureExcel;
import aisaac.util.PolicyManagementPrismaCloudExcel;
import aisaac.util.PolicyManagementExcel;
import aisaac.util.ResponseMsgConstants;
import aisaac.util.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PolicyManagementServiceImpl implements PolicyManagementService {

	@Autowired
	private PolicyManagementRepository policyManagementRepository;

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private CustomPMRepository customPMRepository;

	@Autowired
	private UserCustomFieldMasterRepository userCustomFieldMasterRepository;

	@Autowired
	private RemediationPlaybookNameRepository remediationPlaybookNameRepository;

	@Autowired
	private CspmRemediationStatusRepository cspmRemediationStatusRepository;

	@Autowired
	private GlobalSettingsRepository globalSettingsRepo;

	@Autowired
	private TenantSettingsRepository tenantSettingsRepository;

	@Autowired
	private UserTenantRepository userTenantRepo;

	@Autowired
	private UserCustomFieldListRepository userCustomFieldListRepo;

	@Autowired
	private EntityManager entityManager;

	@Override
	public Object getPolicyManagementList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		Long totalCount = null;

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		Map<String, Object> collectedMap = null;
		Map<String, Long> countMapList = new HashMap<>();

		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();
		int pageNumber = 0;
		int length = 0;
		if (!isExport) {
			pageNumber = request.getDatatableInfo().getStart() / request.getDatatableInfo().getLength(); /// Calculate
																											/// page																											/// number
			length = request.getDatatableInfo().getLength();
		} else {
			pageNumber = request.getStart() / request.getLength(); /// Calculate page number
			length = request.getLength();
		}

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);
		if (sortColumn.equalsIgnoreCase("remediationStatus")
				|| (sortColumn.equalsIgnoreCase("remediationPlaybookName"))) {
			pageable = PageRequest.of(pageNumber, length);
		}

		Page<CspmFinding> logsPage = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}
		// List<PolicyManagementDTO> pl =
		// policyManagementRepository.getCountByCspmFinding(null,pageable);

		// Get CspmFinding details
		totalCount = policyManagementRepository.count();
		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			List<Integer> tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}

			// List<PolicyManagementDTO> results =
			// pmr.findAll(CustomSpecifications.fullSearchFilterSpecification());

			String remPlaybookName = (String) collectedMap.get("remPlaybookName");

			// for advanced search and full search
			if (StringUtils.isNotEmpty(remPlaybookName)) {
				List<String> securityControlIdList = remediationPlaybookNameRepository
						.findAllByColumnName(remPlaybookName);
				request.setSearchSecurityControlIds(securityControlIdList);
			}
		}
		List<CspmFinding> policyManagementList = policyManagementRepository.findAll();
		List<RemediationPlaybookName> remediationPlaybookNameList = remediationPlaybookNameRepository.findAll();
		Map<String, String> PlaybookNameMap = remediationPlaybookNameList.stream()
				.collect(Collectors.toMap(te -> te.getSecurityControlId(), te -> te.getPlaybookName()));

		for (CspmFinding policyManagement : policyManagementList) {
			policyManagement.setRemediationPlaybookName(
					PlaybookNameMap.getOrDefault(policyManagement.getSecurityControlId(), null));
		}
		List<String> remediationStatusList = null;
		List<String> securityControlIds = null;
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			remediationStatusList = (List<String>) collectedMap.get("remediationStatus");
			// securityControlIds = filterByRemediationStatus(remediationStatus);
		}
		// List<PolicyManagementDTO> pml =
		// this.getCountByCspmFinding(collectedMap,true,request,pageable);
		// Long filterList = (long) pml.size();

		// List<Object> policyManagementMonitorList = new
		// PolicyManagementMapperList(pml.getContent()).map();

		switch (request.getSearchType()) {
		case 1: {
			Specification<CspmFinding> fullSearchFilterSpecification = PolicyManagementDomain
					.getAdvanceOrFullSearchFilterSpecification(collectedMap, true, request, CspmFinding.class);
			logsPage = policyManagementRepository.findAll(fullSearchFilterSpecification, pageable);
			break;
		}
		case 3: {
			Specification<CspmFinding> advanceSearchFilterSpecification = PolicyManagementDomain
					.getAdvanceOrFullSearchFilterSpecification(collectedMap, false, request, CspmFinding.class);
			logsPage = policyManagementRepository.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		/*
		 * case 5: { Specification<CspmFinding> advanceSearchFilterSpecification =
		 * PolicyManagementDomain .getAdvanceOrFullSearchFilterSpecification(
		 * collectedMap,false,request,CspmFinding.class); logsPage =
		 * policyManagementRepository.findAll(advanceSearchFilterSpecification,
		 * pageable); break; }
		 */
		default:
			logsPage = policyManagementRepository.findAllByGroupingBy(pageable);
		}

		List<Tenant> tenList = tenantRepository.findAll();

		return getPolicyManagementDetailsWithRequiredResponse(tenList, logsPage, request, totalCount,
				remediationStatusList);

	}

	@Override
	public Object getAutoCompleteDataForPolicyManagementMonitor(Map<String, String> request) {
		if (request.isEmpty()) {
			throw new ValidationException("Required Input Parameters are Missing.");
		}
		String paramName = new ArrayList<>(request.keySet()).get(0);

		return customPMRepository.getDataByColumnNameAndValue(paramName, request.get(paramName), CspmFinding.class);
	}

	public Object getPolicyManagementDetailsWithRequiredResponse(List<Tenant> teList,
			Page<CspmFinding> policyManagementMasterPage, PolicyManagementDetailsRequest request, Long totalCount,
			List<String> remediationStatusList) {
		List<CspmFinding> policyManagementFinalList = new ArrayList<>();
		Map<Integer, String> typeMap = teList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<CspmFinding> policyManagementMastersList = policyManagementMasterPage.getContent();
		for (CspmFinding policyManagement : policyManagementMastersList) {
			policyManagement.setTenantName(typeMap.getOrDefault(policyManagement.getTenantId(), ""));
		}

		Map<Order, String> orderMap = request.getDatatableInfo().getOrder().get(0);
		String order = orderMap.get(Order.name);
		// find list according to condition passed in remediation status
		for (CspmFinding policyManagement : policyManagementMastersList) {
			if (StringUtils.isBlank(policyManagement.getRemediationStatus())) {
				if (StringUtils.isBlank(policyManagement.getRemediationPlaybookName())) {
					policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
				} else {
					if (policyManagement.getComplianceStatus()
							.equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_PASSED)
							&& (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							|| (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_RESOLVED))) {
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
					}

					if (policyManagement.getComplianceStatus()
							.equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_FAILED)
							&& (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							|| (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_RESOLVED))
									&& StringUtils.isNotBlank(policyManagement.getRemediationPlaybookName()))
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NEW);
					else if (policyManagement.getComplianceStatus()
							.equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_FAILED)
							&& (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							&& StringUtils.isNotBlank(policyManagement.getRemediationPlaybookName()))
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);

					if ((policyManagement.getFindingStatus()
							.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_NOTIFIED))
							|| (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_SUPPRESSED))) {
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
					}
				}

			} else {
				if (policyManagement.getRemediationStatus()
						.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_PROCESSING))
					policyManagement.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_PROCESSING);
				else if (policyManagement.getRemediationStatus()
						.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_FAILED))
					policyManagement.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_FAILED);
				else if (policyManagement.getRemediationStatus()
						.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL))
					policyManagement.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL);
			}
		}

		if ("remediationStatus".equalsIgnoreCase(order)) {
			String orderType = orderMap.get(Order.dir);
			if (orderType.equalsIgnoreCase("asc".toLowerCase())) {
				policyManagementMastersList = policyManagementMastersList.stream()
						.sorted(Comparator.comparing(CspmFinding::getRemediationStatus)).collect(Collectors.toList());
			} else {
				policyManagementMastersList = policyManagementMastersList.stream()
						.sorted(Comparator.comparing(CspmFinding::getRemediationStatus).reversed())
						.collect(Collectors.toList());
			}
		} else {
			if (CollectionUtils.isNotEmpty(remediationStatusList))
				policyManagementMastersList = policyManagementMastersList.stream()
						.filter(p -> remediationStatusList.stream()
								.anyMatch(status -> status.equalsIgnoreCase(p.getRemediationStatus().toLowerCase())))
						.toList();
		}
		List<CspmFinding> clist = null;
//			if ("remediationPlaybookName".equalsIgnoreCase(order)) {
//				String orderType=	orderMap.get(Order.dir);
////				 Comparator<CspmFinding> ascendingNameComparator = Comparator
////			                .comparing(CspmFinding::getRemediationPlaybookName, Comparator.nullsFirst(Comparator.naturalOrder()));
////
////			     Comparator<CspmFinding> descendingNameComparator = ascendingNameComparator.reversed();
//
//				if(orderType.equalsIgnoreCase("asc".toLowerCase())){
//					policyManagementMastersList	= policyManagementMastersList.stream().sorted(new Comparator<CspmFinding>() {
//
//						@Override
//						public int compare(CspmFinding o1, CspmFinding o2) {
//							// TODO Auto-generated method stub
//							if (o1.getRemediationPlaybookName() == null && o2.getRemediationPlaybookName() == null) {
//					            return 0;
//					        }
//					        if (o1.getRemediationPlaybookName() == null) {
//					            return -1;
//					        }
//					        if (o2.getRemediationPlaybookName() == null) {
//					            return 1;
//					        }
//							return o1.getRemediationPlaybookName().compareTo(o2.getRemediationPlaybookName());
//						}
//						 
//					}).toList();
//				}else {
//					policyManagementMastersList	= policyManagementMastersList.stream().sorted(new Comparator<CspmFinding>() {
//
//						@Override
//						public int compare(CspmFinding o1, CspmFinding o2) {
//							// TODO Auto-generated method stub
//							if (o1.getRemediationPlaybookName() == null && o2.getRemediationPlaybookName() == null) {
//					            return 0;
//					        }
//					        if (o1.getRemediationPlaybookName() == null) {
//					            return 1;
//					        }
//					        if (o2.getRemediationPlaybookName() == null) {
//					            return -1;
//					        }
//							return o2.getRemediationPlaybookName().compareTo(o1.getRemediationPlaybookName());
//						}
//						 
//					}).toList();
//				      }		
//				}

		Map<String, Map<String, List<CspmFinding>>> groupedMap = policyManagementMastersList.stream()
				.collect(Collectors.groupingBy(CspmFinding::getSecurityControlId, LinkedHashMap::new, Collectors
						.groupingBy(CspmFinding::getCloudResourceId, LinkedHashMap::new, Collectors.toList())));

		Map<String, Map<String, Long>> groupingResult = policyManagementMastersList.stream()
				.collect(Collectors.groupingBy(CspmFinding::getSecurityControlId,
						Collectors.groupingBy(CspmFinding::getCloudResourceId, Collectors.counting())));

		// HashMap<String, Long> newMap = new HashMap<>();
		groupedMap.forEach((securityControlId, accountIdMap) -> {
			accountIdMap.forEach((cloudResourceId, cspmFindingList) -> {
				// Iterate through each CspmFinding in the list
				policyManagementFinalList.add(cspmFindingList.get(0));
			});
		});

		Map<String, Long> newMap = new HashMap<>();

		for (Map.Entry<String, Map<String, Long>> entry : groupingResult.entrySet()) {
			Map<String, Long> innerMap = entry.getValue();
			for (Map.Entry<String, Long> innerEntry : innerMap.entrySet()) {
				String combinedKey = entry.getKey();
				Long value = innerEntry.getValue();
				newMap.put(combinedKey, value);
			}
		}

		for (CspmFinding policyManagement : policyManagementFinalList) {
			policyManagement.setCount(newMap.getOrDefault(policyManagement.getSecurityControlId(), null));
		}

		List<Object> policyManagementMonitorList = new PolicyManagementMapper(policyManagementFinalList).map();
		long recordsFilteredTotal = policyManagementMasterPage.getTotalElements();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) recordsFilteredTotal);
		results.setRecordsFiltered((int) recordsFilteredTotal);
		results.setDraw(request.getDatatableInfo().getDraw());
		results.setLength(request.getDatatableInfo().getLength());
		results.setData(policyManagementMonitorList);

		/*
		 * if (Objects.nonNull(totalCount))
		 * results.setRecordsTotal(totalCount.intValue());
		 */
		return results;
	}

	@Override
	public Object getPolicyManagementListForHistoryFindings(String securityControlId, String cloudResourceId,
			PolicyManagementDetailsRequest request) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		// Long totalCount = null

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();

		int pageNumber = request.getDatatableInfo().getStart() / request.getDatatableInfo().getLength(); /// Calculate
																											/// /// pag
																											/// ///
																											/// number
		int length = request.getDatatableInfo().getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);

		DatatablePage<Object> results = new DatatablePage<>();
		List<CspmFinding> policyManagementMastersList = policyManagementRepository
				.findBySecurityControlIdAndCloudResourceIdAndTenantId(securityControlId, cloudResourceId,
						request.getTenantId(), pageable);

		List<RemediationPlaybookName> remediationPlaybookNameList = remediationPlaybookNameRepository.findAll();
		Map<String, String> PlaybookNameMap = remediationPlaybookNameList.stream()
				.collect(Collectors.toMap(te -> te.getSecurityControlId(), te -> te.getPlaybookName()));
		for (CspmFinding policyManagement : policyManagementMastersList) {
			policyManagement.setRemediationPlaybookName(
					PlaybookNameMap.getOrDefault(policyManagement.getSecurityControlId(), ""));
		}
		List<Object> policyManagementMonitorList = new PolicyManagementMapper(policyManagementMastersList).map();
		results.setData(policyManagementMonitorList);
		return results;
	}
	
	@Override
	public Object getPolicyManagementAzureListForHistoryFindings(String securityControlId, String cloudResourceId,
			PolicyManagementDetailsRequest request) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		// Long totalCount = null

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();

		int pageNumber = request.getDatatableInfo().getStart() / request.getDatatableInfo().getLength(); /// Calculate
																											/// /// pag
																											/// ///
																											/// number
		int length = request.getDatatableInfo().getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);

		DatatablePage<Object> results = new DatatablePage<>();
		List<CspmFinding> policyManagementMastersList = policyManagementRepository
				.findBySecurityControlIdAndCloudResourceIdAndTenantId(securityControlId, cloudResourceId,
						request.getTenantId(), pageable);

		List<Object> policyManagementMonitorList = new PolicyManagementAzureMapper(policyManagementMastersList).map();
		results.setData(policyManagementMonitorList);
		return results;
	}
	
	@Override
	public Object getPolicyManagementPlasmaCloudListForHistoryFindings(String securityControlId, String cloudResourceId,
			PolicyManagementDetailsRequest request) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		// Long totalCount = null

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();

		int pageNumber = request.getDatatableInfo().getStart() / request.getDatatableInfo().getLength(); /// Calculate
																											/// /// pag
																											/// ///
																											/// number
		int length = request.getDatatableInfo().getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);

		DatatablePage<Object> results = new DatatablePage<>();
		List<CspmFinding> policyManagementMastersList = policyManagementRepository
				.findBySecurityControlIdAndCloudResourceIdAndTenantId(securityControlId, cloudResourceId,
						request.getTenantId(), pageable);

		List<Object> policyManagementMonitorList = new PolicyManagementPrismaCloudMapper(policyManagementMastersList).map();
		results.setData(policyManagementMonitorList);
		return results;
	}
	

	@Override
	public Object getDataWithOptionalOrganizationID(PolicyManagementDetailsRequest request) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		// Long totalCount = null

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();

		int pageNumber = request.getDatatableInfo().getStart() / request.getDatatableInfo().getLength(); /// Calculate
																											/// page
																											/// number
		int length = request.getDatatableInfo().getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);

		DatatablePage<Object> results = new DatatablePage<>();
		List<CspmFinding> policyManagementMastersList = policyManagementRepository.findByColumns(request.getOrgIds(),
				pageable);
		List<Tenant> tenList = tenantRepository.findAll();
		List<RemediationPlaybookName> remediationPlaybookNameList = remediationPlaybookNameRepository.findAll();

		Map<Integer, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		for (CspmFinding policyManagement : policyManagementMastersList) {
			policyManagement.setTenantName(typeMap.getOrDefault(policyManagement.getTenantId(), ""));
		}

		Map<String, String> PlaybookNameMap = remediationPlaybookNameList.stream()
				.collect(Collectors.toMap(te -> te.getSecurityControlId(), te -> te.getPlaybookName()));
		for (CspmFinding policyManagement : policyManagementMastersList) {
			policyManagement.setRemediationPlaybookName(
					PlaybookNameMap.getOrDefault(policyManagement.getSecurityControlId(), ""));
		}

		List<Object> policyManagementMonitorList = new PolicyManagementMapper(policyManagementMastersList).map();
		results.setData(policyManagementMonitorList);
		return results;
	}

	@Override
	@Transactional
	public PolicyManagementFindingRemediationResponse remediateWithFindingId(BigInteger complianceFindingId,
			Integer userId) {
		PolicyManagementFindingRemediationResponse response = new PolicyManagementFindingRemediationResponse();

		try {
			if (userId == null || userId < 1) {
				response.setSuccess(false);
				response.setMessage("Invalid user Id");
				return response;
			}

			if (complianceFindingId == null || complianceFindingId.compareTo(BigInteger.valueOf(1)) < 0) {
				response.setSuccess(false);
				response.setMessage("Invalid finding Id");
				return response;
			}

			// get cspm finding id
			Optional<CspmFinding> optionalCspmFinding = policyManagementRepository.findByRecId(complianceFindingId);

			CspmFinding cspmFinding = optionalCspmFinding.orElseThrow(() -> new Exception("Invalid finding"));

			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);
			List<Integer> tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			if (!tenantIds.contains(cspmFinding.getTenantId())) {
				response.setSuccess(false);
				response.setMessage("Unauthorised access to finding");
				return response;
			}

			if (!AppConstants.CSPM_COMPILANCE_STATUS_FAILED.equalsIgnoreCase(cspmFinding.getComplianceStatus())) {
				response.setSuccess(false);
				response.setMessage(String.format(
						"Unable to initiate remediation as Compliance Status is not %s for \"%s\" and \"%s\".",
						AppConstants.CSPM_COMPILANCE_STATUS_FAILED, cspmFinding.getCloudResourceId(),
						cspmFinding.getSecurityControlId()));
				return response;
			}

			if (!AppConstants.CSPM_WORK_FLOW_STATUS_NEW.equalsIgnoreCase(cspmFinding.getFindingStatus())) {
				response.setSuccess(false);
				response.setMessage(String.format(
						"Unable to initiate remediation as Workflow Status is not %s for \"%s\" and \"%s\".",
						AppConstants.CSPM_WORK_FLOW_STATUS_NEW, cspmFinding.getCloudResourceId(),
						cspmFinding.getSecurityControlId()));
				return response;
			}

			// get playbook name
			Optional<String> optionalPlaybookName = remediationPlaybookNameRepository
					.findPlaybookNameBySecurityControlId(cspmFinding.getSecurityControlId());

			String remediationPlaybook = optionalPlaybookName
					.orElseThrow(() -> new Exception("Playbook not found for the specified security Control Id"));

			// global_settings
			List<GlobalSettings> globalSettings = globalSettingsRepo
					.findByParamType(AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_PROXY_SETTING).orElseThrow(
							() -> new IllegalArgumentException(String.format("No settings found for param type: %s",
									AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_PROXY_SETTING)));

			Map<String, String> globalSettingMapForProxy = globalSettings.stream()
					.collect(Collectors.toMap(GlobalSettings::getParamName, GlobalSettings::getParamValue));

			final String isProxyEnabled = globalSettingMapForProxy
					.getOrDefault(AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PROXY_ENABLED, "");
			final String proxyHostName = globalSettingMapForProxy
					.getOrDefault(AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PROXY_HOSTNAME, "");
			final String proxyPortString = globalSettingMapForProxy
					.getOrDefault(AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PROXY_PORTNUMBER, "");
			Integer proxyPortNumber = (StringUtils.isNumeric(proxyPortString)) ? Integer.parseInt(proxyPortString) : 0;
			final String isProxyAuth = globalSettingMapForProxy
					.getOrDefault(AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PROXY_IS_AUTHORIZED, "");
			final String proxyUsername = globalSettingMapForProxy
					.getOrDefault(AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PROXY_USERNAME, "");
			final String proxyPassword = SecurityUtils.decrypt256(
					globalSettingMapForProxy.getOrDefault(AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PROXY_PASSWORD, ""));

			// Tenant_Settings
			List<TenantSettings> tenantSettings = tenantSettingsRepository
					.findByParamTypeAndTenantId(AppConstants.TENANT_SETTINGS_PARAM_TYPE_PM_REMEDIATION,
							cspmFinding.getTenantId())
					.orElseThrow(
							() -> new IllegalArgumentException(String.format("No settings found for param type: %s",
									AppConstants.TENANT_SETTINGS_PARAM_TYPE_PM_REMEDIATION)));

			Map<String, String> pmRemediationTenantSettings = tenantSettings.stream()
					.collect(Collectors.toMap(TenantSettings::getParamName, TenantSettings::getParamValue));

			if (!"true".equalsIgnoreCase(
					pmRemediationTenantSettings.getOrDefault(AppConstants.PM_REMEDIATION_ENABLE, ""))) {
				response.setSuccess(false);
				response.setMessage("Policy Management Remediation is not enabled");
				return response;
			}

			final String pmRemediationServerAddress = pmRemediationTenantSettings
					.get(AppConstants.PM_REMEDIATION_SERVER_ADDRESS);

			if (StringUtils.isBlank(pmRemediationServerAddress)) {
				response.setSuccess(false);
				response.setMessage("No prerequisites/configuration found to get details required for findings");
				return response;
			}

			final String pmRemediationTenantid = pmRemediationTenantSettings
					.getOrDefault(AppConstants.PM_REMEDIATION_TENANTID, "");
			final String pmRemediationUseProxy = pmRemediationTenantSettings
					.getOrDefault(AppConstants.PM_REMEDIATION_USE_PROXY, "");
			final PolicyManagementRemediationClient policyManagementRemediationClient = new PolicyManagementRemediationClient(
					pmRemediationServerAddress, pmRemediationTenantid,
					"true".equalsIgnoreCase(pmRemediationUseProxy) && "true".equalsIgnoreCase(isProxyEnabled),
					proxyHostName, proxyPortNumber, "true".equalsIgnoreCase(isProxyAuth), proxyUsername, proxyPassword,
					60000);

			// client
			JSONObject launchRunbookResponse = new JSONObject();
			try {
				launchRunbookResponse = policyManagementRemediationClient
						.launchRunbook(cspmFinding.getCloudResourceId(), remediationPlaybook).orElse(new JSONObject());
			} catch (Exception e) {
				log.error(String.format("Unable to launch runbook for cloudResourceId %s with playbook %s due to %s",
						cspmFinding.getCloudResourceId(), remediationPlaybook, e.getMessage()), e);
				response.setSuccess(false);
				response.setMessage("Remediation Runbook launch Failed");
				return response;
			}

			if (!PolicyManagementRemediationClient.OK_STATUS_CODES
					.contains(launchRunbookResponse.optInt("statusCode", 0))) {
				log.error(String.format(
						"Unable to launch runbook for cloudResourceId %s with remediationPlaybook %s from Policy Management Remediate as response is %s",
						cspmFinding.getCloudResourceId(), remediationPlaybook, launchRunbookResponse));
				response.setSuccess(false);
				response.setMessage("Remediation initiation failed");
				return response;
			}

			JSONObject launchRunbookResponseContent = launchRunbookResponse.optJSONObject("content");
			if (launchRunbookResponseContent.optInt("status", 0) != 200) {
				log.error(String.format(
						"Unable to launch runbook for cloudResourceId %s with remediationPlaybook %s from Policy Management Remediate as response is %s",
						cspmFinding.getCloudResourceId(), remediationPlaybook, launchRunbookResponse));
				response.setSuccess(false);
				response.setMessage(String.format("Remediation failed due to %s",
						launchRunbookResponseContent.optString("message", "unknown error")));
				return response;
			}

			// Check if remediation is already in progress for the given conditions
			CspmFinding existingRemediationStatus = policyManagementRepository
					.findByTenantIdAndCloudResourceIdAndSecurityControlIdAndRemediationStatus(cspmFinding.getTenantId(),
							cspmFinding.getCloudResourceId(), cspmFinding.getSecurityControlId(),
							AppConstants.CSPM_FINDING_STATUS_PROCESSING);

			if (existingRemediationStatus != null) {
				response.setSuccess(false);
				response.setMessage(String.format(
						"Remediation is already in progress for Cloud Resource ID: %s and Security Control ID: %s",
						cspmFinding.getCloudResourceId(), cspmFinding.getSecurityControlId()));
				return response;
			}

			// set RemediationStatus as In Progress
			cspmFinding.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_PROCESSING);
			cspmFinding = policyManagementRepository.save(cspmFinding);

			if (cspmFinding == null) {
				response.setSuccess(false);
				response.setMessage("Failed to Initiated the Remediation");
				return response;
			}

			// status handling - set latest = 0
			int updatedRowCount = cspmRemediationStatusRepository
					.updateLatestToFalseByCspmFindingId(complianceFindingId);

			// status handling - set latest = 1
			CspmRemediationStatus cspmRemediationStatusForProof = new CspmRemediationStatus();
			cspmRemediationStatusForProof.setTenantId(cspmFinding.getTenantId());
			cspmRemediationStatusForProof.setAisaacTenantId(pmRemediationTenantid);
			cspmRemediationStatusForProof.setCloudResourceId(cspmFinding.getCloudResourceId());
			cspmRemediationStatusForProof.setSecurityControlId(cspmFinding.getSecurityControlId());
			cspmRemediationStatusForProof.setPlaybookName(remediationPlaybook);
			cspmRemediationStatusForProof.setStatus(AppConstants.CSPM_FINDING_STATUS_PROCESSING);
			cspmRemediationStatusForProof.setCspmFindingId(cspmFinding.getRecId());
			cspmRemediationStatusForProof.setLatest(true);
			cspmRemediationStatusForProof.setCreatedDate(new Date());
			cspmRemediationStatusForProof.setUpdatedDate(new Date());
			cspmRemediationStatusForProof.setCreatorUser(userId);

			cspmRemediationStatusRepository.save(cspmRemediationStatusForProof);

		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			log.error("An error occurred", e);
		}

		response.setSuccess(true);
		response.setMessage("Remediation Initiated successfully");
		return response;
	}

	@Override
	public PolicyManagementRemediationRefreshResponse remediateFindingWithRefreshStatus(BigInteger complianceFindingId,
			Integer userId) {
		PolicyManagementRemediationRefreshResponse response = new PolicyManagementRemediationRefreshResponse();

		try {
			if (userId == null || userId < 1) {
				response.setSuccess(false);
				response.setMessage("Invalid user Id");
				return response;
			}

			// Get CspmFinding details
			Optional<CspmFinding> cspmFindingOptional = policyManagementRepository.findByRecId(complianceFindingId);

			CspmFinding cspmFinding = cspmFindingOptional.orElseThrow(() -> new Exception("Invalid finding"));

			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);
			List<Integer> tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			if (!tenantIds.contains(cspmFinding.getTenantId())) {
				response.setSuccess(false);
				response.setMessage("Unauthorised access to finding");
				return response;
			}

			// Get Remediation Status
			Optional<String> remediationStatusOptional = cspmRemediationStatusRepository
					.findStatusByCspmFindingIdAndLatestOrderByCreatedDateDesc(complianceFindingId, true);

			String remediationStatus = remediationStatusOptional
					.orElseThrow(() -> new Exception("Remediation status is invalid"));
			String complianceStatus = cspmFinding.getComplianceStatus();

			if (remediationStatus.equals(AppConstants.CSPM_FINDING_STATUS_PROCESSING)) {
				response.setStatus(AppConstants.CSPM_FINDING_STATUS_PROCESSING);
				response.setMessage("Remediation still in progress");
				response.setComplianceStatus(complianceStatus);
			} else if (remediationStatus.equals(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL)) {
				response.setStatus(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL);
				response.setMessage("Remediation completed");
				response.setComplianceStatus(complianceStatus);
			} else if (remediationStatus.equals(AppConstants.CSPM_FINDING_STATUS_FAILED)) {
				response.setStatus(AppConstants.CSPM_FINDING_STATUS_FAILED);
				response.setMessage("Remediation has failed. Please click on Remediate to retry");
				response.setComplianceStatus(complianceStatus);
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			log.error("An error occurred", e);
			return response;
		}

		response.setSuccess(true);
		return response;
	}

	@Override
	public Map<String, Object> getUserCustomization(Long createdBy, String moduleName, String sectionName) {
		Optional<String> returnable = Optional.empty();
		Optional<String> returnableBydefault = Optional.empty();
		Map<String, Object> pmMap = new HashMap<>();
		List<UserCustomFieldList> list = this.userCustomFieldListRepo
				.findUserCustomization(createdBy, moduleName, sectionName)
				.orElseThrow(() -> new ValidationException(String.format(
						ResponseMsgConstants.FIND_USER_CUSTOMIZATION_FAILURE, moduleName, sectionName, createdBy)));
		if (CollectionUtils.isEmpty(list)) {
			throw new ValidationException(String.format(ResponseMsgConstants.FIND_USER_CUSTOMIZATION_FAILURE,
					moduleName, sectionName, createdBy));
		}

		Predicate<UserCustomFieldList> isUserCustom = e -> Objects.nonNull(e.getCreatedBy());

		returnable = Optional.ofNullable(
				list.stream().filter(isUserCustom).findAny().map(UserCustomFieldList::getFieldDetails).orElse(null));
		pmMap.put("userCustomizedColumn", returnable);

		if (returnable.isEmpty())
			returnableBydefault = Optional.ofNullable(list.stream().filter(isUserCustom.negate()).findAny()
					.map(UserCustomFieldList::getFieldDetails).orElse(null));
		pmMap.put("defaultAssetsCustomizedColumn", returnableBydefault);

		return pmMap;
	}

	@Override
	@Transactional
	public String updateOrAddUserCustomColumn(Long createdBy, String moduleName, String sectionName,
			String userCustomizedJson) {

		UserCustomFieldList dto = this.userCustomFieldListRepo
				.findByCreatedByAndModuleNameAndSectionName(createdBy, moduleName, sectionName).orElse(null);
		// save
		if (Objects.isNull(dto))
			return userCustomFieldListRepo
					.save(UserCustomFieldList.builder().moduleName(moduleName).sectionName(sectionName)
							.fieldDetails(userCustomizedJson).createdBy(createdBy).createdDate(new Date()).build())
					.getFieldDetails();
		// update
		dto.setFieldDetails(userCustomizedJson);
		dto.setUpdatedDate(new Date());
		return this.userCustomFieldListRepo.save(dto).getFieldDetails();
	}

	@Override
	@Transactional
	public void deleteUserCustomColumn(Long createdBy, String moduleName, String sectionName) {

		UserCustomFieldList dto = this.userCustomFieldListRepo
				.findByCreatedByAndModuleNameAndSectionName(createdBy, moduleName, sectionName).orElseThrow(
						() -> new ValidationException(String.format(ResponseMsgConstants.USER_CUSTOMIZATION_NOT_FOUND,
								moduleName, sectionName, createdBy)));
		if (Objects.isNull(dto)) {
			throw new ValidationException(String.format(ResponseMsgConstants.FIND_USER_CUSTOMIZATION_FAILURE,
					moduleName, sectionName, createdBy));
		}

		this.userCustomFieldListRepo.delete(dto);
	}

	@Override
	public Object getcspmFindingList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		Long totalCount = null;

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		Map<String, Object> collectedMap = null;
		Map<String, Long> countMapList = new HashMap<>();

		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();
		int pageNumber = 0;
		int length = 0;
		if (!isExport) {
			pageNumber = request.getDatatableInfo().getStart();
			length = request.getDatatableInfo().getLength();
		} else {
			pageNumber = request.getPart(); /// Calculate page number
			length = request.getLength();
		}

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);
		if (sortColumn.equalsIgnoreCase("remediationStatus")) {
			pageable = PageRequest.of(pageNumber, length);
		}

		Map<String, Object> resultMap = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}

		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			List<Integer> tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}
			// handled for single tenant 
			if (CollectionUtils.isEmpty(request.getOrgIds())) {
				request.setOrgIds(tenantIds);
			}
			
			String remPlaybookName = (String) collectedMap.get("remPlaybookName");

			// for advanced search and full search
			if (StringUtils.isNotEmpty(remPlaybookName)) {
				List<String> securityControlIdList = remediationPlaybookNameRepository
						.findAllByColumnName(remPlaybookName);
				request.setSearchSecurityControlIds(securityControlIdList);
			}
		}
		List<String> remediationStatusList = null;
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			remediationStatusList = (List<String>) collectedMap.get("remediationStatus");
		}

		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME);
		
		Optional<GlobalSettings> optionalVendorName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_VENDOR_PARAM_NAME);
		
		Optional<GlobalSettings> optionalProductName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_PRODUCT_PARAM_NAME);

		String paramValue = optional.get().getParamValue();
		String awsVendorParamValue = optionalVendorName.get().getParamValue();
		String awsProductNameParamValue = optionalProductName.get().getParamValue();

		
		Optional<GlobalSettings> defaultSearch = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT);

		String defaultSearchDays = defaultSearch.get().getParamValue();

		switch (request.getSearchType()) {
		case 1: {
			resultMap = this.getCountByCspmFinding(collectedMap, false, request, pageable, paramValue,defaultSearchDays,
					AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,awsVendorParamValue,awsProductNameParamValue);
			break;
		}
		case 3: {
			resultMap = this.getCountByCspmFinding(collectedMap, true, request, pageable, paramValue,defaultSearchDays,
					AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,awsVendorParamValue,awsProductNameParamValue);
			break;
		}
		default:
		}

		List<Tenant> tenList = tenantRepository.findAll();

		return getPolicyManagementDetailsWithRequiredList(tenList, request, resultMap, remediationStatusList);

	}

	public Object getPolicyManagementDetailsWithRequiredList(List<Tenant> teList,
			PolicyManagementDetailsRequest request, Map<String, Object> resultMap, List<String> remediationStatusList) {

		List<PolicyManagementDTO> resultList = (List<PolicyManagementDTO>) resultMap.get("resultList");

		List<Object> policyManagementMonitorList = new PolicyManagementMapperList(resultList).map();

		List<PolicyManagementResponse> policyManagementMastersList = new ArrayList<>();

		for (Object obj : policyManagementMonitorList) {
			if (obj instanceof PolicyManagementResponse) {
				policyManagementMastersList.add((PolicyManagementResponse) obj);
			}
		}

		Map<Integer, String> typeMap = teList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));

		for (PolicyManagementResponse policyManagement : policyManagementMastersList) {
			policyManagement.setTenantName(typeMap.getOrDefault(policyManagement.getTenantId(), ""));
		}

		List<RemediationPlaybookName> remediationPlaybookNameList = remediationPlaybookNameRepository.findAll();
		Map<String, String> PlaybookNameMap = remediationPlaybookNameList.stream()
				.collect(Collectors.toMap(te -> te.getSecurityControlId(), te -> te.getPlaybookName()));
		for (PolicyManagementDTO policyManagement : resultList) {
			policyManagement.getCspmFinding().setRemediationPlaybookName(
					PlaybookNameMap.getOrDefault(policyManagement.getCspmFinding().getSecurityControlId(), null));
		}

		Map<Order, String> orderMap = request.getDatatableInfo().getOrder().get(0);
		String order = orderMap.get(Order.name);
		// find list according to condition passed in remediation status
		for (PolicyManagementResponse policyManagement : policyManagementMastersList) {
			if (StringUtils.isBlank(policyManagement.getRemediationStatus())) {
				if (StringUtils.isBlank(policyManagement.getRemediationPlaybookName())) {
					policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
				} else {
					if (policyManagement.getComplianceStatus()
							.equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_PASSED)
							&& (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							|| (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_RESOLVED))) {
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
					}

					if (policyManagement.getComplianceStatus()
							.equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_FAILED)
							&& (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							|| (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_RESOLVED))
									&& StringUtils.isNotBlank(policyManagement.getRemediationPlaybookName()))
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NEW);
					else if (policyManagement.getComplianceStatus()
							.equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_FAILED)
							&& (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							&& StringUtils.isNotBlank(policyManagement.getRemediationPlaybookName()))
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);

					if ((policyManagement.getFindingStatus()
							.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_NOTIFIED))
							|| (policyManagement.getFindingStatus()
									.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_SUPPRESSED))) {
						policyManagement.setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
					}
				}

			} else {
				if (policyManagement.getRemediationStatus()
						.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_PROCESSING))
					policyManagement.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_PROCESSING);
				else if (policyManagement.getRemediationStatus()
						.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_FAILED))
					policyManagement.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_FAILED);
				else if (policyManagement.getRemediationStatus()
						.equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL))
					policyManagement.setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL);
			}
		}

		if ("remediationStatus".equalsIgnoreCase(order)) {
			String orderType = orderMap.get(Order.dir);
			if (orderType.equalsIgnoreCase("asc".toLowerCase())) {
				policyManagementMastersList = policyManagementMastersList.stream()
						.sorted(Comparator.comparing(PolicyManagementResponse::getRemediationStatus))
						.collect(Collectors.toList());
			} else {
				policyManagementMastersList = policyManagementMastersList.stream()
						.sorted(Comparator.comparing(PolicyManagementResponse::getRemediationStatus).reversed())
						.collect(Collectors.toList());
			}
		}

		if (CollectionUtils.isNotEmpty(remediationStatusList))
			policyManagementMastersList = policyManagementMastersList.stream()
					.filter(p -> remediationStatusList.stream()
							.anyMatch(status -> status.equalsIgnoreCase(p.getRemediationStatus().toLowerCase())))
					.toList();

		List<Object> policyManagementFinalList = new ArrayList<>(policyManagementMastersList);
		Long totalCountValue = (Long) resultMap.get("totalCount");
		Integer totalCount = totalCountValue.intValue(); // Convert Long to Integer

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal(totalCount);
		results.setRecordsFiltered(totalCount);
		results.setDraw(request.getDatatableInfo().getDraw());
		results.setLength(request.getDatatableInfo().getLength());
		results.setData(policyManagementFinalList);
		return results;
	}

	@Override
	public Map<String, Object> getCountByCspmFinding(Map<String, Object> collectedMap, boolean conditionCheck,
			PolicyManagementDetailsRequest request, Pageable pageable, String sourceName,String defaultSearchDays,String recordState, String vendorName, String productName) {
		
		if (collectedMap == null) {
			collectedMap = new HashMap<>();
		}
		String complianceStandards = (String) collectedMap.get("standards");
		String finding = (String) collectedMap.get("finding");
		String cloudAccountId = (String) collectedMap.get("tenantId");
		String securityControlId = (String) collectedMap.get("securityControlId");
		String remPlaybookName = (String) collectedMap.get("remPlaybookName");
		String cloudResourceId = (String) collectedMap.get("cloudResourceId");
		List<String> severity = (List<String>) collectedMap.get("severity");
		List<String> complianceStatus = (List<String>) collectedMap.get("complianceStatus");
		List<String> workFlowStatus = (List<String>) collectedMap.get("workflowStatus");
		List<String> remediationStatus = (List<String>) collectedMap.get("remediationStatus");
		Object reportedBetween = collectedMap.get("reportedBetween");
		if (reportedBetween == null) {
			reportedBetween = new HashMap<>();
		}
		Map<String, Object> reportedDate = ((Map<String, String>) reportedBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Object createdBetween = collectedMap.get("createdBetween");
		if (createdBetween == null) {
			createdBetween = new HashMap<>();
		}
		Map<String, Object> createdDate = ((Map<String, String>) createdBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Object updatedBetween = collectedMap.get("updatedBetween");
		if (updatedBetween == null) {
			updatedBetween = new HashMap<>();
		}
		Map<String, Object> updatedDate = ((Map<String, String>) updatedBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<Integer> tenantIds = request.getOrgIds();
		
		List<String> securityControlIds = request.getSearchSecurityControlIds();
		String reportedBetweenFrom = ObjectUtils.isNotEmpty(reportedDate.get("from"))
				? String.valueOf(reportedDate.get("from"))
				: null;
		String reportedBetweenTo = ObjectUtils.isNotEmpty(reportedDate.get("to"))
				? String.valueOf(reportedDate.get("to"))
				: null;
		String createdBetweenFrom = ObjectUtils.isNotEmpty(createdDate.get("from"))
				? String.valueOf(createdDate.get("from"))
				: null;
		String createdBetweenTo = ObjectUtils.isNotEmpty(createdDate.get("to")) ? String.valueOf(createdDate.get("to"))
				: null;
		String updatedBetweenFrom = ObjectUtils.isNotEmpty(updatedDate.get("from"))
				? String.valueOf(updatedDate.get("from"))
				: null;
		String updatedBetweenTo = ObjectUtils.isNotEmpty(updatedDate.get("to")) ? String.valueOf(updatedDate.get("to"))
				: null;

		String sortColumn = "", sortOrderBy = "";
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();
		
		if(createdBetweenFrom == null && createdBetweenTo == null){
			DateFormat dateFormatter = new SimpleDateFormat(AppConstants.DATETIME_FORMAT_STR);
			Date currentDate = new Date();
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(currentDate);

	        // Subtract 90 days from the current date
	        calendar.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(defaultSearchDays)));

	        // Get the updated Date after subtracting 90 days
	        Date date90DaysBefore = calendar.getTime();
			String currentDateTime = dateFormatter.format(currentDate);
			createdBetweenFrom =dateFormatter.format(date90DaysBefore);
		        // Subtract 90 days from the current date
			createdBetweenTo  = currentDateTime;
		}
	    
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}
		int pageSize = pageable.getPageSize(); // This is your limit
		int currentPage = pageable.getPageNumber(); // This is your offset

		// Calculate the offset based on the current page and page size
		int offset = currentPage;

		String countQuery = "SELECT COUNT(DISTINCT concat(e.securityControlId,'_',e.cloudResourceId)) FROM CspmFinding e "
				+ "LEFT JOIN RemediationPlaybookName re on e.securityControlId = re.securityControlId "
				+ "WHERE (e.createdDate BETWEEN :createdBetweenFrom AND :createdBetweenTo) AND e.tenantId IN (:tenantIds) AND e.recordState =:recordState AND e.vendorName =:vendorName AND e.productName =:productName AND e.sourceName =:sourceName ";

		// Add conditions for finding, complianceStandards, etc., as needed

		String query = "SELECT new aisaac.dto.PolicyManagementDTO(e, count(e.recId),re.playbookName) "
				+ "FROM CspmFinding e "
				+ "LEFT JOIN RemediationPlaybookName re on e.securityControlId = re.securityControlId "
				+ "WHERE (e.createdDate BETWEEN :createdBetweenFrom AND :createdBetweenTo) AND e.tenantId IN (:tenantIds) AND e.recordState =:recordState AND e.vendorName =:vendorName AND e.productName =:productName AND e.sourceName = :sourceName ";

		if (conditionCheck) {

			if (updatedBetweenFrom != null && updatedBetweenTo != null) {
				query += "AND (e.updatedAt BETWEEN :updatedBetweenFrom AND :updatedBetweenTo) ";
				countQuery += "AND (e.updatedAt BETWEEN :updatedBetweenFrom AND :updatedBetweenTo) ";

			}
			if (reportedBetweenFrom != null && reportedBetweenTo != null) {
				query += "AND (e.createdAt BETWEEN :reportedBetweenFrom AND :reportedBetweenTo) ";
				countQuery += "AND (e.createdAt BETWEEN :reportedBetweenFrom AND :reportedBetweenTo) ";

			}

			if (finding != null && !finding.isEmpty()) {
				query += "AND (e.finding LIKE :finding) ";
				countQuery += "AND (e.finding LIKE :finding) ";

			}

			if (remPlaybookName != null && !remPlaybookName.isEmpty()) {
				query += "AND (re.playbookName LIKE :remPlaybookName) ";
				countQuery += "AND (re.playbookName LIKE :remPlaybookName) ";

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				query += "AND (e.complianceStandards LIKE :complianceStandards) ";
				countQuery += "AND (e.complianceStandards LIKE :complianceStandards) ";

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				query += "AND (e.securityControlId LIKE :securityControlId) ";
				countQuery += "AND (e.securityControlId LIKE :securityControlId) ";

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				query += "AND (e.cloudAccountId LIKE :cloudAccountId) ";
				countQuery += "AND (e.cloudAccountId LIKE :cloudAccountId) ";

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				query += "AND (e.cloudResourceId LIKE :cloudResourceId) ";
				countQuery += "AND (e.cloudResourceId LIKE :cloudResourceId) ";

			}

			if (CollectionUtils.isNotEmpty(remediationStatus)) {
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW) && !remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NA)) {
					query += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) ";
					countQuery += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) ";				
				}
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NA) && !remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW)) {
					query += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "'))) ";
					countQuery += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "'))) ";
				}
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW) && remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NA)) {
					query += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL))) ";
					countQuery += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL))) ";
				}
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW) && (remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_PROCESSING) || remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_FAILED)
						|| remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL))) {
					query += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) OR (e.remediationStatus IN (:remediationStatus)) ";
					countQuery += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) OR (e.remediationStatus IN (:remediationStatus)) ";				
				}
				
				if (remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_PROCESSING) || remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_FAILED)
						|| remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL)) {
					query += "AND (e.remediationStatus IN (:remediationStatus)) ";
					countQuery += "AND (e.remediationStatus IN (:remediationStatus)) ";
				}
			 }

			if (CollectionUtils.isNotEmpty(severity)) {
				query += "AND (e.severity IN (:severity)) ";
				countQuery += "AND (e.severity IN (:severity)) ";

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				query += "AND (e.complianceStatus IN (:complianceStatus)) ";
				countQuery += "AND (e.complianceStatus IN (:complianceStatus)) ";

			}
			if (CollectionUtils.isNotEmpty(workFlowStatus)) {
				query += "AND (e.findingStatus IN (:workFlowStatus)) ";
				countQuery += "AND (e.findingStatus IN (:workFlowStatus)) ";

			}

		} else {
			
			query += "AND (";
			countQuery += "AND (";


			if (finding != null && !finding.isEmpty()) {
				query += "(e.finding LIKE :finding) ";
				countQuery += "(e.finding LIKE :finding) ";

			}
			if (CollectionUtils.isNotEmpty(remediationStatus)) {
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW) && !remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NA)) {
					query += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) ";
					countQuery += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) ";				
				}
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NA) && !remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW)) {
					query += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "'))) ";
					countQuery += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "'))) ";
				}
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW) && remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NA)) {
					query += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL))) ";
					countQuery += "AND ((re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "') AND re.playbookName IS NULL) OR (e.complianceStatus IN ('" + AppConstants.CSPM_COMPILANCE_STATUS_PASSED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "','" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_NOTIFIED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_SUPPRESSED + "')) OR (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL))) ";
				}
				if (remediationStatus.contains(AppConstants.CSPM_REMEDIATION_STATUS_NEW) && (remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_PROCESSING) || remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_FAILED)
						|| remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL))) {
					query += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) OR (e.remediationStatus IN (:remediationStatus)) ";
					countQuery += "AND (e.complianceStatus IN ('" + AppConstants.CSPM_FINDING_STATUS_FAILED + "') AND e.findingStatus IN ('" + AppConstants.CSPM_REMEDIATION_STATUS_NEW + "', '" + AppConstants.CSPM_FINDING_STATUS_RESOLVED + "') AND (re.playbookName IS NOT NULL)) OR (e.remediationStatus IN (:remediationStatus)) ";				
				}
				
				if (remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_PROCESSING) || remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_FAILED)
						|| remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL)) {
					query += "AND (e.remediationStatus IN (:remediationStatus)) ";
					countQuery += "AND (e.remediationStatus IN (:remediationStatus)) ";
				}
			 }
					

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				query += "OR (e.complianceStandards LIKE :complianceStandards) ";
				countQuery += "OR (e.complianceStandards LIKE :complianceStandards) ";

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				query += "OR (e.securityControlId LIKE :securityControlId) ";
				countQuery += "OR (e.securityControlId LIKE :securityControlId) ";

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				query += "OR (e.cloudAccountId LIKE :cloudAccountId) ";
				countQuery += "OR (e.cloudAccountId LIKE :cloudAccountId) ";

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				query += "OR (e.cloudResourceId LIKE :cloudResourceId) ";
				countQuery += "OR (e.cloudResourceId LIKE :cloudResourceId) ";

			}
			if (CollectionUtils.isNotEmpty(severity)) {
				query += "OR (e.severity IN (:severity)) ";
				countQuery += "OR (e.severity IN (:severity)) ";

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				query += "OR (e.complianceStatus IN (:complianceStatus)) ";
				countQuery += "OR (e.complianceStatus IN (:complianceStatus)) ";

			}
			if (CollectionUtils.isNotEmpty(workFlowStatus)) {
				query += "OR (e.findingStatus IN (:workFlowStatus)) ";
				countQuery += "OR (e.findingStatus IN (:workFlowStatus)) ";

			}

			if (remPlaybookName != null && !remPlaybookName.isEmpty()) {
				query += "OR (re.playbookName LIKE :remPlaybookName) ";
				countQuery += "OR (re.playbookName LIKE :remPlaybookName) ";

			}
			query += ")";
			countQuery += ")";
		}
		query += " GROUP BY e.securityControlId, e.cloudResourceId ";
		//countQuery += " GROUP BY e.securityControlId, e.cloudResourceId ";

		if (sortColumn != null && !sortColumn.isEmpty()) {
			if (sortColumn.equalsIgnoreCase("remediationPlaybookName")) {
				query += "ORDER BY re.playbookName ";
			} else {
				query += "ORDER BY e." + sortColumn + " ";
			}

			if ("desc".equalsIgnoreCase(sortOrderBy)) {
				query += "DESC ";

			} else {
				query += "ASC ";

			}
		}

		TypedQuery<PolicyManagementDTO> typedQuery = entityManager.createQuery(query, PolicyManagementDTO.class);
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery, Long.class);

		if(conditionCheck) {
			
			if (finding != null && !finding.isEmpty()) {
				typedQuery.setParameter("finding", "%" +finding+ "%");
				countTypedQuery.setParameter("finding", "%" +finding+ "%");
			}

			if (remPlaybookName != null && !remPlaybookName.isEmpty()) {
				typedQuery.setParameter("remPlaybookName", "%" +remPlaybookName+ "%");
			countTypedQuery.setParameter("remPlaybookName", "%" +remPlaybookName+ "%");

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				typedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");
				countTypedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				typedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");
				countTypedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				typedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");
				countTypedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				typedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");
				countTypedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");

			}
			
			if (CollectionUtils.isNotEmpty(severity)) {
				typedQuery.setParameter("severity", severity);
				countTypedQuery.setParameter("severity", severity);

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				typedQuery.setParameter("complianceStatus", complianceStatus);
				countTypedQuery.setParameter("complianceStatus", complianceStatus);

			}
			if (CollectionUtils.isNotEmpty(workFlowStatus)) {
				typedQuery.setParameter("workFlowStatus", workFlowStatus);
				countTypedQuery.setParameter("workFlowStatus", workFlowStatus);

			}
			if (CollectionUtils.isNotEmpty(remediationStatus)) {
				if (remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_PROCESSING) || remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_FAILED)
						|| remediationStatus.contains(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL)) {
				typedQuery.setParameter("remediationStatus", remediationStatus);
				countTypedQuery.setParameter("remediationStatus", remediationStatus);
				}
			}

			if (tenantIds != null && !tenantIds.isEmpty()) {
				typedQuery.setParameter("tenantIds", tenantIds);
				countTypedQuery.setParameter("tenantIds", tenantIds);

			}

			if (reportedBetweenFrom != null && reportedBetweenTo != null) {
				reportedBetweenFrom = reportedBetweenFrom.replace(" ", "T");
				reportedBetweenTo = reportedBetweenTo.replace(" ", "T");
				typedQuery.setParameter("reportedBetweenFrom", LocalDateTime.parse(reportedBetweenFrom));
				typedQuery.setParameter("reportedBetweenTo", LocalDateTime.parse(reportedBetweenTo));
				countTypedQuery.setParameter("reportedBetweenFrom", LocalDateTime.parse(reportedBetweenFrom));
				countTypedQuery.setParameter("reportedBetweenTo", LocalDateTime.parse(reportedBetweenTo));
			}
			
			if (updatedBetweenFrom != null && updatedBetweenTo != null) {
				updatedBetweenFrom = updatedBetweenFrom.replace(" ", "T");
				updatedBetweenTo = updatedBetweenTo.replace(" ", "T");
				typedQuery.setParameter("updatedBetweenFrom", LocalDateTime.parse(updatedBetweenFrom));
				typedQuery.setParameter("updatedBetweenTo", LocalDateTime.parse(updatedBetweenTo));
				countTypedQuery.setParameter("updatedBetweenFrom", LocalDateTime.parse(updatedBetweenFrom));
				countTypedQuery.setParameter("updatedBetweenTo", LocalDateTime.parse(updatedBetweenTo));
			}
		}else {
			if (finding != null && !finding.isEmpty()) {
				typedQuery.setParameter("finding", "%" +finding+ "%");
				countTypedQuery.setParameter("finding", "%" +finding+ "%");

			}

			if (remPlaybookName != null && !remPlaybookName.isEmpty()) {
				typedQuery.setParameter("remPlaybookName", "%" +remPlaybookName+ "%");
			countTypedQuery.setParameter("remPlaybookName", "%" +remPlaybookName+ "%");

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				typedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");
				countTypedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				typedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");
				countTypedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				typedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");
				countTypedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				typedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");
				countTypedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");

			}

			if (tenantIds != null && !tenantIds.isEmpty()) {
				typedQuery.setParameter("tenantIds", tenantIds);
				countTypedQuery.setParameter("tenantIds",tenantIds);

			}

		}
		
		if (createdBetweenFrom != null && createdBetweenTo != null) {
			createdBetweenFrom = createdBetweenFrom.replace(" ", "T");
			createdBetweenTo = createdBetweenTo.replace(" ", "T");
			typedQuery.setParameter("createdBetweenFrom", LocalDateTime.parse(createdBetweenFrom));
			typedQuery.setParameter("createdBetweenTo", LocalDateTime.parse(createdBetweenTo));
			countTypedQuery.setParameter("createdBetweenFrom", LocalDateTime.parse(createdBetweenFrom));
			countTypedQuery.setParameter("createdBetweenTo", LocalDateTime.parse(createdBetweenTo));
		}
		
		typedQuery.setParameter("recordState", recordState);
		countTypedQuery.setParameter("recordState", recordState);
		
		typedQuery.setParameter("vendorName", vendorName);
		countTypedQuery.setParameter("vendorName", vendorName);
		
		typedQuery.setParameter("productName", productName);
		countTypedQuery.setParameter("productName", productName);
		
		typedQuery.setParameter("sourceName", sourceName);
		countTypedQuery.setParameter("sourceName", sourceName);

		typedQuery.setFirstResult(offset); // Set the offset
		typedQuery.setMaxResults(pageSize); // Set the limit

		List<PolicyManagementDTO> resultList = typedQuery.getResultList();

		List<Long> totalCountList = countTypedQuery.getResultList();
		Long totalCount = totalCountList.get(0);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("resultList", resultList);
		resultMap.put("totalCount", totalCount);

		return resultMap;
	}
	
	//azure findings
	@Override
	public Object getcspmFindingAzureList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		Long totalCount = null;

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		Map<String, Object> collectedMap = null;
		Map<String, Long> countMapList = new HashMap<>();

		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();
		int pageNumber = 0;
		int length = 0;
		if (!isExport) {
			pageNumber = request.getDatatableInfo().getStart();
			length = request.getDatatableInfo().getLength();
		} else {
			pageNumber = request.getPart(); /// Calculate page number
			length = request.getLength();
		}

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);


		Map<String, Object> resultMap = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}

		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			List<Integer> tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}
			// handled for single tenant 
			if (CollectionUtils.isEmpty(request.getOrgIds())) {
				request.setOrgIds(tenantIds);
			}
			
		}
 		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		}

		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME);
		
		Optional<GlobalSettings> optionalVendorName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AZURE_VENDOR_PARAM_NAME);
		
		Optional<GlobalSettings> optionalProductName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AZURE_PRODUCT_PARAM_NAME);

		String paramValue = optional.get().getParamValue();
		String azureVendorParamValue = optionalVendorName.get().getParamValue();
		String azureProductNameParamValue = optionalProductName.get().getParamValue();

		
		Optional<GlobalSettings> defaultSearch = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT);

		String defaultSearchDays = defaultSearch.get().getParamValue();

		switch (request.getSearchType()) {
		case 1: {
			resultMap = this.getCountByCspmFindingAzure(collectedMap, false, request, pageable, paramValue,defaultSearchDays,
					azureVendorParamValue,azureProductNameParamValue, AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		case 3: {
			resultMap = this.getCountByCspmFindingAzure(collectedMap, true, request, pageable, paramValue,defaultSearchDays,
					azureVendorParamValue,azureProductNameParamValue, AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		default:
		}

		List<Tenant> tenList = tenantRepository.findAll();

		return getPolicyManagementDetailsWithRequiredAzureList(tenList, request, resultMap);

	}

	public Object getPolicyManagementDetailsWithRequiredAzureList(List<Tenant> teList,
			PolicyManagementDetailsRequest request, Map<String, Object> resultMap) {

		List<PolicyManagementAzureDTO> resultList = (List<PolicyManagementAzureDTO>) resultMap.get("resultList");

		List<Object> policyManagementMonitorList = new PolicyManagementAzureMapperList(resultList).map();

		List<PolicyManagementAzureResponse> policyManagementMastersList = new ArrayList<>();

		for (Object obj : policyManagementMonitorList) {
			if (obj instanceof PolicyManagementAzureResponse) {
				policyManagementMastersList.add((PolicyManagementAzureResponse) obj);
			}
		}

		Map<Integer, String> typeMap = teList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));

		for (PolicyManagementAzureResponse policyManagement : policyManagementMastersList) {
			policyManagement.setTenantName(typeMap.getOrDefault(policyManagement.getTenantId(), ""));
			if((policyManagement.getComplianceStatus() != null) && policyManagement.getComplianceStatus().equalsIgnoreCase("healthy")){
				policyManagement.setComplianceStatus("Passed");
			}else if((policyManagement.getComplianceStatus() != null) && policyManagement.getComplianceStatus().equalsIgnoreCase("Unhealthy")) {
				policyManagement.setComplianceStatus("Failed");
			}else if(policyManagement.getComplianceStatus() == null || policyManagement.getComplianceStatus().isBlank()) {
				policyManagement.setComplianceStatus("Not Available");
			}
		}

		Map<Order, String> orderMap = request.getDatatableInfo().getOrder().get(0);
		String order = orderMap.get(Order.name);
		// find list according to condition passed in remediation status
	
		List<Object> policyManagementFinalList = new ArrayList<>(policyManagementMastersList);
		Long totalCountValue = (Long) resultMap.get("totalCount");
		Integer totalCount = totalCountValue.intValue(); // Convert Long to Integer

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal(totalCount);
		results.setRecordsFiltered(totalCount);
		results.setDraw(request.getDatatableInfo().getDraw());
		results.setLength(request.getDatatableInfo().getLength());
		results.setData(policyManagementFinalList);
		return results;
	}

	@Override
	public Map<String, Object> getCountByCspmFindingAzure(Map<String, Object> collectedMap, boolean conditionCheck,
			PolicyManagementDetailsRequest request, Pageable pageable, String sourceName,String defaultSearchDays,String vendorName, String productName,String recordState) {

		if (collectedMap == null) {
			collectedMap = new HashMap<>();
		}
		String complianceStandards = (String) collectedMap.get("standards");
		String finding = (String) collectedMap.get("finding");
		String cloudAccountId = (String) collectedMap.get("tenantId");
		String securityControlId = (String) collectedMap.get("securityControlId");
		String cloudResourceId = (String) collectedMap.get("cloudResourceId");
		List<String> severity = (List<String>) collectedMap.get("severity");
		List<String> complianceStatus = (List<String>) collectedMap.get("complianceStatus");
		if (CollectionUtils.isNotEmpty(complianceStatus)) {
			for (int i = 0; i < complianceStatus.size(); i++) {
				if (complianceStatus.get(i).equalsIgnoreCase("Failed")) {
					complianceStatus.set(i, "Unhealthy");
				} else if (complianceStatus.get(i).equalsIgnoreCase("Passed")) {
					complianceStatus.set(i, "Healthy");
				} 
			}
		}
		
		
		Object reportedBetween = collectedMap.get("reportedBetween");
		if (reportedBetween == null) {
			reportedBetween = new HashMap<>();
		}
		Map<String, Object> reportedDate = ((Map<String, String>) reportedBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Object createdBetween = collectedMap.get("createdBetween");
		if (createdBetween == null) {
			createdBetween = new HashMap<>();
		}
		Map<String, Object> createdDate = ((Map<String, String>) createdBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Object updatedBetween = collectedMap.get("updatedBetween");
		if (updatedBetween == null) {
			updatedBetween = new HashMap<>();
		}
		Map<String, Object> updatedDate = ((Map<String, String>) updatedBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<Integer> tenantIds = request.getOrgIds();
		
		List<String> securityControlIds = request.getSearchSecurityControlIds();
		String reportedBetweenFrom = ObjectUtils.isNotEmpty(reportedDate.get("from"))
				? String.valueOf(reportedDate.get("from"))
				: null;
		String reportedBetweenTo = ObjectUtils.isNotEmpty(reportedDate.get("to"))
				? String.valueOf(reportedDate.get("to"))
				: null;
		String createdBetweenFrom = ObjectUtils.isNotEmpty(createdDate.get("from"))
				? String.valueOf(createdDate.get("from"))
				: null;
		String createdBetweenTo = ObjectUtils.isNotEmpty(createdDate.get("to")) ? String.valueOf(createdDate.get("to"))
				: null;
		String updatedBetweenFrom = ObjectUtils.isNotEmpty(updatedDate.get("from"))
				? String.valueOf(updatedDate.get("from"))
				: null;
		String updatedBetweenTo = ObjectUtils.isNotEmpty(updatedDate.get("to")) ? String.valueOf(updatedDate.get("to"))
				: null;

		String sortColumn = "", sortOrderBy = "";
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();
		
		if(createdBetweenFrom == null && createdBetweenTo == null){
			DateFormat dateFormatter = new SimpleDateFormat(AppConstants.DATETIME_FORMAT_STR);
			Date currentDate = new Date();
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(currentDate);

	        // Subtract 90 days from the current date
	        calendar.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(defaultSearchDays)));

	        // Get the updated Date after subtracting 90 days
	        Date date90DaysBefore = calendar.getTime();
			String currentDateTime = dateFormatter.format(currentDate);
			createdBetweenFrom =dateFormatter.format(date90DaysBefore);
		        // Subtract 90 days from the current date
			createdBetweenTo  = currentDateTime;
		}
	    
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}
		int pageSize = pageable.getPageSize(); // This is your limit
		int currentPage = pageable.getPageNumber(); // This is your offset

		// Calculate the offset based on the current page and page size
		int offset = currentPage;

		String countQuery = "SELECT COUNT(DISTINCT concat(e.securityControlId,'_',e.cloudResourceId)) FROM CspmFinding e "
				+ "WHERE (e.createdDate BETWEEN :createdBetweenFrom AND :createdBetweenTo) AND e.tenantId IN (:tenantIds) AND e.vendorName =:vendorName AND e.productName =:productName AND e.sourceName =:sourceName ";

		// Add conditions for finding, complianceStandards, etc., as needed

		String query = "SELECT new aisaac.dto.PolicyManagementAzureDTO(e, count(e.recId)) "
				+ "FROM CspmFinding e "
				+ "WHERE (e.createdDate BETWEEN :createdBetweenFrom AND :createdBetweenTo) AND e.tenantId IN (:tenantIds) AND e.vendorName =:vendorName AND e.productName =:productName AND e.sourceName = :sourceName ";

		if (conditionCheck) {

			if (updatedBetweenFrom != null && updatedBetweenTo != null) {
				query += "AND (e.updatedAt BETWEEN :updatedBetweenFrom AND :updatedBetweenTo) ";
				countQuery += "AND (e.updatedAt BETWEEN :updatedBetweenFrom AND :updatedBetweenTo) ";

			}
			if (reportedBetweenFrom != null && reportedBetweenTo != null) {
				query += "AND (e.createdAt BETWEEN :reportedBetweenFrom AND :reportedBetweenTo) ";
				countQuery += "AND (e.createdAt BETWEEN :reportedBetweenFrom AND :reportedBetweenTo) ";

			}

			if (finding != null && !finding.isEmpty()) {
				query += "AND (e.finding LIKE :finding) ";
				countQuery += "AND (e.finding LIKE :finding) ";

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				query += "AND (e.complianceStandards LIKE :complianceStandards) ";
				countQuery += "AND (e.complianceStandards LIKE :complianceStandards) ";

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				query += "AND (e.securityControlId LIKE :securityControlId) ";
				countQuery += "AND (e.securityControlId LIKE :securityControlId) ";

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				query += "AND (e.cloudAccountId LIKE :cloudAccountId) ";
				countQuery += "AND (e.cloudAccountId LIKE :cloudAccountId) ";

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				query += "AND (e.cloudResourceId LIKE :cloudResourceId) ";
				countQuery += "AND (e.cloudResourceId LIKE :cloudResourceId) ";

			}

			if (CollectionUtils.isNotEmpty(severity)) {
				query += "AND (e.severity IN (:severity)) ";
				countQuery += "AND (e.severity IN (:severity)) ";

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				if(complianceStatus.contains("Not Available")) {
					query += "AND (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "AND (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
				}else {
					query += "AND (e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "AND (e.complianceStatus IN (:complianceStatus)) ";	
				}
							
			}

		} else {
			
			query += "AND (";
			countQuery += "AND (";


			if (finding != null && !finding.isEmpty()) {
				query += "(e.finding LIKE :finding) ";
				countQuery += "(e.finding LIKE :finding) ";

			}					

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				query += "OR (e.complianceStandards LIKE :complianceStandards) ";
				countQuery += "OR (e.complianceStandards LIKE :complianceStandards) ";

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				query += "OR (e.securityControlId LIKE :securityControlId) ";
				countQuery += "OR (e.securityControlId LIKE :securityControlId) ";

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				query += "OR (e.cloudAccountId LIKE :cloudAccountId) ";
				countQuery += "OR (e.cloudAccountId LIKE :cloudAccountId) ";

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				query += "OR (e.cloudResourceId LIKE :cloudResourceId) ";
				countQuery += "OR (e.cloudResourceId LIKE :cloudResourceId) ";

			}
			if (CollectionUtils.isNotEmpty(severity)) {
				query += "OR (e.severity IN (:severity)) ";
				countQuery += "OR (e.severity IN (:severity)) ";

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				if(complianceStatus.contains("Not Available")) {
					query += "OR (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "OR (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
				}else {
					query += "OR (e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "OR (e.complianceStatus IN (:complianceStatus)) ";	
				}

			}

			query += ")";
			countQuery += ")";
		}
		query += " GROUP BY e.securityControlId, e.cloudResourceId ";
		//countQuery += " GROUP BY e.securityControlId, e.cloudResourceId ";

		if (sortColumn != null && !sortColumn.isEmpty()) {

			query += "ORDER BY e." + sortColumn + " ";

			if ("desc".equalsIgnoreCase(sortOrderBy)) {
				query += "DESC ";
			} else {
				query += "ASC ";

			}
		}

		TypedQuery<PolicyManagementAzureDTO> typedQuery = entityManager.createQuery(query, PolicyManagementAzureDTO.class);
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery, Long.class);

		if(conditionCheck) {
			
			if (finding != null && !finding.isEmpty()) {
				typedQuery.setParameter("finding", "%" +finding+ "%");
				countTypedQuery.setParameter("finding", "%" +finding+ "%");

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				typedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");
				countTypedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				typedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");
				countTypedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				typedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");
				countTypedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				typedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");
				countTypedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");

			}
			
			if (CollectionUtils.isNotEmpty(severity)) {
				typedQuery.setParameter("severity", severity);
				countTypedQuery.setParameter("severity", severity);

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				typedQuery.setParameter("complianceStatus", complianceStatus);
				countTypedQuery.setParameter("complianceStatus", complianceStatus);

			}

			if (reportedBetweenFrom != null && reportedBetweenTo != null) {
				reportedBetweenFrom = reportedBetweenFrom.replace(" ", "T");
				reportedBetweenTo = reportedBetweenTo.replace(" ", "T");
				typedQuery.setParameter("reportedBetweenFrom", LocalDateTime.parse(reportedBetweenFrom));
				typedQuery.setParameter("reportedBetweenTo", LocalDateTime.parse(reportedBetweenTo));
				countTypedQuery.setParameter("reportedBetweenFrom", LocalDateTime.parse(reportedBetweenFrom));
				countTypedQuery.setParameter("reportedBetweenTo", LocalDateTime.parse(reportedBetweenTo));
			}


			if (updatedBetweenFrom != null && updatedBetweenTo != null) {
				updatedBetweenFrom = updatedBetweenFrom.replace(" ", "T");
				updatedBetweenTo = updatedBetweenTo.replace(" ", "T");
				typedQuery.setParameter("updatedBetweenFrom", LocalDateTime.parse(updatedBetweenFrom));
				typedQuery.setParameter("updatedBetweenTo", LocalDateTime.parse(updatedBetweenTo));
				countTypedQuery.setParameter("updatedBetweenFrom", LocalDateTime.parse(updatedBetweenFrom));
				countTypedQuery.setParameter("updatedBetweenTo", LocalDateTime.parse(updatedBetweenTo));
			}
		}else {
			if (finding != null && !finding.isEmpty()) {
				typedQuery.setParameter("finding", "%" +finding+ "%");
				countTypedQuery.setParameter("finding", "%" +finding+ "%");

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				typedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");
				countTypedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				typedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");
				countTypedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				typedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");
				countTypedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				typedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");
				countTypedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");

			}

		}
		
		if (createdBetweenFrom != null && createdBetweenTo != null) {
			createdBetweenFrom = createdBetweenFrom.replace(" ", "T");
			createdBetweenTo = createdBetweenTo.replace(" ", "T");
			typedQuery.setParameter("createdBetweenFrom", LocalDateTime.parse(createdBetweenFrom));
			typedQuery.setParameter("createdBetweenTo", LocalDateTime.parse(createdBetweenTo));
			countTypedQuery.setParameter("createdBetweenFrom", LocalDateTime.parse(createdBetweenFrom));
			countTypedQuery.setParameter("createdBetweenTo", LocalDateTime.parse(createdBetweenTo));
		}
		
		if (tenantIds != null && !tenantIds.isEmpty()) {
			typedQuery.setParameter("tenantIds", tenantIds);
			countTypedQuery.setParameter("tenantIds", tenantIds);

		}
				
		typedQuery.setParameter("vendorName", vendorName);
		countTypedQuery.setParameter("vendorName", vendorName);
		
		typedQuery.setParameter("productName", productName);
		countTypedQuery.setParameter("productName", productName);
		
		typedQuery.setParameter("sourceName", sourceName);
		countTypedQuery.setParameter("sourceName", sourceName);
		
		typedQuery.setFirstResult(offset); // Set the offset
		typedQuery.setMaxResults(pageSize); // Set the limit

		List<PolicyManagementAzureDTO> resultList = typedQuery.getResultList();

		List<Long> totalCountList = countTypedQuery.getResultList();
		Long totalCount = totalCountList.get(0);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("resultList", resultList);
		resultMap.put("totalCount", totalCount);

		return resultMap;
	}
	
	@Override
	public Object getcspmFindingPrismaCloudList(PolicyManagementDetailsRequest request, Integer userId, boolean isExport) {

		boolean isSearchBy = false;
		String searchTxt = "", sortColumn = "", sortOrderBy = "";
		Long totalCount = null;

		Map<Search, String> searchMap = request.getDatatableInfo().getSearch();
		Map<String, Object> collectedMap = null;
		Map<String, Long> countMapList = new HashMap<>();

		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			isSearchBy = Boolean.parseBoolean(searchMap.get(Search.regex));
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();
		int pageNumber = 0;
		int length = 0;
		if (!isExport) {
			pageNumber = request.getDatatableInfo().getStart();
			length = request.getDatatableInfo().getLength();
		} else {
			pageNumber = request.getPart(); /// Calculate page number
			length = request.getLength();
		}

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);


		Map<String, Object> resultMap = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}

		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			List<Integer> tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}
			// handled for single tenant 
			if (CollectionUtils.isEmpty(request.getOrgIds())) {
				request.setOrgIds(tenantIds);
			}
			
		}
 		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		}

		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME);
		
		Optional<GlobalSettings> optionalVendorName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_PRISMA_CLOUD_VENDOR_PARAM_NAME);
		
		Optional<GlobalSettings> optionalProductName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_PRISMA_CLOUD_PRODUCT_PARAM_NAME);

		String paramValue = optional.get().getParamValue();
		String prismaCloudVendorParamValue = optionalVendorName.get().getParamValue();
		String prismaCloudProductNameParamValue = optionalProductName.get().getParamValue();
		
		Optional<GlobalSettings> defaultSearch = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT);

		String defaultSearchDays = defaultSearch.get().getParamValue();

		switch (request.getSearchType()) {
		case 1: {
			resultMap = this.getCountByCspmFindingPlasmaCloud(collectedMap, false, request, pageable, paramValue,defaultSearchDays,
					prismaCloudVendorParamValue,prismaCloudProductNameParamValue, AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		case 3: {
			resultMap = this.getCountByCspmFindingPlasmaCloud(collectedMap, true, request, pageable, paramValue,defaultSearchDays,
					prismaCloudVendorParamValue,prismaCloudProductNameParamValue, AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		default:
		}

		List<Tenant> tenList = tenantRepository.findAll();

		return getPolicyManagementDetailsWithRequiredPlasmaCloudList(tenList, request, resultMap);

	}

	public Object getPolicyManagementDetailsWithRequiredPlasmaCloudList(List<Tenant> teList,
			PolicyManagementDetailsRequest request, Map<String, Object> resultMap) {

		List<PolicyManagementPrismaCloudDTO> resultList = (List<PolicyManagementPrismaCloudDTO>) resultMap.get("resultList");

		List<Object> policyManagementMonitorList = new PolicyManagementPrismaCloudMapperList(resultList).map();

		List<PolicyManagementPrismaCloudResponse> policyManagementMastersList = new ArrayList<>();

		for (Object obj : policyManagementMonitorList) {
			if (obj instanceof PolicyManagementPrismaCloudResponse) {
				policyManagementMastersList.add((PolicyManagementPrismaCloudResponse) obj);
			}
		}

		Map<Integer, String> typeMap = teList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));

		for (PolicyManagementPrismaCloudResponse policyManagement : policyManagementMastersList) {
			policyManagement.setTenantName(typeMap.getOrDefault(policyManagement.getTenantId(), ""));
			if((policyManagement.getComplianceStatus() != null) && policyManagement.getComplianceStatus().equalsIgnoreCase("healthy")){
				policyManagement.setComplianceStatus("Passed");
			}else if((policyManagement.getComplianceStatus() != null) && policyManagement.getComplianceStatus().equalsIgnoreCase("Unhealthy")) {
				policyManagement.setComplianceStatus("Failed");
			}else if(policyManagement.getComplianceStatus() == null || policyManagement.getComplianceStatus().isBlank()) {
				policyManagement.setComplianceStatus("Not Available");
			}
		}

		Map<Order, String> orderMap = request.getDatatableInfo().getOrder().get(0);
		String order = orderMap.get(Order.name);
		// find list according to condition passed in remediation status
	
		List<Object> policyManagementFinalList = new ArrayList<>(policyManagementMastersList);
		Long totalCountValue = (Long) resultMap.get("totalCount");
		Integer totalCount = totalCountValue.intValue(); // Convert Long to Integer

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal(totalCount);
		results.setRecordsFiltered(totalCount);
		results.setDraw(request.getDatatableInfo().getDraw());
		results.setLength(request.getDatatableInfo().getLength());
		results.setData(policyManagementFinalList);
		return results;
	}

	@Override
	public Map<String, Object> getCountByCspmFindingPlasmaCloud(Map<String, Object> collectedMap, boolean conditionCheck,
			PolicyManagementDetailsRequest request, Pageable pageable, String sourceName,String defaultSearchDays,String vendorName, String productName,String recordState) {

		if (collectedMap == null) {
			collectedMap = new HashMap<>();
		}
		String complianceStandards = (String) collectedMap.get("standards");
		String finding = (String) collectedMap.get("finding");
		String cloudAccountId = (String) collectedMap.get("tenantId");
		String securityControlId = (String) collectedMap.get("securityControlId");
		String cloudResourceId = (String) collectedMap.get("cloudResourceId");
		String cloudServiceName = (String) collectedMap.get("cloudServiceName");
		List<String> severity = (List<String>) collectedMap.get("severity");
		List<String> complianceStatus = (List<String>) collectedMap.get("complianceStatus");
		
		if (CollectionUtils.isNotEmpty(complianceStatus)) {
			for (int i = 0; i < complianceStatus.size(); i++) {
				if (complianceStatus.get(i).equalsIgnoreCase("Failed")) {
					complianceStatus.set(i, "Unhealthy");
				} else if (complianceStatus.get(i).equalsIgnoreCase("Passed")) {
					complianceStatus.set(i, "Healthy");
				}
			}
		}
		
		Object reportedBetween = collectedMap.get("reportedBetween");
		if (reportedBetween == null) {
			reportedBetween = new HashMap<>();
		}
		Map<String, Object> reportedDate = ((Map<String, String>) reportedBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Object createdBetween = collectedMap.get("createdBetween");
		if (createdBetween == null) {
			createdBetween = new HashMap<>();
		}
		Map<String, Object> createdDate = ((Map<String, String>) createdBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		Object updatedBetween = collectedMap.get("updatedBetween");
		if (updatedBetween == null) {
			updatedBetween = new HashMap<>();
		}
		Map<String, Object> updatedDate = ((Map<String, String>) updatedBetween).entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<Integer> tenantIds = request.getOrgIds();
		
		List<String> securityControlIds = request.getSearchSecurityControlIds();
		String reportedBetweenFrom = ObjectUtils.isNotEmpty(reportedDate.get("from"))
				? String.valueOf(reportedDate.get("from"))
				: null;
		String reportedBetweenTo = ObjectUtils.isNotEmpty(reportedDate.get("to"))
				? String.valueOf(reportedDate.get("to"))
				: null;
		String createdBetweenFrom = ObjectUtils.isNotEmpty(createdDate.get("from"))
				? String.valueOf(createdDate.get("from"))
				: null;
		String createdBetweenTo = ObjectUtils.isNotEmpty(createdDate.get("to")) ? String.valueOf(createdDate.get("to"))
				: null;
		String updatedBetweenFrom = ObjectUtils.isNotEmpty(updatedDate.get("from"))
				? String.valueOf(updatedDate.get("from"))
				: null;
		String updatedBetweenTo = ObjectUtils.isNotEmpty(updatedDate.get("to")) ? String.valueOf(updatedDate.get("to"))
				: null;

		String sortColumn = "", sortOrderBy = "";
		List<Map<Order, String>> orderList = request.getDatatableInfo().getOrder();
		
		if(createdBetweenFrom == null && createdBetweenTo == null){
			DateFormat dateFormatter = new SimpleDateFormat(AppConstants.DATETIME_FORMAT_STR);
			Date currentDate = new Date();
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(currentDate);

	        // Subtract 90 days from the current date
	        calendar.add(Calendar.DAY_OF_MONTH, -(Integer.parseInt(defaultSearchDays)));

	        // Get the updated Date after subtracting 90 days
	        Date date90DaysBefore = calendar.getTime();
			String currentDateTime = dateFormatter.format(currentDate);
			createdBetweenFrom =dateFormatter.format(date90DaysBefore);
		        // Subtract 90 days from the current date
			createdBetweenTo  = currentDateTime;
		}
	    
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}
		int pageSize = pageable.getPageSize(); // This is your limit
		int currentPage = pageable.getPageNumber(); // This is your offset

		// Calculate the offset based on the current page and page size
		int offset = currentPage;

		String countQuery = "SELECT COUNT(DISTINCT concat(e.securityControlId,'_',e.cloudResourceId)) FROM CspmFinding e "
				+ "WHERE (e.createdDate BETWEEN :createdBetweenFrom AND :createdBetweenTo) AND e.tenantId IN (:tenantIds) AND e.recordState =:recordState AND e.vendorName =:vendorName AND e.productName =:productName AND e.sourceName =:sourceName ";

		// Add conditions for finding, complianceStandards, etc., as needed

		String query = "SELECT new aisaac.dto.PolicyManagementPrismaCloudDTO(e, count(e.recId)) "
				+ "FROM CspmFinding e "
				+ "WHERE (e.createdDate BETWEEN :createdBetweenFrom AND :createdBetweenTo) AND e.tenantId IN (:tenantIds) AND e.recordState =:recordState AND e.vendorName =:vendorName AND e.productName =:productName AND e.sourceName = :sourceName ";

		if (conditionCheck) {

			if (updatedBetweenFrom != null && updatedBetweenTo != null) {
				query += "AND (e.updatedAt BETWEEN :updatedBetweenFrom AND :updatedBetweenTo) ";
				countQuery += "AND (e.updatedAt BETWEEN :updatedBetweenFrom AND :updatedBetweenTo) ";

			}
			if (reportedBetweenFrom != null && reportedBetweenTo != null) {
				query += "AND (e.createdAt BETWEEN :reportedBetweenFrom AND :reportedBetweenTo) ";
				countQuery += "AND (e.createdAt BETWEEN :reportedBetweenFrom AND :reportedBetweenTo) ";

			}

			if (finding != null && !finding.isEmpty()) {
				query += "AND (e.finding LIKE :finding) ";
				countQuery += "AND (e.finding LIKE :finding) ";

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				query += "AND (e.complianceStandards LIKE :complianceStandards) ";
				countQuery += "AND (e.complianceStandards LIKE :complianceStandards) ";

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				query += "AND (e.securityControlId LIKE :securityControlId) ";
				countQuery += "AND (e.securityControlId LIKE :securityControlId) ";

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				query += "AND (e.cloudAccountId LIKE :cloudAccountId) ";
				countQuery += "AND (e.cloudAccountId LIKE :cloudAccountId) ";

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				query += "AND (e.cloudResourceId LIKE :cloudResourceId) ";
				countQuery += "AND (e.cloudResourceId LIKE :cloudResourceId) ";

			}

			if (CollectionUtils.isNotEmpty(severity)) {
				query += "AND (e.severity IN (:severity)) ";
				countQuery += "AND (e.severity IN (:severity)) ";

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				if(complianceStatus.contains("Not Available")) {
					query += "AND (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "AND (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
				}else {
					query += "AND (e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "AND (e.complianceStatus IN (:complianceStatus)) ";
				}
				
			}
			
			if (cloudServiceName != null && !cloudServiceName.isEmpty()) {
				query += "AND (e.cspmStr13 LIKE :cloudServiceName) ";
				countQuery += "AND (e.cspmStr13 LIKE :cloudServiceName) ";

			}

		} else {
			
			query += "AND (";
			countQuery += "AND (";


			if (finding != null && !finding.isEmpty()) {
				query += "(e.finding LIKE :finding) ";
				countQuery += "(e.finding LIKE :finding) ";

			}					

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				query += "OR (e.complianceStandards LIKE :complianceStandards) ";
				countQuery += "OR (e.complianceStandards LIKE :complianceStandards) ";

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				query += "OR (e.securityControlId LIKE :securityControlId) ";
				countQuery += "OR (e.securityControlId LIKE :securityControlId) ";

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				query += "OR (e.cloudAccountId LIKE :cloudAccountId) ";
				countQuery += "OR (e.cloudAccountId LIKE :cloudAccountId) ";

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				query += "OR (e.cloudResourceId LIKE :cloudResourceId) ";
				countQuery += "OR (e.cloudResourceId LIKE :cloudResourceId) ";

			}
			if (CollectionUtils.isNotEmpty(severity)) {
				query += "OR (e.severity IN (:severity)) ";
				countQuery += "OR (e.severity IN (:severity)) ";

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				if(complianceStatus.contains("Not Available")) {
					query += "OR (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "OR (e.complianceStatus IS NULL OR e.complianceStatus IN (:complianceStatus)) ";
				}else {
					query += "OR (e.complianceStatus IN (:complianceStatus)) ";
					countQuery += "OR (e.complianceStatus IN (:complianceStatus)) ";	
				}

			}
			
			if (cloudServiceName != null && !cloudServiceName.isEmpty()) {
				query += "OR (e.cspmStr13 LIKE :cloudServiceName) ";
				countQuery += "OR (e.cspmStr13 LIKE :cloudServiceName) ";

			}

			query += ")";
			countQuery += ")";
		}
		query += " GROUP BY e.securityControlId, e.cloudResourceId ";
		//countQuery += " GROUP BY e.securityControlId, e.cloudResourceId ";

		if (sortColumn != null && !sortColumn.isEmpty()) {

			query += "ORDER BY e." + sortColumn + " ";

			if ("desc".equalsIgnoreCase(sortOrderBy)) {
				query += "DESC ";
			} else {
				query += "ASC ";

			}
		}

		TypedQuery<PolicyManagementPrismaCloudDTO> typedQuery = entityManager.createQuery(query, PolicyManagementPrismaCloudDTO.class);
		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery, Long.class);

		if(conditionCheck) {
			
			if (finding != null && !finding.isEmpty()) {
				typedQuery.setParameter("finding", "%" +finding+ "%");
				countTypedQuery.setParameter("finding", "%" +finding+ "%");

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				typedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");
				countTypedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				typedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");
				countTypedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				typedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");
				countTypedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				typedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");
				countTypedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");

			}
			
			if (CollectionUtils.isNotEmpty(severity)) {
				typedQuery.setParameter("severity", severity);
				countTypedQuery.setParameter("severity", severity);

			}
			if (CollectionUtils.isNotEmpty(complianceStatus)) {
				typedQuery.setParameter("complianceStatus", complianceStatus);
				countTypedQuery.setParameter("complianceStatus", complianceStatus);

			}
			if (cloudServiceName != null && !cloudServiceName.isEmpty()) {
				typedQuery.setParameter("cloudServiceName", "%" +cloudServiceName+ "%");
				countTypedQuery.setParameter("cloudServiceName", "%" +cloudServiceName+ "%");

			}

			if (tenantIds != null && !tenantIds.isEmpty()) {
				typedQuery.setParameter("tenantIds", tenantIds);
				countTypedQuery.setParameter("tenantIds", tenantIds);

			}

			if (reportedBetweenFrom != null && reportedBetweenTo != null) {
				reportedBetweenFrom = reportedBetweenFrom.replace(" ", "T");
				reportedBetweenTo = reportedBetweenTo.replace(" ", "T");
				typedQuery.setParameter("reportedBetweenFrom", LocalDateTime.parse(reportedBetweenFrom));
				typedQuery.setParameter("reportedBetweenTo", LocalDateTime.parse(reportedBetweenTo));
				countTypedQuery.setParameter("reportedBetweenFrom", LocalDateTime.parse(reportedBetweenFrom));
				countTypedQuery.setParameter("reportedBetweenTo", LocalDateTime.parse(reportedBetweenTo));
			}


			if (updatedBetweenFrom != null && updatedBetweenTo != null) {
				updatedBetweenFrom = updatedBetweenFrom.replace(" ", "T");
				updatedBetweenTo = updatedBetweenTo.replace(" ", "T");
				typedQuery.setParameter("updatedBetweenFrom", LocalDateTime.parse(updatedBetweenFrom));
				typedQuery.setParameter("updatedBetweenTo", LocalDateTime.parse(updatedBetweenTo));
				countTypedQuery.setParameter("updatedBetweenFrom", LocalDateTime.parse(updatedBetweenFrom));
				countTypedQuery.setParameter("updatedBetweenTo", LocalDateTime.parse(updatedBetweenTo));
			}
		}else {
			if (finding != null && !finding.isEmpty()) {
				typedQuery.setParameter("finding", "%" +finding+ "%");
				countTypedQuery.setParameter("finding", "%" +finding+ "%");

			}

			if (complianceStandards != null && !complianceStandards.isEmpty()) {
				typedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");
				countTypedQuery.setParameter("complianceStandards", "%" +complianceStandards+ "%");

			}
			if (securityControlId != null && !securityControlId.isEmpty()) {
				typedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");
				countTypedQuery.setParameter("securityControlId", "%" +securityControlId+ "%");

			}
			if (cloudAccountId != null && !cloudAccountId.isEmpty()) {
				typedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");
				countTypedQuery.setParameter("cloudAccountId", "%" +cloudAccountId+ "%");

			}
			if (cloudResourceId != null && !cloudResourceId.isEmpty()) {
				typedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");
				countTypedQuery.setParameter("cloudResourceId", "%" +cloudResourceId+ "%");

			}
			if (cloudServiceName != null && !cloudServiceName.isEmpty()) {
				typedQuery.setParameter("cloudServiceName", "%" +cloudServiceName+ "%");
				countTypedQuery.setParameter("cloudServiceName", "%" +cloudServiceName+ "%");

			}

			if (tenantIds != null && !tenantIds.isEmpty()) {
				typedQuery.setParameter("tenantIds", tenantIds);
				countTypedQuery.setParameter("tenantIds",tenantIds);

			}

		}
		

		if (createdBetweenFrom != null && createdBetweenTo != null) {
			createdBetweenFrom = createdBetweenFrom.replace(" ", "T");
			createdBetweenTo = createdBetweenTo.replace(" ", "T");
			typedQuery.setParameter("createdBetweenFrom", LocalDateTime.parse(createdBetweenFrom));
			typedQuery.setParameter("createdBetweenTo", LocalDateTime.parse(createdBetweenTo));
			countTypedQuery.setParameter("createdBetweenFrom", LocalDateTime.parse(createdBetweenFrom));
			countTypedQuery.setParameter("createdBetweenTo", LocalDateTime.parse(createdBetweenTo));
		}
		
		typedQuery.setParameter("recordState", recordState);
		countTypedQuery.setParameter("recordState", recordState);
		
		typedQuery.setParameter("vendorName", vendorName);
		countTypedQuery.setParameter("vendorName", vendorName);
		
		typedQuery.setParameter("productName", productName);
		countTypedQuery.setParameter("productName", productName);
		
		typedQuery.setParameter("sourceName", sourceName);
		countTypedQuery.setParameter("sourceName", sourceName);
		
		typedQuery.setFirstResult(offset); // Set the offset
		typedQuery.setMaxResults(pageSize); // Set the limit

		List<PolicyManagementPrismaCloudDTO> resultList = typedQuery.getResultList();

		List<Long> totalCountList = countTypedQuery.getResultList();
		Long totalCount = totalCountList.get(0);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("resultList", resultList);
		resultMap.put("totalCount", totalCount);

		return resultMap;
	}
	
	@Override
	public Object getAwsConfigFindings(String resourceId,Integer tenantId) {
				
		Pageable pageable = getPageable();
		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_SOURCE_NAME);
		
		String paramValue = optional.get().getParamValue();
		int count = policyManagementRepository.countByCloudResourceIdAndRecordStateAndSourceNameAndTenantId(resourceId,AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,paramValue,tenantId);
		Page<CspmFinding> cspmFindings = policyManagementRepository.findFirstNCspmFindingsLimitedTo(resourceId,AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,paramValue,tenantId,pageable);			
		List<AwsConfigResponseDto> list = cspmFindings.stream().map(cspm->mapToAwsConfigDto(cspm)).collect(Collectors.toList());	
		
		DatatablePage<AwsConfigResponseDto> dataTable= new DatatablePage<>();
		dataTable.setData(list);
		dataTable.setRecordsTotal(count);
		dataTable.setRecordsFiltered(count);		
		return dataTable;
	}
	
	@Override
	public Object getAwsConfigFindings(String resourceId,Integer tenantId,DataTableRequest request) {
				
		Pageable pageable = getPageable(request);
			
		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_SOURCE_NAME);
		
		String paramValue = optional.get().getParamValue();
		
		int count = policyManagementRepository.countByCloudResourceIdAndRecordStateAndSourceNameAndTenantId(resourceId,AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,paramValue,tenantId);
		Page<CspmFinding> cspmFindings = policyManagementRepository.findFirstNCspmFindingsLimitedTo(resourceId,AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,paramValue,tenantId,pageable);			
		
		List<AwsConfigResponseDto> list = cspmFindings.stream().map(cspm->mapToAwsConfigDto(cspm)).collect(Collectors.toList());		
		
		DatatablePage<AwsConfigResponseDto> dataTable= new DatatablePage<>();
		dataTable.setData(list);
		dataTable.setRecordsTotal(count);
		dataTable.setRecordsFiltered(count);	
		dataTable.setLength(request.getLength());
		dataTable.setDraw(request.getDraw());
		return dataTable;
	}
	
 
	private Pageable getPageable(DataTableRequest request) {	
		int pageNumber = 0;
		int length = 0;
		pageNumber = request.getStart() / request.getLength(); /// Calculate page number
		length = request.getLength();
		// create Pageable instance
		String sortColumn=AppConstants.CSPM_FINDING_COLUMN_CREATED_DATE;
		Sort sort = Sort.by(sortColumn).descending();	
		Pageable pageable = PageRequest.of(pageNumber, length,sort);
		return pageable;
	}
	
	
	
	private Pageable getPageable() {
		int pageNumber = 0;
		int length = 10;
		String sortColumn=AppConstants.CSPM_FINDING_COLUMN_CREATED_DATE;
		Sort sort = Sort.by(sortColumn).descending();		
		Pageable pageable = PageRequest.of(pageNumber, length,sort);
		return pageable;
	}
	
	
	
	public AwsConfigResponseDto mapToAwsConfigDto(CspmFinding cspmFinding) {
		AwsConfigResponseDto awsConfigResponseDto = new AwsConfigResponseDto();
		awsConfigResponseDto.setFindingTitle(cspmFinding.getFinding());
		awsConfigResponseDto.setSeverity(cspmFinding.getSeverity());
		return awsConfigResponseDto;
	}
	
	@Override
	public Object exportPolicyManagementList(PolicyManagementDetailsRequest request, Integer limit, String path,Integer userId) {
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		Map<String, Object> collectedMap = null;

		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);
		
		int pageNumber = request.getStart(); // Calculate page number
		int length = request.getLength();
		Pageable pageable = PageRequest.of(pageNumber, length);
	

		Map<String, Object> resultMap = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}
		List<Integer> tenantIds = null;
		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			 tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}

			String remPlaybookName = (String) collectedMap.get("remPlaybookName");

			// for advanced search and full search
			if (StringUtils.isNotEmpty(remPlaybookName)) {
				List<String> securityControlIdList = remediationPlaybookNameRepository
						.findAllByColumnName(remPlaybookName);
				request.setSearchSecurityControlIds(securityControlIdList);
			}
		}
	
		List<String> remediationStatusList = null;
		List<String> securityControlIds = null;
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			remediationStatusList = (List<String>) collectedMap.get("remediationStatus");
		}
    
         Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
 				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
 				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME);
 		
 		Optional<GlobalSettings> optionalVendorName = globalSettingsRepo.findByParamTypeAndParamName(
 				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
 				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_VENDOR_PARAM_NAME);
 		
 		Optional<GlobalSettings> optionalProductName = globalSettingsRepo.findByParamTypeAndParamName(
 				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
 				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_PRODUCT_PARAM_NAME);

 		String paramValue = optional.get().getParamValue();
 		String awsVendorParamValue = optionalVendorName.get().getParamValue();
 		String awsProductNameParamValue = optionalProductName.get().getParamValue();

 		
 		Optional<GlobalSettings> defaultSearch = globalSettingsRepo.findByParamTypeAndParamName(
 				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
 				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT);

 		String defaultSearchDays = defaultSearch.get().getParamValue();
        if(tenantIds.size()==1) {
       	 request.setOrgIds(tenantIds);
        }
 		switch (request.getSearchType()) {
 		case 1: {
 			resultMap = this.getCountByCspmFinding(collectedMap, false, request, pageable, paramValue,defaultSearchDays,
 					AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,awsVendorParamValue,awsProductNameParamValue);
 			break;
 		}
 		case 3: {
 			resultMap = this.getCountByCspmFinding(collectedMap, true, request, pageable, paramValue,defaultSearchDays,
 					AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE,awsVendorParamValue,awsProductNameParamValue);
 			break;
 		}
 		default:
 		}

		List<Tenant> tenList = tenantRepository.findAll();
		List<PolicyManagementDTO> resultList = (List<PolicyManagementDTO>) resultMap.get("resultList");

		Map<Integer, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));

		for (PolicyManagementDTO policyManagement : resultList) {
			policyManagement.getCspmFinding().setTenantName(typeMap.getOrDefault(policyManagement.getCspmFinding().getTenantId(), ""));
		}
		
		List<RemediationPlaybookName> remediationPlaybookNameList = remediationPlaybookNameRepository.findAll();
		Map<String, String> PlaybookNameMap = remediationPlaybookNameList.stream()
				.collect(Collectors.toMap(te -> te.getSecurityControlId(), te -> te.getPlaybookName()));
		for (PolicyManagementDTO policyManagement : resultList) {
			policyManagement.getCspmFinding().setRemediationPlaybookName(
					PlaybookNameMap.getOrDefault(policyManagement.getCspmFinding().getSecurityControlId(), null));
		}
		
		for (PolicyManagementDTO policyManagement : resultList) {
			if (StringUtils.isBlank(policyManagement.getCspmFinding().getRemediationStatus())) {

				if (StringUtils.isBlank(policyManagement.getCspmFinding().getRemediationPlaybookName())) {
					policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
				} else {
					if (policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_PASSED)
							&& (policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							|| (policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_RESOLVED))) {
						policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
					}

					if (policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_FAILED)
							&& (policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							|| (policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_RESOLVED))
									&& StringUtils.isNotBlank(policyManagement.getCspmFinding().getRemediationPlaybookName()))
						policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NEW);
					else if (policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase(AppConstants.CSPM_COMPILANCE_STATUS_FAILED)
							&& (policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_WORK_FLOW_STATUS_NEW))
							&& StringUtils.isNotBlank(policyManagement.getCspmFinding().getRemediationPlaybookName()))
						policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);

					if ((policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_NOTIFIED))
							|| (policyManagement.getCspmFinding().getFindingStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_SUPPRESSED))) {
						policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_REMEDIATION_STATUS_NA);
					}
				}

			} else {
				if (policyManagement.getCspmFinding().getRemediationStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_PROCESSING))
					policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_PROCESSING);
				else if (policyManagement.getCspmFinding().getRemediationStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_FAILED))
					policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_FAILED);
				else if (policyManagement.getCspmFinding().getRemediationStatus().equalsIgnoreCase(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL))
					policyManagement.getCspmFinding().setRemediationStatus(AppConstants.CSPM_FINDING_STATUS_SUCCESSFUL);
			}
		}
		Long totalCountValue = (Long) resultMap.get("totalCount");
		Integer totalCount = totalCountValue.intValue();
		boolean moreThanOnePartsAvailable = Integer.compare(totalCount, limit) > 0;

		List<Object> listData = new PolicyManagementMapperList(resultList).map();
		String fileName  ="";
		if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
			fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
					AppConstants.POLICY_MANAGEMENT_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
					itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_XLSX);
		}else {
			 fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
					AppConstants.POLICY_MANAGEMENT_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
					itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_CSV);
		}				
		try {if(tenantIds.size() > 1) {
			boolean isMultiTenantUser = true;
			if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new PolicyManagementExcel(listData ,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(PolicyManagementMapper.export(resultList), path, fileName);
			}
		}else {
			boolean isMultiTenantUser = false;
			if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new PolicyManagementExcel(listData,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(PolicyManagementMapper.exportForSingleTenant(resultList), path, fileName);
			}
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			//first time only data is less than export limit
			if((totalCount - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart((totalCount - offset) <= limit);
		} 

		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable);
	}
	
	@Override
	public Object exportPolicyManagementAzureList(PolicyManagementDetailsRequest request, Integer limit, String path,Integer userId) {
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		Map<String, Object> collectedMap = null;

		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);
		
		int pageNumber = request.getStart(); // Calculate page number
		int length = request.getLength();
		Pageable pageable = PageRequest.of(pageNumber, length);
	

		Map<String, Object> resultMap = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}
		List<Integer> tenantIds = null;
		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			 tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}

		}
	
		List<String> securityControlIds = null;
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		}

		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME);
		
		Optional<GlobalSettings> optionalVendorName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AZURE_VENDOR_PARAM_NAME);
		
		Optional<GlobalSettings> optionalProductName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_AZURE_PRODUCT_PARAM_NAME);

		String paramValue = optional.get().getParamValue();
		String azureVendorParamValue = optionalVendorName.get().getParamValue();
		String azureProductNameParamValue = optionalProductName.get().getParamValue();
		
		Optional<GlobalSettings> defaultSearch = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT);

		String defaultSearchDays = defaultSearch.get().getParamValue();
         if(tenantIds.size()==1) {
        	 request.setOrgIds(tenantIds);
         }
         
		switch (request.getSearchType()) {
		case 1: {
			resultMap = this.getCountByCspmFindingAzure(collectedMap, false, request, pageable, paramValue,defaultSearchDays,
					azureVendorParamValue,azureProductNameParamValue,AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		case 3: {
			resultMap = this.getCountByCspmFindingAzure(collectedMap, true, request, pageable, paramValue,defaultSearchDays,
					azureVendorParamValue,azureProductNameParamValue,AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		default:
			// logsPage = policyManagementRepository.findAllByGroupingBy(pageable);
		}

		List<Tenant> tenList = tenantRepository.findAll();
		List<PolicyManagementAzureDTO> resultList = (List<PolicyManagementAzureDTO>) resultMap.get("resultList");

		Map<Integer, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));

		for (PolicyManagementAzureDTO policyManagement : resultList) {
			policyManagement.getCspmFinding().setTenantName(typeMap.getOrDefault(policyManagement.getCspmFinding().getTenantId(), ""));
			if((policyManagement.getCspmFinding().getComplianceStatus() != null) && policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase("healthy")){
				policyManagement.getCspmFinding().setComplianceStatus("Passed");
			}else if((policyManagement.getCspmFinding().getComplianceStatus() != null) && policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase("Unhealthy")) {
				policyManagement.getCspmFinding().setComplianceStatus("Failed");
			}else if(policyManagement.getCspmFinding().getComplianceStatus() == null || policyManagement.getCspmFinding().getComplianceStatus().isBlank()) {
				policyManagement.getCspmFinding().setComplianceStatus("Not Available");
			}
		}
		
		Long totalCountValue = (Long) resultMap.get("totalCount");
		Integer totalCount = totalCountValue.intValue();
		boolean moreThanOnePartsAvailable = Integer.compare(totalCount, limit) > 0;

		List<Object> listData = new PolicyManagementAzureMapperList(resultList).map();
		String fileName  ="";
		
		if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
			fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
					AppConstants.POLICY_MANAGEMENT_AZURE_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
					itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_XLSX);
		}else {
			 fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
					AppConstants.POLICY_MANAGEMENT_AZURE_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
					itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_CSV);
		}				
		try {if(tenantIds.size() > 1) {
			boolean isMultiTenantUser = true;
			if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new PolicyManagementAzureExcel(listData ,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(PolicyManagementAzureMapper.export(resultList), path, fileName);
			}
		}else {
			boolean isMultiTenantUser = false;
			if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new PolicyManagementAzureExcel(listData,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(PolicyManagementAzureMapper.exportForSingleTenant(resultList), path, fileName);
			}
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			//first time only data is less than export limit
			if((totalCount - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart((totalCount - offset) <= limit);
		} 
 
		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable);
	}
	@Override
	public Object exportPolicyManagementPrismaCloudList(PolicyManagementDetailsRequest request, Integer limit, String path,Integer userId) {
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		Map<String, Object> collectedMap = null;

		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);
		
		int pageNumber = request.getStart(); // Calculate page number
		int length = request.getLength();
		Pageable pageable = PageRequest.of(pageNumber, length);
	

		Map<String, Object> resultMap = null;
		PolicyManagementListingResponse response = new PolicyManagementListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}
		List<Integer> tenantIds = null;
		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			List<UserTenant> userTenants = this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);

			 tenantIds = userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList());
			List<Integer> cspmTenantIds = request.getOrgIds();
			if (CollectionUtils.isNotEmpty(cspmTenantIds)) {
				List<Integer> notIncspmTenantIds = tenantIds.stream().filter(e -> cspmTenantIds.contains(e))
						.collect(Collectors.toList());

				if (CollectionUtils.isEmpty(notIncspmTenantIds)) {
					response.setSuccess(false);
					response.setMessage("Unauthorised access to finding");
					return response;
				}
			}

		}
	
		List<String> securityControlIds = null;
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		}

		Optional<GlobalSettings> optional = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME);
		
		Optional<GlobalSettings> optionalVendorName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_PRISMA_CLOUD_VENDOR_PARAM_NAME);
		
		Optional<GlobalSettings> optionalProductName = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_PM_PRISMA_CLOUD_PRODUCT_PARAM_NAME);

		String paramValue = optional.get().getParamValue();
		String prismaCloudVendorParamValue = optionalVendorName.get().getParamValue();
		String prismaCloudProductNameParamValue = optionalProductName.get().getParamValue();
				
		Optional<GlobalSettings> defaultSearch = globalSettingsRepo.findByParamTypeAndParamName(
				AppConstants.GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT,
				AppConstants.GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT);

		String defaultSearchDays = defaultSearch.get().getParamValue();
         if(tenantIds.size()==1) {
        	 request.setOrgIds(tenantIds);
         }
         
		switch (request.getSearchType()) {
		case 1: {
			resultMap = this.getCountByCspmFindingPlasmaCloud(collectedMap, false, request, pageable, paramValue,defaultSearchDays,
					prismaCloudVendorParamValue,prismaCloudProductNameParamValue, AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		case 3: {
			resultMap = this.getCountByCspmFindingPlasmaCloud(collectedMap, true, request, pageable, paramValue,defaultSearchDays,
					prismaCloudVendorParamValue,prismaCloudProductNameParamValue, AppConstants.CSPM_FINDING_RECORD_STATE_ACTIVE);
			break;
		}
		default:
		}

		List<Tenant> tenList = tenantRepository.findAll();
		List<PolicyManagementPrismaCloudDTO> resultList = (List<PolicyManagementPrismaCloudDTO>) resultMap.get("resultList");

		Map<Integer, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));

		for (PolicyManagementPrismaCloudDTO policyManagement : resultList) {
			policyManagement.getCspmFinding().setTenantName(typeMap.getOrDefault(policyManagement.getCspmFinding().getTenantId(), ""));
			if((policyManagement.getCspmFinding().getComplianceStatus() != null) && policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase("healthy")){
				policyManagement.getCspmFinding().setComplianceStatus("Passed");
			}else if((policyManagement.getCspmFinding().getComplianceStatus() != null) && policyManagement.getCspmFinding().getComplianceStatus().equalsIgnoreCase("Unhealthy")) {
				policyManagement.getCspmFinding().setComplianceStatus("Failed");
			}else if(policyManagement.getCspmFinding().getComplianceStatus() == null || policyManagement.getCspmFinding().getComplianceStatus().isBlank()) {
				policyManagement.getCspmFinding().setComplianceStatus("Not Available");
			}
		}
		
		Long totalCountValue = (Long) resultMap.get("totalCount");
		Integer totalCount = totalCountValue.intValue();
		boolean moreThanOnePartsAvailable = Integer.compare(totalCount, limit) > 0;

		List<Object> listData = new PolicyManagementPrismaCloudMapperList(resultList).map();
		String fileName  ="";
		
		if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
			fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
					AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
					itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_XLSX);
		}else {
			 fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.POLICY_MANAGEMENT_FILENAME_SEPARATOR_START,
					AppConstants.POLICY_MANAGEMENT_PRISMA_CLOUD_FINDINGS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
					itIsLastPart, AppConstants.POLICY_MANAGEMENT_FILENAME_EXTENSION_CSV);
		}				
		try {if(tenantIds.size() > 1) {
			boolean isMultiTenantUser = true;
			if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new PolicyManagementPrismaCloudExcel(listData ,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(PolicyManagementPrismaCloudMapper.export(resultList), path, fileName);
			}
		}else {
			boolean isMultiTenantUser = false;
			if(request.getFileType().equalsIgnoreCase(AppConstants.POLICY_MANAGEMENT_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new PolicyManagementPrismaCloudExcel(listData,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(PolicyManagementPrismaCloudMapper.exportForSingleTenant(resultList), path, fileName);
			}
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			//first time only data is less than export limit
			if((totalCount - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart((totalCount - offset) <= limit);
		} 

		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable);
	}



}
