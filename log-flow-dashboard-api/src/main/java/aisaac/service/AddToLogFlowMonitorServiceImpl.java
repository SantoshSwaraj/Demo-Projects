package aisaac.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import aisaac.dto.AutoSuggestionRequiredFileds;
import aisaac.dto.CustomLFDRepository;
import aisaac.dto.NotesFormatDto;
import aisaac.entities.AuditTrail;
import aisaac.entities.AuditTrailMaster;
import aisaac.entities.LogStopageDetectedDevicesAuditTrail;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.Notes;
import aisaac.entities.ProductMaster;
import aisaac.entities.ProductTypeMaster;
import aisaac.entities.Tenant;
import aisaac.entities.User;
import aisaac.exception.ValidationException;
import aisaac.payload.mapper.AuditTrailListMapper;
import aisaac.payload.mapper.LFDAuditTrailMapper;
import aisaac.payload.mapper.LogDetectedToNotesMapper;
import aisaac.payload.mapper.LogStopageDetectedDevicesMapper;
import aisaac.payload.mapper.LogStoppageGetProductDetailsMapper;
import aisaac.payload.request.AddToLogFlowMonitorUserActionRequest;
import aisaac.payload.request.AuditTrailListRequest;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.payload.request.LogStopageDetectedDeviceCompositeKeyRequest;
import aisaac.repository.AuditTrailMasterRepository;
import aisaac.repository.AuditTrailRepository;
import aisaac.repository.LogStopageDetectedDevicesAuditTrailRepository;
import aisaac.repository.LogStopageDetectedDevicesRepository;
import aisaac.repository.NotesRepository;
import aisaac.repository.ProductMasterRepository;
import aisaac.repository.ProductTypeMasterRepository;
import aisaac.repository.TenantRepository;
import aisaac.repository.UserRepository;
import aisaac.util.AppConstants;
import aisaac.util.LogFlowDashboardUtils;

@Service
public class AddToLogFlowMonitorServiceImpl implements AddToLogFlowMonitorService {

	@Autowired
	private LogStopageDetectedDevicesRepository logStopageDetectedDevicesRepository;

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private CustomLFDRepository customLFDRepository;

	@Autowired
	private NotesRepository notesRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductMasterRepository productMasterRepository;
	
	@Autowired
	private ProductTypeMasterRepository productTypeMasterRepository;
	
	@Autowired
	private LogStopageDetectedDevicesAuditTrailRepository logStopageDetectedDevicesAuditTrailRepository;

	@Autowired
	private AuditTrailMasterRepository auditTrailMasterRepository;
	
	@Autowired
	private AuditTrailRepository auditTrailRepository;
	
	@Override
	public Object getDetectedDevicesList(LogFlowDashboardDetailsRequest request,List<ProductMaster> productMasters) {

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
		Page<LogStoppageDetectedDevices> logsPage = null;
		
		Optional<List<Long>> mayBeProductIdsForVendorNameType = LogFlowDashboardUtils
				.getProductIdsByProductVendorOrNameOrType(productMasters, request.getSearchProductVendor(),
						request.getSearchProductName(), request.getSearchProductType(), request.getSearchType());
		List<Long> productIdsForVendorNameType = mayBeProductIdsForVendorNameType.orElse(null);

		if (Objects.nonNull(productIdsForVendorNameType)) {
			request.setProductId(productIdsForVendorNameType);
		}
		
		switch (request.getSearchType()) {
		case 1: {
			Specification<LogStoppageDetectedDevices> fullSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt,
							searchTxt, searchTxt, true, request, LogStoppageDetectedDevices.class);
			logsPage = logStopageDetectedDevicesRepository.findAll(fullSearchFilterSpecification, pageable);

			break;
		}
		case 3: {
			Specification<LogStoppageDetectedDevices> advanceSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							request.getSearchProductVendor(), request.getSearchProductName(),
							request.getSearchProductType(), request.getSearchProductIP(),
							request.getSearchProductHostName(), request.getSearchCloudResourcID(),
							request.getSearchColletorIP(), request.getSearchCollectorHostName(),
							request.getSearchToEmail(), request.getSearchCcEmail(), false, request,
							LogStoppageDetectedDevices.class);
			logsPage = logStopageDetectedDevicesRepository.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		default: {
			Specification<LogStoppageDetectedDevices> advanceSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							request.getSearchProductVendor(), request.getSearchProductName(),
							request.getSearchProductType(), request.getSearchProductIP(),
							request.getSearchProductHostName(), request.getSearchCloudResourcID(),
							request.getSearchColletorIP(), request.getSearchCollectorHostName(),
							request.getSearchToEmail(), request.getSearchCcEmail(), false, request,
							LogStoppageDetectedDevices.class);
			logsPage = logStopageDetectedDevicesRepository.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		}

