package aisaac.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import aisaac.domain.ExposuresDomain;
import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.domain.datatable.Order;
import aisaac.domain.datatable.Search;
import aisaac.entities.EasmIssue;
import aisaac.entities.Tenant;
import aisaac.payload.mapper.ExposuresMapper;
import aisaac.payload.request.ExposuresDetailsRequest;
import aisaac.payload.response.ExposuresListingResponse;
import aisaac.repository.ExposuresRepository;
import aisaac.repository.TenantRepository;
import aisaac.repository.UserTenantRepository;
import aisaac.util.AppConstants;
import aisaac.util.ExcelUtils;
import aisaac.util.ExposuresExcel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExposuresServiceImpl  implements ExposuresService {
	
	@Autowired
	private UserTenantRepository userTenantRepo;
	
	@Autowired
	private ExposuresRepository exposuresRepository;
	
	@Autowired
	private TenantRepository tenantRepository;
	
	@Override
	public Object getExposuresList(ExposuresDetailsRequest request, Integer userId, boolean isExport) {

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
																											/// page
																											/// number
			length = request.getDatatableInfo().getLength();
		} else {
			pageNumber = request.getStart() / request.getLength(); /// Calculate page number
			length = request.getLength();
		}

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);
		
		Page<EasmIssue> logsPage = null;
		ExposuresListingResponse response = new ExposuresListingResponse();

		if (userId == null || userId < 1) {
			response.setSuccess(false);
			response.setMessage("Invalid user Id");
			return response;
		}

		// Get EasmFinding details
		//totalCount = exposuresRepository.count();
		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// user tenant check
			/*
			 * List<UserTenant> userTenants =
			 * this.userTenantRepo.findByEnabledTenantsAndUserIdEquals(userId);
			 * 
			 * List<Integer> tenantIds =
			 * userTenants.stream().map(UserTenant::getTenantId).collect(Collectors.toList()
			 * ); List<Integer> easmTenantIds = request.getOrgIds(); if
			 * (CollectionUtils.isNotEmpty(easmTenantIds)) { List<Integer>
			 * notIneasmTenantIds = tenantIds.stream().filter(e ->
			 * easmTenantIds.contains(e)) .collect(Collectors.toList());
			 * 
			 * if (CollectionUtils.isEmpty(notIneasmTenantIds)) {
			 * response.setSuccess(false);
			 * response.setMessage("Unauthorised access to finding"); return response; } }
			 */

		}

		switch (request.getSearchType()) {
		
		case 1: {
			Specification<EasmIssue> fullSearchFilterSpecification = ExposuresDomain
					.getAdvanceOrFullSearchFilterSpecification(collectedMap, true, request, EasmIssue.class);
			logsPage = exposuresRepository.findAll(fullSearchFilterSpecification, pageable);
			break;
		}
		case 3: {
			Specification<EasmIssue> advanceSearchFilterSpecification = ExposuresDomain
					.getAdvanceOrFullSearchFilterSpecification(collectedMap, false, request, EasmIssue.class);
			logsPage = exposuresRepository.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		 
		default:
			logsPage = exposuresRepository.findAll(pageable);
		}
		
		List<Tenant> tenList = tenantRepository.findAll();
		
		Map<Integer, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<EasmIssue> expList = logsPage.getContent();
		for (EasmIssue exposures : expList) {
			exposures.setTenantName(typeMap.getOrDefault(exposures.getTenantId(), ""));
		}
		
		List<EasmIssue> list = logsPage.getContent();
		
		List<Object> exposureList = new ExposuresMapper(list).map();
		long recordsFilteredTotal = logsPage.getTotalElements();
		
		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) recordsFilteredTotal);
		results.setRecordsFiltered((int) recordsFilteredTotal);
		results.setDraw(request.getDatatableInfo().getDraw());
		results.setLength(request.getDatatableInfo().getLength());
		results.setData(exposureList);
		
		return results;

	}
	
	@Override
	public Object exportExposuresList(ExposuresDetailsRequest request, Integer limit, String path) {
		
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		Map<String, Object> collectedMap = null;
		
		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);
		
		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		
		Page<EasmIssue> logsPage = null;

		Object currentSearchFilters = request.getCurrentSearchFilters();
		if (currentSearchFilters != null) {
			collectedMap = ((Map<String, String>) currentSearchFilters).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		switch (request.getSearchType()) {
		
		case 1: {
			Specification<EasmIssue> fullSearchFilterSpecification = ExposuresDomain
					.getAdvanceOrFullSearchFilterSpecification(collectedMap, true, request, EasmIssue.class);
			logsPage = exposuresRepository.findAll(fullSearchFilterSpecification, pageable);
			break;
		}
		case 3: {
			Specification<EasmIssue> advanceSearchFilterSpecification = ExposuresDomain
					.getAdvanceOrFullSearchFilterSpecification(collectedMap, false, request, EasmIssue.class);
			logsPage = exposuresRepository.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		 
		default:
			logsPage = exposuresRepository.findAll(pageable);
		}
		
		List<Tenant> tenList = tenantRepository.findAll();
		
		Map<Integer, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<EasmIssue> expList = logsPage.getContent();
		for (EasmIssue exposures : expList) {
			exposures.setTenantName(typeMap.getOrDefault(exposures.getTenantId(), ""));
		}
		
		List<EasmIssue> list = logsPage.getContent();
		
		List<Object> exposureList = new ExposuresMapper(list).map();

		boolean moreThanOnePartsAvailable = Integer.compare((int)logsPage.getTotalElements(), limit) > 0;

		String fileName = "";
		
		if (request.getFileType().equalsIgnoreCase("xlsx")) {
			 fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.EXPOSURES_FILENAME_SEPARATOR_START,
					AppConstants.EXPOSURES_REPORT_FILENAME, moreThanOnePartsAvailable, 0,
					itIsLastPart, AppConstants.EXPOSURES_FILENAME_EXTENSION_XLSX);
		}else {
			 fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
					AppConstants.EXPOSURES_FILENAME_SEPARATOR_START,
					AppConstants.EXPOSURES_REPORT_FILENAME, moreThanOnePartsAvailable, 0,
					itIsLastPart, AppConstants.EXPOSURES_FILENAME_EXTENSION_CSV);
		}
		try {
		if (request.getIsMultiTenantUser()) {
			boolean isMultiTenantUser = true;
			if(request.getFileType().equalsIgnoreCase(AppConstants.EXPOSURES_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new ExposuresExcel(exposureList ,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(ExposuresMapper.export(list), path, fileName);
			}
		} else {
			boolean isMultiTenantUser = false;
			if(request.getFileType().equalsIgnoreCase(AppConstants.EXPOSURES_FILENAME_XLSX)) {
				path = ExcelUtils.downloadToPath(new ExposuresExcel(exposureList,isMultiTenantUser), path, fileName);
			}else {
				path = ExcelUtils.downloadCSVToPath(ExposuresMapper.exportForSingleTenant(list), path, fileName);
			}
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			// first time only data is less than export limit
			if (((int)logsPage.getTotalElements() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart(((int)logsPage.getTotalElements() - offset) <= limit);
		}

		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable);
	}

}
