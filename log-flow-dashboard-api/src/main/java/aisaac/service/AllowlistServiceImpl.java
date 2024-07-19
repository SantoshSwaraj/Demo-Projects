package aisaac.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import aisaac.domain.LogFlowDashboardDomain;
import aisaac.domain.datatable.DatatablePage;
import aisaac.domain.datatable.Order;
import aisaac.domain.datatable.Search;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AutoSuggestionRequiredFileds;
import aisaac.dto.CustomLFDRepository;
import aisaac.entities.AuditTrail;
import aisaac.entities.AuditTrailMaster;
import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.entities.Notes;
import aisaac.entities.ProductMaster;
import aisaac.entities.Tenant;
import aisaac.entities.UserCustomFieldMaster;
import aisaac.entities.WhitelistAuditTrail;
import aisaac.exception.ValidationException;
import aisaac.payload.mapper.AllowListAddProductMapper;
import aisaac.payload.mapper.AllowListEditProductMapper;
import aisaac.payload.mapper.AllowListToNotesMapper;
import aisaac.payload.mapper.LFDAuditTrailMapper;
import aisaac.payload.mapper.LogStopageAllowListDevicesMapper;
import aisaac.payload.request.AllowListRequestAddProduct;
import aisaac.payload.request.AllowListRequestEditProduct;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.payload.response.ApiResponse;
import aisaac.repository.AuditTrailMasterRepository;
import aisaac.repository.AuditTrailRepository;
import aisaac.repository.LogStopageWhiteListRepositary;
import aisaac.repository.NotesRepository;
import aisaac.repository.ProductMasterRepository;
import aisaac.repository.TenantRepository;
import aisaac.repository.UserCustomFieldMasterRepository;
import aisaac.repository.WhiteListAuditTrailRepository;
import aisaac.util.AppConstants;
import aisaac.util.LogFlowDashboardUtils;

@Service
public class AllowlistServiceImpl implements AllowlistService {

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private LogStopageWhiteListRepositary logStopageWhiteListRepositary;

	@Autowired
	private UserCustomFieldMasterRepository userCustomFieldMasterRepository;

	@Autowired
	private CustomLFDRepository customLFDRepository;
	

	@Autowired
	private ProductMasterRepository productMasterRepository;
	
	@Autowired
	private WhiteListAuditTrailRepository whitelistAuditTrailRepository;
	
	@Autowired
	private AuditTrailMasterRepository auditTrailMasterRepository;
	
	@Autowired
	private AuditTrailRepository auditTrailRepository;
	
	@Autowired
	private NotesRepository notesRepository;
	
	@Override
	public Object getAllowListDevicesList(LogFlowDashboardDetailsRequest request, List<ProductMaster> productMasters) {

		String searchTxt = "", sortColumn = "", sortOrderBy = "";

		Map<Search, String> searchMap = request.getSearch();
		List<Map<Order, String>> orderList = request.getOrder();

		if (searchMap != null && searchMap.size() > 0) {
			searchTxt = searchMap.get(Search.value);
		}
		if (orderList != null && orderList.size() > 0) {
			Map<Order, String> orderMap = orderList.get(0);
			sortColumn = orderMap.get(Order.name);
			sortOrderBy = orderMap.get(Order.dir);
		}

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();

		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);
		if (AppConstants.PRODUCT_MASTER_FIELDS.contains(sortColumn)
				|| AppConstants.TENANT_NAME.equalsIgnoreCase(sortColumn)) {
			pageable = PageRequest.of(pageNumber, length);
		}
		Page<LogStoppageWhitelistDevices> logsPage = null;

		Optional<List<Long>> mayBeProductIdsForVendorNameType = LogFlowDashboardUtils
				.getProductIdsByProductVendorOrNameOrType(productMasters, request.getSearchProductVendor(),
						request.getSearchProductName(), request.getSearchProductType(), request.getSearchType());
		List<Long> productIdsForVendorNameType = mayBeProductIdsForVendorNameType.orElse(null);

		if (Objects.nonNull(productIdsForVendorNameType)) {
			request.setProductId(productIdsForVendorNameType);
		}