		List<Tenant> tenList = tenantRepository.findAll();

		return getDetectedDetailsWithRequiredResponse(tenList, logsPage, request);

	}

	@Override
	public Object getAutoCompleteDataForDetectedDevices(Map<String, String> request) {
		if (request.isEmpty()) {
			throw new ValidationException("Required Input Parameters are Missing.");
		}
		Field[] fields = LogStoppageDetectedDevices.class.getDeclaredFields();
		List<String> fieldNames = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
		String paramName = new ArrayList<>(request.keySet()).get(0);

		if (!fieldNames.contains(paramName)) {
			throw new ValidationException("Param Name " + paramName + " Is Not a Valid Name");
		}
		return customLFDRepository.getDataByColumnNameAndValue(paramName, request.get(paramName),
				LogStoppageDetectedDevices.class);
	}

	public Object getDetectedDetailsWithRequiredResponse(List<Tenant> teList,
			Page<LogStoppageDetectedDevices> logStopageDetectedPage, LogFlowDashboardDetailsRequest request) {

		Map<Long, String> typeMap = teList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<LogStoppageDetectedDevices> logStopageDetectedList = logStopageDetectedPage.getContent();
		List<Long> detectedRecId=new ArrayList<>();
		for (LogStoppageDetectedDevices logStopageDetectedDevices : logStopageDetectedList) {
			logStopageDetectedDevices
					.setTenantName(typeMap.getOrDefault(logStopageDetectedDevices.getTenantId(), ""));
			detectedRecId.add(logStopageDetectedDevices.getRecId());
		}
		List<Notes> notes = notesRepository.findAllByTypeRecIdInAndNoteTypeOrderByCreatedDateDesc(detectedRecId,
				AppConstants.DETECTED_DEVICES);
		
		List<User> users= userRepository.findAll();
		Map<Long, String> createdByAndIdMap = users.stream().collect(
				Collectors.toMap(o -> o.getUserId(), o -> LogFlowDashboardUtils.getDisplayUsername(o.getDisplayName(),
						o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));
	
		Map<Long, List<NotesFormatDto>> noteMap = notes.stream().collect(
				Collectors.groupingBy(Notes::getTypeRecId, Collectors.mapping(o->LogFlowDashboardDomain.getNotesFormatDtoByNote(o,createdByAndIdMap), Collectors.toList())));


		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();

		Map<Long, ProductMaster> productMasterMap = productMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> o));
		
		List<Object> logFlowDetectedList = new LogStopageDetectedDevicesMapper(logStopageDetectedList,noteMap,productMasterMap).map();
		long recordsFilteredTotal = logStopageDetectedPage.getTotalElements();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) recordsFilteredTotal);
		results.setRecordsFiltered((int) recordsFilteredTotal);
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(logFlowDetectedList);

		return results;
	}

	@Override
	public Object performAddToLogflowMonitorUserActions(AddToLogFlowMonitorUserActionRequest request,
			String actionType) {

		if (StringUtils.isBlank(actionType)
				|| !AppConstants.ADD_TO_LOG_FLOW_MONITOR_USER_ACTIONS.contains(actionType)) {
			throw new ValidationException("User Action is not Valid");
		}

		if (AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE.equals(actionType)
				&& StringUtils.isBlank(request.getAddNote())) {
			throw new ValidationException("Text Is Required to Perform Add Note User Action");
		}

		List<LogStoppageDetectedDevices> logStoppageDetectedDevices = logStopageDetectedDevicesRepository
				.findAllByDeletedAndRecIdIn(AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT, request.getRecIds());

		if (logStoppageDetectedDevices.isEmpty()) {
			throw new ValidationException("Data is not Available For this Request");
		}
		try {
			logStoppageDetectedDevices = LogFlowDashboardDomain.getDetectedDevicesEntityByUserAction(actionType,
					logStoppageDetectedDevices);

			List<LogStoppageDetectedDevices> logStoppageDetectedDeviceSaved = logStopageDetectedDevicesRepository
					.saveAll(logStoppageDetectedDevices);
			
//			List<LogStopageDetectedDevicesAuditTrail> logStopageDetectedDevicesAuditTrail= logStoppageDetectedDeviceSaved.stream().map(o->LFDAuditTrailMapper.map(o,request.getUserId().longValue())).collect(Collectors.toList());			
//			logStopageDetectedDevicesAuditTrailRepository.saveAll(logStopageDetectedDevicesAuditTrail);
			
			if (logStoppageDetectedDevices.size() == logStoppageDetectedDeviceSaved.size()) {

				String auditTrailAction = LogFlowDashboardDomain
						.getAuditTrailActionsForDetectedDevices(logStoppageDetectedDeviceSaved.size(), actionType);

				if (StringUtils.isNotBlank(request.getAddNote())) {
					List<Notes> notes = LogDetectedToNotesMapper.addNoteMap(logStoppageDetectedDeviceSaved,
							request.getAddNote(), request.getUserId());
					notesRepository.saveAll(notes);
				}
				
				insertAuditTrailForDetectedDevices(auditTrailAction, logStoppageDetectedDeviceSaved, request.getAddNote(),
						request.getUserId());
				return AppConstants.ADD_TO_LOG_FLOW_MONITOR_USER_ACTION_SUCCESS + actionType;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AppConstants.ADD_TO_LOG_FLOW_MONITOR_USER_ACTION_FAILED + actionType;
	}

	@Override
	public Object getAutoCompleteDataForDetectedDevicesWithOptionalOrganizationIDs(List<Integer> orgIds,
			String fieldName) {
		if (!AppConstants.AUTO_POPULATE_FIELDS_FOR_ADD_TO_LFM.contains(fieldName)) {
			throw new ValidationException("URL is not Valid");
		}
		return customLFDRepository.getDataByColumnNameWithOptionalOrganizationIds(fieldName, orgIds,
				LogStoppageDetectedDevices.class);
	}

	@Override
	public Object getDetectedDevicesListToAddLogFlowDetails(LogStopageDetectedDeviceCompositeKeyRequest request) {
		List<LogStoppageDetectedDevices> logStoppageDetectedDevices = logStopageDetectedDevicesRepository
				.findAllByDeletedAndProductIdInAndTenantIdInAndProductIPInAndProductHostNameIn(
						AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT, request.getProductIds(),
						request.getCustomerIds(), request.getProductIPs(), request.getProductHostNames());

		if (logStoppageDetectedDevices.isEmpty()) {
			throw new ValidationException("Data is not Available For this Request");
		}
		List<Tenant> tenList = tenantRepository.findAll();
		Map<Long, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		Map<Long, List<NotesFormatDto>> noteMap = new HashMap<>();

		return LogStoppageGetProductDetailsMapper.mapLogDetected(logStoppageDetectedDevices, typeMap, noteMap);
	}

	@Override
	public Object getDetectedDeviceListByRecIds(List<Long> recIds) {
		List<LogStoppageDetectedDevices> logStoppageDetectedDevices = logStopageDetectedDevicesRepository
				.findAllByDeletedAndRecIdIn(AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT, recIds);

		if (logStoppageDetectedDevices.isEmpty()) {
			throw new ValidationException("Data is not Available For this Request");
		}
		List<Tenant> tenList = tenantRepository.findAll();
		Map<Long, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<Notes> notes = notesRepository.findAllByTypeRecIdInAndNoteTypeOrderByCreatedDateDesc(recIds,
				AppConstants.DETECTED_DEVICES);
		List<User> users= userRepository.findAll();
		Map<Long, String> createdByAndIdMap = users.stream().collect(
				Collectors.toMap(o -> o.getUserId(), o -> LogFlowDashboardUtils.getDisplayUsername(o.getDisplayName(),
						o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));
	
		Map<Long, List<NotesFormatDto>> noteMap = notes.stream().collect(
				Collectors.groupingBy(Notes::getTypeRecId, Collectors.mapping(o->LogFlowDashboardDomain.getNotesFormatDtoByNote(o,createdByAndIdMap), Collectors.toList())));

		return LogStoppageGetProductDetailsMapper.mapLogDetected(logStoppageDetectedDevices, typeMap,noteMap);
	}

	@Override
	public Object deleteProductByTenantId(DeleteAllProductRequest allProductRequest) {
		List<LogStoppageDetectedDevices> logStoppageDetectedDevices = logStopageDetectedDevicesRepository
				.findAllByDeletedAndTenantId(AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT,
						allProductRequest.getTenantId());

		if (logStoppageDetectedDevices.isEmpty()) {
			return "Data is not Available for the Tenant Id " + allProductRequest.getTenantId();
		}

		logStoppageDetectedDevices=logStoppageDetectedDevices.stream().map(o->{
			o.setDeleted(true);
			o.setUpdatedDate(LocalDateTime.now());
			return o;
		}).collect(Collectors.toList());
		List<LogStoppageDetectedDevices> logStoppageDetectedDevicesSaved= logStopageDetectedDevicesRepository.saveAll(logStoppageDetectedDevices);

		List<LogStopageDetectedDevicesAuditTrail> logStopageDetectedDevicesAuditTrail= logStoppageDetectedDevicesSaved.stream().map(o->LFDAuditTrailMapper.map(o,allProductRequest.getUserId())).collect(Collectors.toList());			
		logStopageDetectedDevicesAuditTrailRepository.saveAll(logStopageDetectedDevicesAuditTrail);
		
		if (logStoppageDetectedDevices.size() == logStoppageDetectedDevicesSaved.size()&&StringUtils.isNotBlank(allProductRequest.getAddNote())) {
				List<Notes> notes = LogDetectedToNotesMapper.addNoteMap(logStoppageDetectedDevices,
						allProductRequest.getAddNote(),allProductRequest.getUserId().intValue());
				notesRepository.saveAll(notes);
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

		List<LogStoppageDetectedDevices> logStoppageDetectedDevicesList = logStopageDetectedDevicesRepository
				.findAllByDeletedAndTenantIdIn(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT, tenantIdList);
		Map<Long, List<LogStoppageDetectedDevices>> logStoppageDetectedDevicesListMap = logStoppageDetectedDevicesList
				.stream().collect(Collectors.groupingBy(LogStoppageDetectedDevices::getTenantId));

		Map<Long, AutoSuggestionRequiredFileds> response = new HashMap<>();

		logStoppageDetectedDevicesListMap.forEach((tenantId, logStopageDetectedList) -> {
			AutoSuggestionRequiredFileds autoSuggestionRequiredFileds = new AutoSuggestionRequiredFileds();
			Set<String> productIPList = new HashSet<>();
			Set<String> productHostNameList = new HashSet<>();
			Set<String> collectorIPList = new HashSet<>();
			Set<String> productNameList = new HashSet<>();
			Set<String> productVendorList = new HashSet<>();
			Set<String> productTypeList = new HashSet<>();

			logStopageDetectedList.forEach(logStoppageDetectedDevices -> {
				ProductMaster productMaster = null;
				if (ObjectUtils.isNotEmpty(logStoppageDetectedDevices.getProductIP()))
					productIPList.add(logStoppageDetectedDevices.getProductIP());

				if (ObjectUtils.isNotEmpty(logStoppageDetectedDevices.getProductHostName()))
					productHostNameList.add(logStoppageDetectedDevices.getProductHostName());

				if (ObjectUtils.isNotEmpty(logStoppageDetectedDevices.getCollectorAddress()))
					collectorIPList.add(logStoppageDetectedDevices.getCollectorAddress());
				if (Objects.isNull(logStoppageDetectedDevices.getProductId())) {
					logStoppageDetectedDevices.setProductId(0);
				}
				productMaster = productMasterMap.get(logStoppageDetectedDevices.getProductId().longValue());
				if (Objects.nonNull(productMaster)) {
					productNameList.add(productMaster.getProductName());
					productVendorList.add(productMaster.getProductVendor());
					productTypeList.add(productMaster.getProductType());
				}
			});
			autoSuggestionRequiredFileds.setCollectorIP(collectorIPList);
			autoSuggestionRequiredFileds.setProductHostName(productHostNameList);
			autoSuggestionRequiredFileds.setProductIP(productIPList);
			autoSuggestionRequiredFileds.setProductName(productNameList);
			autoSuggestionRequiredFileds.setProductType(productTypeList);
			autoSuggestionRequiredFileds.setProductVendor(productVendorList);
			response.put(tenantId, autoSuggestionRequiredFileds);
		});
		return response;
	}

	private void insertAuditTrailForDetectedDevices(String action,
			List<LogStoppageDetectedDevices> logStoppageDetectedDevices,String note, Integer userId ) {


//		if(true) {
//			return;
//		}
		
		List<ProductTypeMaster> productTypeMastersList = productTypeMasterRepository.findAllByDeletedFalse();
		Map<Long, String> productTypeMap = productTypeMastersList.stream()
				.collect(Collectors.toMap(o -> o.getProductTypeId(), o -> o.getProductType()));
		
		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();
		List<Tenant> tenants = tenantRepository.findAll();
		Map<Long, String> tenateNameAndIdMap = tenants.stream()
				.collect(Collectors.toMap(o -> o.getTenantId(), o -> o.getTenantName()));
		Map<Long, ProductMaster> productMasterMap = productMasters.stream().map(o -> {
			o.setProductType(productTypeMap.get(o.getProductTypeId()));
			return o;
		}).collect(Collectors.toMap(o -> o.getProductId(), o -> o));

		AuditTrailMaster auditTrailMaster = auditTrailMasterRepository.findByActionTypeAndModuleName(action,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD, AuditTrailMaster.class);

		if (Objects.isNull(auditTrailMaster)) {
			return;
		}


		List<AuditTrail> auditTrails = new ArrayList<>();
		auditTrails = logStoppageDetectedDevices.stream().map(o -> new AuditTrail()
				.setCreatedBy(Objects.nonNull(userId) ? userId.longValue() : null)
				.setCreatedDate(LocalDateTime.now())
				.setModuleRecId(o.getRecId())
				.setAuditTrailMasterId(auditTrailMaster.getRecId())
				.setTenantId(o.getTenantId())
				.setDetails(LogFlowDashboardDomain
						.getAuditTrailJsonResponseByDetectedDevices(action, o,tenateNameAndIdMap, productMasterMap, note)))
				.collect(Collectors.toList());
		auditTrailRepository.saveAll(auditTrails);
	}

	@Override
	public Object getAuditTrailList(AuditTrailListRequest auditTrailListRequest) {

		int pageNumber = auditTrailListRequest.getStart() / auditTrailListRequest.getLength(); // Calculate page number
		int length = auditTrailListRequest.getLength();
		Pageable pageable = PageRequest.of(pageNumber, length);

		List<User> users = userRepository.findAll();
		Map<Long, String> createdByIdMap = users.stream()
				.collect(Collectors.toMap(o -> o.getUserId(), o -> LogFlowDashboardUtils
						.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));

		List<AuditTrailMaster> auditTrailMasterList = auditTrailMasterRepository
				.findAllByModuleName(AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD);
		Map<Long, String> recIdAndActionDescMap = auditTrailMasterList.stream()
				.collect(Collectors.toMap(o -> o.getRecId(), o -> o.getActionDesc()));

		Specification<AuditTrail> specification = LogFlowDashboardDomain.getAuditTrailListFilter(auditTrailListRequest,
				recIdAndActionDescMap.keySet());

		Page<AuditTrail> auditTrailPage = auditTrailRepository.findAll(specification, pageable);
		List<AuditTrail> auditTrailList = auditTrailPage.getContent();

		long recordsFilteredTotal = auditTrailPage.getTotalElements();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) recordsFilteredTotal);
		results.setRecordsFiltered((int) recordsFilteredTotal);
		results.setDraw(auditTrailListRequest.getDraw());
		results.setLength(auditTrailListRequest.getLength());
		results.setData(AuditTrailListMapper.map(auditTrailList, createdByIdMap, recIdAndActionDescMap));

		return results;
	}
	
}