		switch (request.getSearchType()) {
		case 1: {
			Specification<LogStoppageWhitelistDevices> fullSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt,
							searchTxt, searchTxt, true, request, LogStoppageWhitelistDevices.class);
			logsPage = logStopageWhiteListRepositary.findAll(fullSearchFilterSpecification, pageable);

			break;
		}
		case 3: {
			Specification<LogStoppageWhitelistDevices> advanceSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							request.getSearchProductVendor(), request.getSearchProductName(),
							request.getSearchProductType(), request.getSearchProductIP(),
							request.getSearchProductHostName(), request.getSearchCloudResourcID(),
							request.getSearchColletorIP(), request.getSearchCollectorHostName(),
							request.getSearchToEmail(), request.getSearchCcEmail(), false, request,
							LogStoppageWhitelistDevices.class);
			logsPage = logStopageWhiteListRepositary.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		default: {
			Specification<LogStoppageWhitelistDevices> advanceSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							request.getSearchProductVendor(), request.getSearchProductName(),
							request.getSearchProductType(), request.getSearchProductIP(),
							request.getSearchProductHostName(), request.getSearchCloudResourcID(),
							request.getSearchColletorIP(), request.getSearchCollectorHostName(),
							request.getSearchToEmail(), request.getSearchCcEmail(), false, request,
							LogStoppageWhitelistDevices.class);
			logsPage = logStopageWhiteListRepositary.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		}

		List<Tenant> tenList = tenantRepository.findAll();

		return getAllowListDetailsWithRequiredResponse(tenList, logsPage, request);

	}

	@Override
	public Object getAutoCompleteDataForAllowList(Map<String, String> request) {
		if (request.isEmpty()) {
			throw new ValidationException("Required Input Parameters are Missing.");
		}
		Field[] fields = LogStoppageWhitelistDevices.class.getDeclaredFields();
		List<String> fieldNames = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
		String paramName = new ArrayList<>(request.keySet()).get(0);

		if (!fieldNames.contains(paramName)) {
			throw new ValidationException("Param Name " + paramName + " Is Not a Valid Name");
		}
		return customLFDRepository.getDataByColumnNameAndValue(paramName, request.get(paramName),
				LogStoppageWhitelistDevices.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object allowlistUserActionDelete(List<Long> recIds, String note, Long userId) {

		if (recIds.isEmpty()) {
			throw new ValidationException("At least One record is required to perform a user  Action");
		}

		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException("UserId is Required to perform User Action");
		}

		if (StringUtils.isBlank(note)) {
			throw new ValidationException("Note Is Required to Perform Delete User Action");
		}
		List<LogStoppageWhitelistDevices> logStopageWhitelist = logStopageWhiteListRepositary.findAllById(recIds);

		logStopageWhitelist = logStopageWhitelist.stream().map(o -> o.setUpdatedById(userId)
				.setDeleted(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE).setUpdatedDate(LocalDateTime.now()))
				.collect(Collectors.toList());
		logStopageWhitelist = logStopageWhiteListRepositary.saveAll(logStopageWhitelist);
		
		if(StringUtils.isNotBlank(note)) {
			List<Notes> notes =AllowListToNotesMapper.deleteMap(logStopageWhitelist, note);
			notesRepository.saveAll(notes);
		}
		
//		List<WhitelistAuditTrail> whitelistAuditTrails= logStopageWhitelist.stream().map(o->LFDAuditTrailMapper.map(o)).collect(Collectors.toList());			
//		whitelistAuditTrailRepository.saveAll(whitelistAuditTrails);
		if (logStopageWhitelist.size() > 1)
			insertAuditTrailForAllowlist(AppConstants.LFD_ACTION_TYPE_BULK_DELETED_FROM_ALLOWLIST, logStopageWhitelist,
					note, Collections.EMPTY_MAP);
		else
			insertAuditTrailForAllowlist(AppConstants.LFD_ACTION_TYPE_DELETED_FROM_ALLOWLIST, logStopageWhitelist, note,
					Collections.EMPTY_MAP);
		return AppConstants.ALLOWLIST_USER_ACTION_SUCCESS;
	}

	public Object getAllowListDetailsWithRequiredResponse(List<Tenant> teList,
			Page<LogStoppageWhitelistDevices> logStopageWhiteListPage, LogFlowDashboardDetailsRequest request) {

		Map<Long, String> typeMap = teList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<LogStoppageWhitelistDevices> logStopageWhiteList = logStopageWhiteListPage.getContent();

		List<Long> allowlistRecIds = new ArrayList<>();
		for (LogStoppageWhitelistDevices logStopageWhiteListDevices : logStopageWhiteList) {
			logStopageWhiteListDevices
					.setTenantName(typeMap.getOrDefault(logStopageWhiteListDevices.getTenantId(), StringUtils.EMPTY));
			allowlistRecIds.add(logStopageWhiteListDevices.getRecId());
		}

		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();

		Map<Long, ProductMaster> productMasterMap = productMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> o));

		List<Object> logFlowAllowList = new LogStopageAllowListDevicesMapper(logStopageWhiteList, productMasterMap)
				.map();
		long recordsFilteredTotal = logStopageWhiteListPage.getTotalElements();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) recordsFilteredTotal);
		results.setRecordsFiltered((int) recordsFilteredTotal);
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(logFlowAllowList);

		
		return results;
	}

	@Override
	public Object userCustomizationDataInsertForLFD(Integer userId, String tabName, String fieldDetails) {
		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException("UserId is Required to perform User Customization");
		}
		if (StringUtils.isBlank(fieldDetails)) {
			throw new ValidationException("Field Details cannot be Empty");
		}

		if (!AppConstants.LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_TABS.contains(tabName)) {
			throw new ValidationException("URL Is not Valid");
		}
		
		tabName=LogFlowDashboardDomain.getUserCustomizationTabSectionName(tabName);
		
		UserCustomFieldMaster userCustomFieldMaster = userCustomFieldMasterRepository
				.findByCreatedByAndModuleNameAndSectionName(userId, AppConstants.LOG_FLOW_DASHBOARD_NAME, tabName)
				.orElse(new UserCustomFieldMaster());

		if (ObjectUtils.isNotEmpty(userCustomFieldMaster.getCreatedDate())) {
			userCustomFieldMaster.setUpdatedDate(LocalDateTime.now());
		}

		if (ObjectUtils.isEmpty(userCustomFieldMaster.getCreatedDate())) {
			userCustomFieldMaster.setCreatedBy(userId);
			userCustomFieldMaster.setCreatedDate(LocalDateTime.now());
		}

		userCustomFieldMaster.setModuleName(AppConstants.LOG_FLOW_DASHBOARD_NAME);
		userCustomFieldMaster.setFieldDetails(fieldDetails);
		userCustomFieldMaster.setSectionName(tabName);

		userCustomFieldMasterRepository.save(userCustomFieldMaster);

		return AppConstants.LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_SUCCESS;
	}

	@Override
	public Object userCustomizationDataResetForLFD(Integer userId, String tabName) {
		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException("UserId is Required to perform User Customization");
		}

		if (!AppConstants.LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_TABS.contains(tabName)) {
			throw new ValidationException("URL Is not Valid");
		}
		tabName=LogFlowDashboardDomain.getUserCustomizationTabSectionName(tabName);
		UserCustomFieldMaster userCustomFieldMaster = userCustomFieldMasterRepository
				.findByCreatedByAndModuleNameAndSectionName(userId, AppConstants.LOG_FLOW_DASHBOARD_NAME, tabName).orElseThrow(()->new ValidationException("User Customization Details Not Found"));
		
		userCustomFieldMasterRepository.delete(userCustomFieldMaster);;
		
		return AppConstants.LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_RESET;
	}

	@Override
	public Object getUserCustomizationDetailsForLFD(Integer userId, String tabName) {
		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException("UserId is Required to perform User Customization");
		}

		if (!AppConstants.LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_TABS.contains(tabName)) {
			throw new ValidationException("URL Is not Valid");
		}
		Optional<String> returnable = Optional.empty();
		Optional<String> returnableBydefault = Optional.empty();
		Map<String, Object> responseMap = new HashMap<>();
		List<UserCustomFieldMaster> list = userCustomFieldMasterRepository
				.findAll(LogFlowDashboardDomain.getCustomizationListWhereCondition(userId, tabName));

		Predicate<UserCustomFieldMaster> isUserCustom = e -> Objects.nonNull(e.getCreatedBy());

		returnable = Optional.ofNullable(
				list.stream().filter(isUserCustom).findAny().map(UserCustomFieldMaster::getFieldDetails).orElse(null));
		responseMap.put("userCustomizedColumn", returnable);

		if (returnable.isEmpty())
			returnableBydefault = Optional.ofNullable(list.stream().filter(isUserCustom.negate()).findAny()
					.map(UserCustomFieldMaster::getFieldDetails).orElse(null));
		responseMap.put("defaultCustomizedColumn", returnableBydefault);
		return responseMap;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object addProductToAllowList(List<AllowListRequestAddProduct> request) {
		ApiResponse response = new ApiResponse();
		
		List<LogStoppageWhitelistDevices> whitelistDevices = new AllowListAddProductMapper(request).map();

		List<Long> productIds = new ArrayList<>();
		List<Long> customerIds = new ArrayList<>();

		request.forEach(o -> {
			productIds.add(o.getProductId());
			customerIds.add(o.getTenantId());
		});

		List<LogStoppageWhitelistDevices> existingData = logStopageWhiteListRepositary
				.findAllByDeletedAndTenantIdInAndProductIdIn(AppConstants.DETECTED_DEVICE_WHITELIST_DEFAULT_VALUE,
						customerIds, productIds);
		
		whitelistDevices = whitelistDevices.stream()
				.filter(log1 -> existingData.stream().noneMatch(log2 -> log1.getProductId().equals(log2.getProductId())
						&& log1.getTenantId().equals(log2.getTenantId())))
				.collect(Collectors.toList());

		if (whitelistDevices.isEmpty()) {
			response.setStatus(422);
			response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_DUPLICATE);
			return response;
		}
		List<LogStoppageWhitelistDevices> savedDevices = logStopageWhiteListRepositary.saveAll(whitelistDevices);

		if (savedDevices.size() == whitelistDevices.size()) {

//			List<WhitelistAuditTrail> whitelistAuditTrails = savedDevices.stream().map(o -> LFDAuditTrailMapper.map(o))
//					.collect(Collectors.toList());
//			whitelistAuditTrailRepository.saveAll(whitelistAuditTrails);

			if (Objects.nonNull(request.get(0)) && Objects.nonNull(request.get(0).getRecId())) {
				insertAuditTrailForAllowlist(
						AppConstants.LFD_ACTION_TYPE_ADD_TO_ALLOWLIST_FROM_ADD_TO_LOG_FLOW_MONITORING, whitelistDevices,
						StringUtils.EMPTY, Collections.EMPTY_MAP);
			} else {
				insertAuditTrailForAllowlist(AppConstants.LFD_ACTION_TYPE_ADDED_TO_ALLOWLIST, whitelistDevices,
						StringUtils.EMPTY, Collections.EMPTY_MAP);
			}
			response.setStatus(200);
			response.setMessage(AppConstants.ALLOW_LIST_PRODUCT_SAVE);
			return response;
		}
		response.setStatus(400);
		response.setMessage(AppConstants.ALLOW_LIST_PRODUCT_FAILED);
		return response;
	}

	@Override
	public Object editAllowListProduct(List<AllowListRequestEditProduct> request) {
		ApiResponse response = new ApiResponse();
		List<Long> productIds = new ArrayList<>();
		List<Long> customerIds = new ArrayList<>();
		List<Long> recIds = new ArrayList<>();

		request.forEach(o -> {
			productIds.add(o.getProductId());
			customerIds.add(o.getTenantId());
			recIds.add(o.getRecId());
		});

		List<LogStoppageWhitelistDevices> existingData = logStopageWhiteListRepositary
				.findAllByDeletedAndTenantIdInAndProductIdIn(AppConstants.DETECTED_DEVICE_WHITELIST_DEFAULT_VALUE,
						customerIds, productIds);

		request = request.stream()
				.filter(log1 -> existingData.stream()
						.noneMatch(log2 -> log1.getProductId().equals(log2.getProductId())
								&& log1.getTenantId().equals(log2.getTenantId())
								&& !log1.getRecId().equals(log2.getRecId())))
				.collect(Collectors.toList());

		if (request.isEmpty()) {
			response.setStatus(422);
			response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_DUPLICATE);
			return response;
		}

		List<LogStoppageWhitelistDevices> logStoppageWhitelistDevices = logStopageWhiteListRepositary
				.findAllById(recIds);

		Map<Long, LogStoppageWhitelistDevices> oldLogStoppageWhitelistDevicesMap = logStoppageWhitelistDevices.stream()
				.collect(Collectors.toMap(o -> o.getRecId(), o -> {
					try {
						return o.clone();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						return o;
					}
				}));

		List<LogStoppageWhitelistDevices> logStoppageWhitelistDevicesFinal = new AllowListEditProductMapper(
				logStoppageWhitelistDevices, request).map();

		logStoppageWhitelistDevicesFinal = logStopageWhiteListRepositary.saveAll(logStoppageWhitelistDevicesFinal);

		if (logStoppageWhitelistDevicesFinal.size() == logStoppageWhitelistDevices.size()) {

//			List<WhitelistAuditTrail> whitelistAuditTrails = logStoppageWhitelistDevicesFinal.stream()
//					.map(o -> LFDAuditTrailMapper.map(o)).collect(Collectors.toList());
//			whitelistAuditTrailRepository.saveAll(whitelistAuditTrails);

			insertAuditTrailForAllowlist(AppConstants.LFD_ACTION_TYPE_EDITED_IN_ALLOWLIST,
					logStoppageWhitelistDevicesFinal, StringUtils.EMPTY, oldLogStoppageWhitelistDevicesMap);
			response.setStatus(200);
			response.setMessage(AppConstants.ALLOW_LIST_PRODUCT_EDIT);
			return response;
		}
		response.setStatus(400);
		response.setMessage(AppConstants.ALLOW_LIST_PRODUCT_EDIT_FAILED);
		return response;
	}

	@Override
	public Object deleteProductByTenantId(DeleteAllProductRequest allProductRequest) {
		List<LogStoppageWhitelistDevices> logStopageWhitelist = logStopageWhiteListRepositary
				.findAllByDeletedAndTenantId(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
						allProductRequest.getTenantId());

		if (logStopageWhitelist.isEmpty()) {
			return "Data is not Available for the Tenant Id " + allProductRequest.getTenantId();
		}

		logStopageWhitelist=logStopageWhitelist.stream().map(o->{
			o.setDeleted(true);
			o.setUpdatedById(allProductRequest.getUserId());
			o.setUpdatedDate(LocalDateTime.now());
			o.setDescription(allProductRequest.getAddNote());
			return o;
		}).collect(Collectors.toList());
		List<LogStoppageWhitelistDevices> logStopageWhitelistSaved= logStopageWhiteListRepositary.saveAll(logStopageWhitelist);
		if (logStopageWhitelist.size() == logStopageWhitelistSaved.size()&&StringUtils.isNotBlank(allProductRequest.getAddNote())) {
			return AppConstants.LOG_FLOW_DELETE_ALL_PRODUCTS_SUCCESS;
		}
		return AppConstants.LOG_FLOW_DELETE_ALL_PRODUCTS_FAILED;
	}

	@Override
	public Object getListOfAutoCompleteDataForLogFlowMonitor(List<ProductMaster> productMasters) {

		List<Tenant> tenants = tenantRepository.findAll();
		List<Long> tenantIdList = tenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());

		Map<Long, ProductMaster> productMasterMap = productMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> o));

		List<LogStoppageWhitelistDevices> logStopageAllowList = logStopageWhiteListRepositary
				.findAllByDeletedAndTenantIdIn(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT, tenantIdList);
		Map<Long, List<LogStoppageWhitelistDevices>> logStopageAllowListMap = logStopageAllowList.stream()
				.collect(Collectors.groupingBy(LogStoppageWhitelistDevices::getTenantId));

		Map<Long, AutoSuggestionRequiredFileds> response = new HashMap<>();

		logStopageAllowListMap.forEach((tenantId, allowList) -> {
			AutoSuggestionRequiredFileds autoSuggestionRequiredFileds = new AutoSuggestionRequiredFileds();
			Set<String> productNameList = new HashSet<>();
			Set<String> productVendorList = new HashSet<>();

			allowList.forEach(allowListMaster -> {
				ProductMaster productMaster = null;
				if (Objects.isNull(allowListMaster.getProductId())) {
					allowListMaster.setProductId(0L);
				}
				productMaster = productMasterMap.get(allowListMaster.getProductId().longValue());
				if (Objects.nonNull(productMaster)) {
					productNameList.add(productMaster.getProductName());
					productVendorList.add(productMaster.getProductVendor());
				}
			});
			autoSuggestionRequiredFileds.setCollectorIP(Collections.emptySet());
			autoSuggestionRequiredFileds.setProductHostName(Collections.emptySet());
			autoSuggestionRequiredFileds.setProductIP(Collections.emptySet());
			autoSuggestionRequiredFileds.setProductName(productNameList);
			autoSuggestionRequiredFileds.setProductType(Collections.emptySet());
			autoSuggestionRequiredFileds.setProductVendor(productVendorList);
			response.put(tenantId, autoSuggestionRequiredFileds);
		});
		return response;
	}

	private void insertAuditTrailForAllowlist(String action,
			List<LogStoppageWhitelistDevices> logStoppageWhitelistDevices, String note,
			Map<Long, LogStoppageWhitelistDevices> oldLogStoppageWhitelistDevicesMap) {

//		if(true) {
//			return;
//		}
		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();
		List<Tenant> tenants = tenantRepository.findAll();
		Map<Long, String> tenateNameAndIdMap = tenants.stream()
				.collect(Collectors.toMap(o -> o.getTenantId(), o -> o.getTenantName()));
		Map<Long, ProductMaster> productMasterMap = productMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> o));

		AuditTrailMaster auditTrailMaster = auditTrailMasterRepository.findByActionTypeAndModuleName(action,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD, AuditTrailMaster.class);

		if (Objects.isNull(auditTrailMaster)) {
			return;
		}

		List<AuditTrail> auditTrails = new ArrayList<>();
		auditTrails = logStoppageWhitelistDevices.stream().map(o -> new AuditTrail()
				.setCreatedBy(Objects.nonNull(o.getUpdatedById()) ? o.getUpdatedById() : o.getCreatedById())
				.setCreatedDate(LocalDateTime.now())
				.setModuleRecId(o.getRecId())
				.setAuditTrailMasterId(auditTrailMaster.getRecId())
				.setTenantId(o.getTenantId())
				.setDetails(getAuditTrailJsonResponseByAllowlist(action, o, tenateNameAndIdMap, productMasterMap,
						o.getRecId(), auditTrailMaster.getRecId(), note,
						oldLogStoppageWhitelistDevicesMap.get(o.getRecId()))))
				.collect(Collectors.toList());
		auditTrailRepository.saveAll(auditTrails);
	}
	private String getAuditTrailJsonResponseByAllowlist(String action,
			LogStoppageWhitelistDevices logStoppageWhitelistDevices, Map<Long, String> tenantMap,
			Map<Long, ProductMaster> productMasterMap, Long moduleRecId, Long auditTrailMasterId, String deleteNote,
			LogStoppageWhitelistDevices oldLogStoppageWhitelistDevices) {

		AuditTrailDetailsOnly auditrailDetailOnly = auditTrailRepository
				.findTop1ByModuleRecIdAndAuditTrailMasterIdOrderByRecIdDesc(moduleRecId, auditTrailMasterId,
						AuditTrailDetailsOnly.class);

		return LogFlowDashboardDomain.getAuditTrailJsonResponseByAllowlist(action, logStoppageWhitelistDevices, tenantMap,
				productMasterMap, auditrailDetailOnly, deleteNote, oldLogStoppageWhitelistDevices);
	}
}
