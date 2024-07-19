package aisaac.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.domain.LogFlowDashboardDomain;
import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.domain.datatable.Order;
import aisaac.domain.datatable.Search;
import aisaac.dto.AisaacApiQueueRequestDto;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AutoSuggestionRequiredFileds;
import aisaac.dto.CustomLFDRepository;
import aisaac.dto.MinutesAndLastEventDto;
import aisaac.dto.NotesFormatDto;
import aisaac.entities.AisaacApiQueue;
import aisaac.entities.ApiConfiguration;
import aisaac.entities.ApplicationSettingsMaster;
import aisaac.entities.AssetMaster;
import aisaac.entities.AuditTrail;
import aisaac.entities.AuditTrailMaster;
import aisaac.entities.BusinessUnit;
import aisaac.entities.GlobalSettings;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.LogStopageMasterAuditTrail;
import aisaac.entities.LogStopageReportNotifications;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.Notes;
import aisaac.entities.ProductMaster;
import aisaac.entities.ProductTypeMaster;
import aisaac.entities.SysParameterType;
import aisaac.entities.SysParameterValue;
import aisaac.entities.Tenant;
import aisaac.entities.User;
import aisaac.exception.ValidationException;
import aisaac.payload.mapper.LFDAuditTrailMapper;
import aisaac.payload.mapper.LogStopageMasterAddProductMapper;
import aisaac.payload.mapper.LogStopageMasterEditProductMapper;
import aisaac.payload.mapper.LogStopageMasterMapper;
import aisaac.payload.mapper.LogStopageMasterToNotesMapper;
import aisaac.payload.mapper.LogStoppageGetProductDetailsMapper;
import aisaac.payload.request.DeleteAllProductRequest;
import aisaac.payload.request.DigestMailSettingRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.payload.request.LogFlowMonitorRequestAddProduct;
import aisaac.payload.request.LogFlowMonitorRequestEditProduct;
import aisaac.payload.response.ApiResponse;
import aisaac.payload.response.LFDSettingsResponse;
import aisaac.payload.response.LFMResponse;
import aisaac.repository.AisaacApiQueueRepository;
import aisaac.repository.ApplicationSettingsMasterRepository;
import aisaac.repository.AssetMasterRepository;
import aisaac.repository.AuditTrailMasterRepository;
import aisaac.repository.AuditTrailRepository;
import aisaac.repository.BusinessUnitRepository;
import aisaac.repository.GlobalSettingsRepository;
import aisaac.repository.LogStopageDetectedDevicesRepository;
import aisaac.repository.LogStopageMasterAuditTrailRepository;
import aisaac.repository.LogStopageMasterRepository;
import aisaac.repository.LogStopageReportNotificationsRepository;
import aisaac.repository.NotesRepository;
import aisaac.repository.ProductMasterRepository;
import aisaac.repository.ProductTypeMasterRepository;
import aisaac.repository.SysParamTypeRepository;
import aisaac.repository.SysParameterValueRepository;
import aisaac.repository.TenantRepository;
import aisaac.repository.UserRepository;
import aisaac.util.AppConstants;
import aisaac.util.ExcelUtils;
import aisaac.util.LFDBlukUploadTemplateExcel;
import aisaac.util.LogFlowDashboardUtils;
import aisaac.util.LogFlowMonitorExcel;
import jakarta.persistence.EntityNotFoundException;

@Service

public class LogFlowMonitorServiceImpl implements LogFlowMonitorService {

	@Autowired
	private LogStopageMasterRepository logStopageMasterRepository;

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private CustomLFDRepository customLFDRepository;

	@Autowired
	private LogStopageDetectedDevicesRepository detectedDevicesRepository;

	@Autowired
	private LogStopageReportNotificationsRepository logStopageReportNotificationsRepository;

	@Autowired
	private ApplicationSettingsMasterRepository applicationSettingsMasterRepository;

	@Autowired
	private NotesRepository notesRepository;
	
	@Autowired
	private AuditTrailMasterRepository auditTrailMasterRepository;
	
	@Autowired
	private AuditTrailRepository auditTrailRepository;
	
	@Autowired
	private ProductMasterRepository productMasterRepository;
	
	@Autowired
	private ProductTypeMasterRepository productTypeMasterRepository;
	
	@Autowired
	private SysParameterValueRepository sysParamValueRepository;
	
	@Autowired
	private SysParamTypeRepository sysParamTypeRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LogStopageMasterAuditTrailRepository logStopageMasterAuditTrailRepository;
	
	@Autowired
	private AisaacApiQueueRepository aisaacApiQueueRepository;
	
	@Autowired
	private AssetMasterRepository assetMasterRepository;
	
	@Autowired
	private BusinessUnitRepository businessUnitRepository;
	
	@Autowired
	private GlobalSettingsRepository globalSettingsRepository;
	
	@Override
	public Object getLogFlowMonitorList(LogFlowDashboardDetailsRequest request, List<ProductMaster> productMaster) {

		Page<LogStopageMaster> logStopageMasterPage= commonCodeForLFMListingAndExport(request,productMaster);
		return getLFMListWithRequiredResponse(logStopageMasterPage, request);

	}

	private Page<LogStopageMaster> commonCodeForLFMListingAndExport(LogFlowDashboardDetailsRequest request,
			List<ProductMaster> productMaster) {
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
		// replacing sorting columns
		sortColumn = sortColumn.replace(AppConstants.LOG_STOPPAGE_THRESHOLD_JSON,
				AppConstants.LOG_STOPAGE_THRESHOLD_TIME);
		sortColumn = sortColumn.replace(AppConstants.EMAIL_NOTIFICATION_FREQUENCY_JSON,
				AppConstants.EMAIL_ALERT_FREQUENCY);

		Sort sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortColumn).ascending()
				: Sort.by(sortColumn).descending();
		if (AppConstants.MONITOR_STATUS.equalsIgnoreCase(sortColumn)) {
			sort = sortOrderBy.equalsIgnoreCase(Sort.Direction.ASC.name())
					? Sort.by(Sort.Order.asc(AppConstants.SUPPRESSED), Sort.Order.asc(AppConstants.DISABLED))
					: Sort.by(Sort.Order.desc(AppConstants.SUPPRESSED), Sort.Order.desc(AppConstants.DISABLED));
		}

		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length, sort);

		if (AppConstants.PRODUCT_MASTER_FIELDS.contains(sortColumn)
				|| AppConstants.ASSET_TYPE.equalsIgnoreCase(sortColumn)
				|| AppConstants.PRODUCT_TYPE.equalsIgnoreCase(sortColumn)) {
			pageable = PageRequest.of(pageNumber, length);
		}
		Page<LogStopageMaster> logsPage = null;
		
		Optional<List<Long>> mayBeProductIdsForVendorNameType = LogFlowDashboardUtils
				.getProductIdsByProductVendorOrNameOrType(productMaster, request.getSearchProductVendor(),
						request.getSearchProductName(), request.getSearchProductType(), request.getSearchType());
		List<Long> productIdsForVendorNameType = mayBeProductIdsForVendorNameType.orElse(null);
		if (Objects.nonNull(productIdsForVendorNameType)) {
			request.setProductId(productIdsForVendorNameType);
		}

		Optional<List<Long>> mayBeProductTypeIdsForVendorNameType = LogFlowDashboardUtils
				.getProductTypeIdsByProductVendorOrNameOrType(productMaster, request.getSearchProductVendor(),
						request.getSearchProductName(), request.getSearchProductType());
		List<Long> productTypeIdsForVendorNameType = mayBeProductTypeIdsForVendorNameType.orElse(null);

		if (Objects.nonNull(productTypeIdsForVendorNameType)) {
			request.setProductTypeId(productTypeIdsForVendorNameType);
		}
		
		switch (request.getSearchType()) {
		case 1: {
			
			Specification<LogStopageMaster> fullSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt, searchTxt,
							searchTxt, searchTxt, true, request, LogStopageMaster.class);
			logsPage = logStopageMasterRepository.findAll(fullSearchFilterSpecification, pageable);

			break;
		}
		case 3: {
			
//			Optional<List<Long>> mayBeProductIdsForVendorNameType = LogFlowDashboardUtils
//					.getProductIdsByProductVendorOrNameOrTypeForAdvanceSearch(productMaster, request.getSearchProductVendor(),
//							request.getSearchProductName(), request.getSearchProductType());
//			List<Long> productIdsForVendorNameType = mayBeProductIdsForVendorNameType.orElse(null);
//			if (Objects.nonNull(productIdsForVendorNameType)) {
//				request.setProductId(productIdsForVendorNameType);
//			}
//
//			Optional<List<Long>> mayBeProductTypeIdsForVendorNameType = LogFlowDashboardUtils
//					.getProductTypeIdsByProductVendorOrNameOrTypeForAdvanceSearch(productMaster, request.getSearchProductVendor(),
//							request.getSearchProductName(), request.getSearchProductType());
//			List<Long> productTypeIdsForVendorNameType = mayBeProductTypeIdsForVendorNameType.orElse(null);
//
//			if (Objects.nonNull(productTypeIdsForVendorNameType)) {
//				request.setProductTypeId(productTypeIdsForVendorNameType);
//			}
			
			Specification<LogStopageMaster> advanceSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							request.getSearchProductVendor(), request.getSearchProductName(),
							request.getSearchProductType(), request.getSearchProductIP(),
							request.getSearchProductHostName(), request.getSearchCloudResourcID(),
							request.getSearchColletorIP(), request.getSearchCollectorHostName(),
							request.getSearchToEmail(), request.getSearchCcEmail(), false, request,
							LogStopageMaster.class);
			logsPage = logStopageMasterRepository.findAll(advanceSearchFilterSpecification, pageable);
			break;
		}
		default: {
			Specification<LogStopageMaster> advanceSearchFilterSpecification = LogFlowDashboardDomain
					.getAdvanceOrFullSearchFilterSpecification(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT,
							request.getSearchProductVendor(), request.getSearchProductName(),
							request.getSearchProductType(), request.getSearchProductIP(),
							request.getSearchProductHostName(), request.getSearchCloudResourcID(),
							request.getSearchColletorIP(), request.getSearchCollectorHostName(),
							request.getSearchToEmail(), request.getSearchCcEmail(), false, request,
							LogStopageMaster.class);
			logsPage = logStopageMasterRepository.findAll(advanceSearchFilterSpecification, pageable);
		}
		}
		return logsPage;
	}
	
	@Override
	public Object getAutoCompleteDataForLogFlowMonitor(Map<String, String> request) {
		if (request.isEmpty()) {
			throw new ValidationException("Required Input Parameters are Missing.");
		}
		String paramName = new ArrayList<>(request.keySet()).get(0);
		return customLFDRepository.getDataByColumnNameAndValue(paramName, request.get(paramName),
				LogStopageMaster.class);
	}

	public Object getLFMListWithRequiredResponse( Page<LogStopageMaster> logStopageMasterPage,
			LogFlowDashboardDetailsRequest request) {

		List<LogStopageMaster> logStopageMastersList = logStopageMasterPage.getContent();
		List<Long> logStopageRecIds = logStopageMastersList.stream().map(LogStopageMaster::getRecId)
				.collect(Collectors.toList());

		List<Notes> notes = notesRepository.findAllByTypeRecIdInAndNoteTypeOrderByCreatedDateDesc(logStopageRecIds,
				AppConstants.LOG_FLOW_MONITORING);
		List<User> users = userRepository.findAll();
		Map<Long, String> createdByAndIdMap = users.stream()
				.collect(Collectors.toMap(o -> o.getUserId(), o -> LogFlowDashboardUtils
						.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));

		Map<Long, List<NotesFormatDto>> noteMap = notes.stream()
				.collect(Collectors.groupingBy(Notes::getTypeRecId,
						Collectors.mapping(o -> LogFlowDashboardDomain.getNotesFormatDtoByNote(o, createdByAndIdMap),
								Collectors.toList())));

		List<SysParameterValue> sysParameterValues = sysParamValueRepository.findAll();
		Map<Long, String> assetTypeMapFromSysParam = sysParameterValues.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();
		List<ProductTypeMaster> productTypeMastersList = productTypeMasterRepository.findAllByDeletedFalse();

		Map<Long, String> productTypeIdMap = productTypeMastersList.stream()
				.collect(Collectors.toMap(ProductTypeMaster::getProductTypeId, ProductTypeMaster::getProductType));

		Map<Long, ProductMaster> productMasterMap = productMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> {
					o.setProductType(productTypeIdMap.get(o.getProductTypeId()));
					return o;
				}));

		List<Object> logFlowMonitorList = new LogStopageMasterMapper(logStopageMastersList, noteMap,
				assetTypeMapFromSysParam, productMasterMap).map();
		long recordsFilteredTotal = logStopageMasterPage.getTotalElements();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) recordsFilteredTotal);
		results.setRecordsFiltered((int) recordsFilteredTotal);
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(logFlowMonitorList);

		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object performLogflowMonitorUserActions(String actionType, List<Long> recIds, String note, Long userId
			, Map<String, Long> statusMap, Long moduleRecId) {

		if (StringUtils.isBlank(actionType) || !AppConstants.LOG_FLOW_MONITOR_USER_ACTIONS.contains(actionType)) {
			throw new ValidationException("User Action is not Valid");
		}

		if (ObjectUtils.isEmpty(userId)) {
			throw new ValidationException("UserId is Required to perform User Action");
		}

		if (recIds.isEmpty()) {
			throw new ValidationException(
					"At least One record is required to perform a user " + actionType + " Action");
		}

		if (AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE.equals(actionType) && StringUtils.isBlank(note)) {
			throw new ValidationException("Text Is Required to Perform Add Note User Action");
		}

		List<LogStopageMaster> logStopageMasters = logStopageMasterRepository.findAllById(recIds);

		if (logStopageMasters.isEmpty()) {
			throw new ValidationException("Data is not Available");
		}
		List<Tenant> tenants = tenantRepository.findAll();
		Map<Long, String> tenateNameAndIdMap = tenants.stream()
				.collect(Collectors.toMap(o -> o.getTenantId(), o -> o.getTenantName()));
		
		logStopageMasters = logStopageMasters.stream()
				.map(o -> LogFlowDashboardDomain.getLogStopageMasterByUserAction(note, actionType, o, userId))
				.filter(o -> o != null).collect(Collectors.toList());
		logStopageMasters = logStopageMasterRepository.saveAll(logStopageMasters);

//		List<LogStopageMasterAuditTrail> logStopageMasterAuditTrails = logStopageMasters.stream()
//				.map(o -> LFDAuditTrailMapper.map(o)).collect(Collectors.toList());
//		logStopageMasterAuditTrailRepository.saveAll(logStopageMasterAuditTrails);

		String actionTypeAuditTrail = LogFlowDashboardDomain.getAuditTrailActionsForLogStoppageMaster(actionType,
				logStopageMasters.size());
		
		Map<String, String> noteMap =logStopageMasters.stream().collect(Collectors.toMap(o->o.getProductId() + o.getTenantId()
		+ o.getProductIP() + o.getProductHostName(), o->note));
		
		insertAuditTrailForLogStoppage(actionTypeAuditTrail, logStopageMasters, tenateNameAndIdMap, noteMap,
				Collections.EMPTY_MAP, Collections.EMPTY_MAP);

		// Adding note to the notes table
		if (StringUtils.isNotBlank(note)
				&& (AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE.equalsIgnoreCase(actionType)
						|| AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE.equalsIgnoreCase(actionType))) {
			List<Notes> notes = LogStopageMasterToNotesMapper.addNoteMap(logStopageMasters, note);
			notesRepository.saveAll(notes);
		}

		// TODO insert data for enable and disable action in aisaac_api_queue table for
		// DBR processing
		Optional<GlobalSettings> optionalIsCentralSiemRuleEnabled = globalSettingsRepository.findByParamTypeAndParamName(AppConstants.GLOBAL_SETTINSG_PARAM_TYPE_CENTRAL_SIEM, AppConstants.GLOBAL_SETTINGS_PARAM_NAME_ENABLE_CENTRAL_SIEM_RULE);
		
		String isCentralSiemRuleEnabled = AppConstants.GLOBAL_SETTINGS_PARAM_VALUE_FALSE;
		
		if(optionalIsCentralSiemRuleEnabled.isPresent()) {
			isCentralSiemRuleEnabled = optionalIsCentralSiemRuleEnabled.get().getParamValue();
		}
		
		if(isCentralSiemRuleEnabled.equalsIgnoreCase(AppConstants.GLOBAL_SETTINGS_PARAM_VALUE_TRUE)) {
            
        	insertQueue(logStopageMasters, actionType, statusMap, moduleRecId);
        }

		return AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUCCESS + actionType;
	}

	private void insertQueue(List<LogStopageMaster> list, String status, Map<String
			, Long> sysParameterValuesForStatus, Long moduleRecId) {
		ObjectMapper mapper = new ObjectMapper();
    	
    	AisaacApiQueueRequestDto requestDetails = new AisaacApiQueueRequestDto();
    	Set<Long> recId = new HashSet<>();
    	Set<Long> productId = new HashSet<>();
    	Set<Long> productTypeId = new HashSet<>();
    	
    	list.forEach(lfd -> {
    		recId.add(lfd.getRecId().longValue());
    		productId.add(lfd.getProductId().longValue());
    		productTypeId.add(lfd.getProductTypeId().longValue());
    	});
    	
    	
    	requestDetails.setRecId(recId);
    	requestDetails.setStatus(status);
    	requestDetails.setProductId(productId);
    	requestDetails.setProductTypeId(productTypeId);
    	
    	AisaacApiQueue aisaacApiQueue = new AisaacApiQueue();
    	
    	LocalDateTime createdDate = LocalDateTime.now();
    	LocalDateTime updatedDate = null;
    	
    	aisaacApiQueue.setStatus(sysParameterValuesForStatus.get(AppConstants.SYSPARAM_TYPE_STATUS_VALUE_NEW));
    	aisaacApiQueue.setCreatedDate(createdDate);
    	aisaacApiQueue.setUpdatedDate(updatedDate);
    	aisaacApiQueue.setModuleRecId(moduleRecId);
    	try {
			aisaacApiQueue.setRequestDetails(mapper.writeValueAsString(requestDetails));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	aisaacApiQueue.setResponseDetails(null);
    	
    	aisaacApiQueueRepository.save(aisaacApiQueue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object addProductToLogStoppageMaster(List<LogFlowMonitorRequestAddProduct> request,
			Map<String, Long> productTypeMap, String addAction) {
		ApiResponse response = new ApiResponse();
		List<Tenant> tenants = tenantRepository.findAll();
		Map<Long, String> tenateNameAndIdMap = tenants.stream()
				.collect(Collectors.toMap(o -> o.getTenantId(), o -> o.getTenantName()));
		List<Integer> productIds = new ArrayList<>();
		List<Long> customerIds = new ArrayList<>() ,recIds = new ArrayList<>();
		List<String> productIps = new ArrayList<>(), productHostNames = new ArrayList<>();
		ApplicationSettingsMaster applicationSettingsCustomTrueMaster = applicationSettingsMasterRepository
				.findBySettingsType(AppConstants.LFD_CUSTOM_TRUE_SETTINGS);
		ApplicationSettingsMaster applicationSettingsCustomFalseMaster = applicationSettingsMasterRepository
				.findBySettingsType(AppConstants.LFD_CUSTOM_FALSE_SETTINGS);
		if (applicationSettingsCustomTrueMaster == null
				|| StringUtils.isBlank(applicationSettingsCustomTrueMaster.getDataList())
				|| applicationSettingsCustomFalseMaster == null
				|| StringUtils.isBlank(applicationSettingsCustomFalseMaster.getDataList())) {
			throw new EntityNotFoundException("Application Settings not found for LFD");
		}
		Map<String, String> notesMap = new HashMap<>();

		request.forEach(o -> {
			o.setLogStopageThresholdTime(LogFlowDashboardUtils.getMinutesFromCustomText(o.getLogStoppageThreshold(),
					applicationSettingsCustomTrueMaster.getDataList(),
					applicationSettingsCustomFalseMaster.getDataList()));
			o.setEmailAlertFrequency(LogFlowDashboardUtils.getMinutesFromCustomText(o.getEmailNotificationFrequency(),
					applicationSettingsCustomTrueMaster.getDataList(),
					applicationSettingsCustomFalseMaster.getDataList()));
			productIds.add(o.getProductID());
			customerIds.add(o.getTenantId());
			productIps.add(o.getProductIP());
			productHostNames.add(o.getProductHostName());
			if (Objects.nonNull(o.getRecId())) {
				recIds.add(o.getRecId());
			}
			notesMap.put(o.getProductID() + o.getTenantId() + o.getProductIP() + o.getProductHostName(), o.getNote());
		});

		// Finding detected devices and analyzing the device status
		List<LogStoppageDetectedDevices> detectedDevices = detectedDevicesRepository
				.findAllByDeletedAndRecIdIn(AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT, recIds);
//		List<LogStoppageDetectedDevices> detectedDevices = detectedDevicesRepository.findAllByQuery(
//				AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT, productIds, customerIds, productIps,
//				productHostNames);

		Map<String, MinutesAndLastEventDto> detectedDevicesMinutesMap = detectedDevices.stream()
				.filter(o -> o.getLastEventReceived() != null)
				.collect(Collectors.toMap(
						o -> o.getProductId() + o.getTenantId() + o.getProductIP() + o.getProductHostName(),
						o -> LogFlowDashboardUtils
								.getMinutesDifferenceFromDetectedDeviceCreatedDateAndLastEventReceived(o)));

		// Mapping request to logStopageMaster
		List<LogStopageMaster> logStopageMasters = new LogStopageMasterAddProductMapper(request, tenateNameAndIdMap,
				detectedDevicesMinutesMap, productTypeMap).map();

		// filter duplicate data
		List<LogStopageMaster> existingData = logStopageMasterRepository
				.findAllByDeletedAndTenantIdInAndProductIdInAndProductIPInAndProductHostNameIn(
						AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT, customerIds, productIds, productIps,
						productHostNames);

		logStopageMasters = logStopageMasters.stream()
				.filter(log1 -> existingData.stream()
						.noneMatch(log2 -> log1.getProductId().equals(log2.getProductId())
								&& log1.getTenantId().equals(log2.getTenantId())
								&& log1.getProductIP().equals(log2.getProductIP())
								&& log1.getProductHostName().equals(log2.getProductHostName())))
				.collect(Collectors.toList());
		if (logStopageMasters.isEmpty()) {
			response.setStatus(422);
			response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_DUPLICATE);
			return response;
		}

		List<LogStopageMaster> logStopageMastersSaved = logStopageMasterRepository.saveAll(logStopageMasters);

		if (logStopageMasters.size() == logStopageMastersSaved.size()) {
			
			//Updating Detected devices Delete Flag 
			if (AppConstants.LFD_ACTION_TYPE_ADDED_LOG_FLOW_MONITORING.equalsIgnoreCase(addAction)) {
				if(logStopageMasters.size()>1) {
					addAction=AppConstants.LFD_ACTION_TYPE_ADDED_LOG_FLOW_MONITORING_BULK;
				}
				detectedDevices = detectedDevices.stream()
						.map(o ->
						{ 
							o.setDeleted(AppConstants.ADD_TO_LOG_FLOW_MONITOR_USER_ACTION_DELETE);
							o.setUpdatedDate(LocalDateTime.now());
							return o;
						})
						.collect(Collectors.toList());
				detectedDevicesRepository.saveAll(detectedDevices);

			}

//			List<LogStopageMasterAuditTrail> logStopageMasterAuditTrails = logStopageMastersSaved.stream()
//					.map(o -> LFDAuditTrailMapper.map(o)).collect(Collectors.toList());
//			logStopageMasterAuditTrailRepository.saveAll(logStopageMasterAuditTrails);
			// Mapping notes and inserting
			List<Notes> notesEntityList = new LogStopageMasterToNotesMapper(logStopageMastersSaved, notesMap).map();
			notesRepository.saveAll(notesEntityList);
			insertAuditTrailForLogStoppage(addAction, logStopageMastersSaved, tenateNameAndIdMap, notesMap,
					Collections.EMPTY_MAP, Collections.EMPTY_MAP);
			
			response.setStatus(200);
			response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_SAVE);
			return response;
		}
		response.setStatus(400);
		response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_FAILED);
		return response;
	}

	@Override
	public Object logStoppageMasterEditProduct(List<LogFlowMonitorRequestEditProduct> request,Map<String, Long> productTypeMap) {
		ApiResponse response = new ApiResponse();
		List<Long> recIds = new ArrayList<>();
		Map<String, String> notesMap = new HashMap<>();
		
		ApplicationSettingsMaster applicationSettingsCustomTrueMaster = applicationSettingsMasterRepository
				.findBySettingsType(AppConstants.LFD_CUSTOM_TRUE_SETTINGS);
		ApplicationSettingsMaster applicationSettingsCustomFalseMaster = applicationSettingsMasterRepository
				.findBySettingsType(AppConstants.LFD_CUSTOM_FALSE_SETTINGS);
		if (applicationSettingsCustomTrueMaster == null
				|| StringUtils.isBlank(applicationSettingsCustomTrueMaster.getDataList())
				|| applicationSettingsCustomFalseMaster == null
				|| StringUtils.isBlank(applicationSettingsCustomFalseMaster.getDataList())) {
			throw new EntityNotFoundException("Application Settings not found for LFD");
		}
		
		request.forEach(o -> {
			
			o.setLogStopageThresholdTime(LogFlowDashboardUtils.getMinutesFromCustomText(o.getLogStoppageThreshold(),
					applicationSettingsCustomTrueMaster.getDataList(),
					applicationSettingsCustomFalseMaster.getDataList()));
			o.setEmailAlertFrequency(LogFlowDashboardUtils.getMinutesFromCustomText(o.getEmailNotificationFrequency(),
					applicationSettingsCustomTrueMaster.getDataList(),
					applicationSettingsCustomFalseMaster.getDataList()));
			
			recIds.add(o.getRecId());
			notesMap.put(String.valueOf(o.getRecId()), o.getNote());
		});

		List<LogStopageMaster> logStopageMasters = logStopageMasterRepository.findAllById(recIds);
		
		Map<Long, LogStopageMaster> oldLogStopagemasterMap = logStopageMasters.stream()
				.collect(Collectors.toMap(o -> o.getRecId(), o -> {
					try {
						return o.clone();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						return o;
					}
				}));

		if (logStopageMasters.isEmpty()) {
			throw new ValidationException("Data is not Available for the Request");
		}
		List<Tenant> tenants = tenantRepository.findAll();
		Map<Long, String> tenateNameAndIdMap = tenants.stream()
				.collect(Collectors.toMap(o -> o.getTenantId(), o -> o.getTenantName()));

		

		List<LogStopageMaster> logStopageMastersFinal = new LogStopageMasterEditProductMapper(request,
				logStopageMasters, tenateNameAndIdMap, productTypeMap).map();

		logStopageMastersFinal = logStopageMasterRepository.saveAll(logStopageMastersFinal);
		if (logStopageMasters.size() == logStopageMastersFinal.size()) {
//			List<LogStopageMasterAuditTrail> logStopageMasterAuditTrails= logStopageMastersFinal.stream().map(o->LFDAuditTrailMapper.map(o)).collect(Collectors.toList());			
//			logStopageMasterAuditTrailRepository.saveAll(logStopageMasterAuditTrails);
			
			List<Notes> oldNotesList = notesRepository.findAllByTypeRecIdInAndNoteTypeOrderByCreatedDateDesc(recIds,
					AppConstants.LOG_FLOW_MONITORING);
			Map<Long, String> oldNotesMap = oldNotesList.stream().collect(
					Collectors.toMap(o -> o.getTypeRecId(), o -> o.getNote(), (existing, newValue) -> existing));

			insertAuditTrailForLogStoppage(AppConstants.LFD_ACTION_TYPE_EDITED, logStopageMastersFinal, tenateNameAndIdMap,
					notesMap,oldLogStopagemasterMap,oldNotesMap);

			// Mapping notes and inserting
			List<Notes> notesEntityList = new LogStopageMasterToNotesMapper(logStopageMastersFinal, notesMap).editMap();
			notesRepository.saveAll(notesEntityList);
			response.setStatus(200);
			response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_EDIT);
			return response;
		}
		response.setStatus(400);
		response.setMessage(AppConstants.LOG_FLOW_MONITOR_PRODUCT_EDIT_FAILED);
		return response;
	}

	@Override
	public Object uploadBulkLogFlowMonitorData(MultipartFile multipartfile, Integer userId) {
		if (ObjectUtils.isEmpty(multipartfile) || multipartfile.getSize() == 0) {
			throw new ValidationException("File can not be empty.");
		}

		String fileName = multipartfile.getOriginalFilename();

		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toUpperCase();
		if (!AppConstants.LOG_FLOW_MONITOT_UPLOAD_ACCESSIBLE_FORMAT.contains(fileExtension)) {
			throw new ValidationException(
					"Please upload file in valid format. Only .xls / .xlsx  file can be uploaded. ");
		}
		List<LogStopageMaster> logStopageMasters = new ArrayList<>();
		try {
			logStopageMasters = getLogStoppageMasterFromExcel(multipartfile, userId, logStopageMasters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<LogStopageMaster> logStopageMastersSaved = logStopageMasterRepository.saveAll(logStopageMasters);
		if (logStopageMasters.size() == logStopageMastersSaved.size()) {
			return logStopageMastersSaved.size() + AppConstants.LOG_FLOW_MONITOR_PRODUCT_SAVE;
		}

		return AppConstants.LOG_FLOW_MONITOR_PRODUCT_FAILED;

	}

	public List<LogStopageMaster> getLogStoppageMasterFromExcel(MultipartFile fileName, Integer userId,
			List<LogStopageMaster> logStopageMasters) throws Exception {
		Workbook workbook = new XSSFWorkbook(fileName.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		List<String> columNames = new ArrayList<>();
		List<Tenant> tenants = tenantRepository.findAll();
		Map<String, Long> tenateNameAndIdMap = tenants.stream()
				.collect(Collectors.toMap(o -> o.getTenantName(), o -> o.getTenantId()));
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0)
				columNames = LogFlowDashboardUtils.getColumnNamesFromExcelRow(row);
			else {
				LogStopageMaster log = LogFlowDashboardDomain.getLogStopageMasterFromRow(row, columNames,
						tenateNameAndIdMap, userId);
				if (log != null) {
					logStopageMasters.add(log);
				}
			}
		}
		workbook.close();
		return logStopageMasters;
	}

	@Override
	public Object getAutoCompleteDataForLFMWithOptionalOrganizationIDs(List<Integer> orgIds, String fieldName) {

		if (!AppConstants.AUTO_POPULATE_FIELDS_FOR_LFM.contains(fieldName)) {
			throw new ValidationException("URL is not Valid");
		}
		return customLFDRepository.getDataByColumnNameWithOptionalOrganizationIds(fieldName, orgIds,
				LogStopageMaster.class);
	}

	@Override
	public Object saveDigestEmailSettings(DigestMailSettingRequest request) {

		LogStopageReportNotifications data = logStopageReportNotificationsRepository.findById(request.getTenantId())
				.orElse(new LogStopageReportNotifications());
		data.setEnableMail(request.getEnableMail());
		data.setMailTriggeredHour(request.getMailTriggeredHour());

		if (data.getCreatedDate() != null) {
			if (request.getEnableMail() == 1) {
				data.setToEmail(request.getToEmail());
				data.setCcEmail(request.getCcEmail());
			}
			data.setUpdatedBy(request.getUserId());
			data.setUpdatedDate(LocalDate.now());

		} else {
			data.setCreatedBy(request.getUserId());
			data.setCreatedDate(LocalDate.now());
			data.setToEmail(request.getToEmail());
			data.setCcEmail(request.getCcEmail());
			data.setTenantId(request.getTenantId());
			data.setTenantName(request.getTenantName());
		}

		data = logStopageReportNotificationsRepository.save(data);
		return AppConstants.DIGEST_MAIL_SETTINGS_SUCCESS;
	}

	@Override
	public Object getLogStopageDetailsById(Long recId) {

		LogStopageMaster logStopageMaster = logStopageMasterRepository.findById(recId)
				.orElseThrow(() -> new EntityNotFoundException("No Data Available"));
		List<Tenant> tenList = tenantRepository.findAll();
		Map<Long, String> typeMap = tenList.stream()
				.collect(Collectors.toMap(te -> te.getTenantId(), te -> te.getTenantName()));
		List<Notes> notes = notesRepository.findAllByTypeRecIdAndNoteTypeOrderByCreatedDateDesc(logStopageMaster.getRecId(),
				AppConstants.LOG_FLOW_MONITORING);
		List<User> users= userRepository.findAll();
		Map<Long, String> createdByAndIdMap = users.stream().collect(
				Collectors.toMap(o -> o.getUserId(), o -> LogFlowDashboardUtils.getDisplayUsername(o.getDisplayName(),
						o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));
	
		Map<Long, List<NotesFormatDto>> noteMap = notes.stream().collect(
				Collectors.groupingBy(Notes::getTypeRecId, Collectors.mapping(o->LogFlowDashboardDomain.getNotesFormatDtoByNote(o,createdByAndIdMap), Collectors.toList())));

		return LogStoppageGetProductDetailsMapper.map(logStopageMaster, typeMap, noteMap);

	}

	@Override
	public Object getLFDSettings(List<ProductMaster> productMasters,Integer exportLimit) {

//		ApplicationSettingsMaster applicationSettingsCustomFalseMaster = applicationSettingsMasterRepository
//				.findBySettingsType(AppConstants.LFD_CUSTOM_FALSE_SETTINGS);
//		ApplicationSettingsMaster applicationSettingsCustomTrueMaster = applicationSettingsMasterRepository
//				.findBySettingsType(AppConstants.LFD_CUSTOM_TRUE_SETTINGS);

		List<ApplicationSettingsMaster> applicationSettingsMasters = applicationSettingsMasterRepository.findAll();

		Map<String, ApplicationSettingsMaster> applicationSettingsMasterMap = applicationSettingsMasters.stream()
				.collect(Collectors.toMap(o -> o.getSettingsType(), o -> o,(existing,newValue)->existing));

		ApplicationSettingsMaster applicationSettingsCustomFalseMaster = applicationSettingsMasterMap
				.get(AppConstants.LFD_CUSTOM_FALSE_SETTINGS);
		ApplicationSettingsMaster applicationSettingsCustomTrueMaster = applicationSettingsMasterMap
				.get(AppConstants.LFD_CUSTOM_TRUE_SETTINGS);
		ApplicationSettingsMaster serverity = applicationSettingsMasterMap.get(AppConstants.LFD_SERVERITY_SETTINGS);
		ApplicationSettingsMaster monitorStatus = applicationSettingsMasterMap.get(AppConstants.LFD_MONITOR_STATUS_SETTINGS);
		List<String> monitorStatusSorted=Arrays.asList(LogFlowDashboardUtils.removeSlashFromString(monitorStatus.getDataList()));
		Collections.sort(monitorStatusSorted);
		
		List<SysParameterType> sysParameterTypes = sysParamTypeRepository
				.findAllByParamType(AppConstants.SYSPARAM_ASSET_TYPE);

		List<Long> sysParamTypeIds = sysParameterTypes.stream().map(SysParameterType::getParaTypeId)
				.collect(Collectors.toList());

		List<SysParameterValue> sysParameterValues = sysParamValueRepository.findByParamTypeIdIn(sysParamTypeIds)
				.orElseThrow(() -> new EntityNotFoundException("Application Settings not found for Asset Type"));

		Map<Long, String> assetTypeMapFromSysParam = sysParameterValues.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		if (applicationSettingsCustomFalseMaster == null
				|| StringUtils.isBlank(applicationSettingsCustomFalseMaster.getDataList())
				|| applicationSettingsCustomTrueMaster == null
				|| StringUtils.isBlank(applicationSettingsCustomTrueMaster.getDataList()) || serverity == null
				|| StringUtils.isBlank(serverity.getDataList())
				|| monitorStatus == null
				|| StringUtils.isBlank(monitorStatus.getDataList())) {
			throw new EntityNotFoundException("Application Settings not found for LFD");
		}

		Map<String, List<ProductMaster>> map = productMasters.stream()
				.collect(Collectors.groupingBy(a -> a.getProductVendor()));
		Set<String> deviceVendorList = (map != null && map.size() > 0) ? map.keySet() : new HashSet<String>();
		Map<String, Object> pvpnmap = new HashMap<>();
		pvpnmap.put("vendors", deviceVendorList);
		pvpnmap.put("vendors_names", map);

		LFDSettingsResponse lfdSettings = new LFDSettingsResponse();
		lfdSettings.setCustom(
				LogFlowDashboardUtils.getListFromJsonString(applicationSettingsCustomTrueMaster.getDataList()));
		lfdSettings.setDefaults(
				LogFlowDashboardUtils.getListFromJsonString(applicationSettingsCustomFalseMaster.getDataList()));
		lfdSettings.setAssetType(assetTypeMapFromSysParam);
		lfdSettings.setSeverity(LogFlowDashboardUtils.removeSlashFromString(serverity.getDataList()));

		lfdSettings.setMonitorStatus(monitorStatusSorted);
		
		lfdSettings.setDeviceMasterList(pvpnmap);
		lfdSettings.setExportLimit(exportLimit);
		return lfdSettings;
	}

	private void insertAuditTrailForLogStoppage(String action, List<LogStopageMaster> newLogList,
			Map<Long, String> tenantMap, Map<String, String> notesMap,
			Map<Long, LogStopageMaster> oldLogStopagemasterMap,Map<Long, String> oldNotesMap) {

//		if(true) {
//			return;
//		}
		List<ProductTypeMaster> productTypeMastersList = productTypeMasterRepository.findAllByDeletedFalse();
		Map<Long, String> productTypeMap = productTypeMastersList.stream()
				.collect(Collectors.toMap(o -> o.getProductTypeId(), o -> o.getProductType()));
		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();

		Map<Long, ProductMaster> productMasterMap = productMasters.stream().map(o -> {
			o.setProductType(productTypeMap.get(o.getProductTypeId()));
			return o;
		}).collect(Collectors.toMap(o -> o.getProductId(), o -> o));

		List<SysParameterValue> sysParameterValues = sysParamValueRepository.findAll();

		Map<Long, String> assetTypeMapFromSysParam = sysParameterValues.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		AuditTrailMaster auditTrailMaster = auditTrailMasterRepository.findByActionTypeAndModuleName(action,
				AppConstants.API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD, AuditTrailMaster.class);

		if (Objects.isNull(auditTrailMaster)) {
			return;
		}

//		AuditTrailDetailsOnly auditTrailDetailsOnly = auditTrailRepository
//				.findTop1ByModuleRecIdOrderByRecIdDesc(auditTrailMaster.getRecId(), AuditTrailDetailsOnly.class);

		List<AuditTrail> auditTrails = new ArrayList<>();
		auditTrails = newLogList.stream().map(o -> new AuditTrail()
				.setCreatedBy(Objects.nonNull(o.getUpdatedBy()) ? o.getUpdatedBy() : Long.valueOf(o.getCreatedBy()))
				.setCreatedDate(LocalDateTime.now())
				.setModuleRecId(o.getRecId())
				.setAuditTrailMasterId(auditTrailMaster.getRecId())
				.setTenantId(o.getTenantId())
				.setDetails(getAuditTrailJsonResponseByLogStopageMaster(action, o, tenantMap, assetTypeMapFromSysParam,
						productMasterMap, o.getRecId(), auditTrailMaster.getRecId(), notesMap,
						oldLogStopagemasterMap.get(o.getRecId()), oldNotesMap.get(o.getRecId()))))
				.collect(Collectors.toList());
		auditTrailRepository.saveAll(auditTrails);
	}

	private String getAuditTrailJsonResponseByLogStopageMaster(String action, LogStopageMaster newLog,
			Map<Long, String> tenantMap, Map<Long, String> assetTypeMap, Map<Long, ProductMaster> productMasterMap,
			Long moduleRecId, Long auditTrailMasterId, Map<String, String> notesMap,
			LogStopageMaster oldLogStopageMaster, String oldNotes) {
		AuditTrailDetailsOnly auditTrailDetailsOnly = auditTrailRepository
				.findTop1ByModuleRecIdAndAuditTrailMasterIdOrderByRecIdDesc(moduleRecId, auditTrailMasterId,
						AuditTrailDetailsOnly.class);
		return LogFlowDashboardDomain.getAuditTrailJsonResponseByLogStopageMaster(action, newLog, tenantMap,
				assetTypeMap, productMasterMap, auditTrailDetailsOnly, notesMap, oldLogStopageMaster, oldNotes);
	}
	
	@Override
	public Object deleteProductByTenantId(DeleteAllProductRequest allProductRequest) {
		List<LogStopageMaster> logStopageMasters = logStopageMasterRepository.findAllByDeletedAndTenantId(
				AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT, allProductRequest.getTenantId());

		if (logStopageMasters.isEmpty()) {
			throw new EntityNotFoundException("Data is not Available for the Tenant Id " + allProductRequest.getTenantId());
		}

		logStopageMasters=logStopageMasters.stream().map(o->{
			o.setDeleted(true);
			o.setUpdatedBy(allProductRequest.getUserId());
			o.setUpdatedDate(LocalDateTime.now());
			return o;
		}).collect(Collectors.toList());
		List<LogStopageMaster> logStopageMastersSaved= logStopageMasterRepository.saveAll(logStopageMasters);
		
		List<LogStopageMasterAuditTrail> logStopageMasterAuditTrails= logStopageMastersSaved.stream().map(o->LFDAuditTrailMapper.map(o)).collect(Collectors.toList());			
		logStopageMasterAuditTrailRepository.saveAll(logStopageMasterAuditTrails);
		
		
		if (logStopageMasters.size() == logStopageMastersSaved.size()&&
			StringUtils.isNotBlank(allProductRequest.getAddNote())) {
				List<Notes> notes = LogStopageMasterToNotesMapper.addNoteMap(logStopageMasters,
						allProductRequest.getAddNote());
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

		List<LogStopageMaster> logStopageMasterList = logStopageMasterRepository
				.findAllByDeletedAndTenantIdIn(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT, tenantIdList);
		Map<Long, List<LogStopageMaster>> logStopageMasterListMap = logStopageMasterList.stream()
				.collect(Collectors.groupingBy(LogStopageMaster::getTenantId));

		Map<Long, AutoSuggestionRequiredFileds> response = new HashMap<>();

//		List<String> productIPList = new ArrayList<>();
//		List<String> productHostNameList = new ArrayList<>();
//		List<String> collectorIPList = new ArrayList<>();
//		List<String> productNameList = new ArrayList<>();
//		List<String> productVendorList = new ArrayList<>();
//		List<String> productTypeList = new ArrayList<>();
//		

		logStopageMasterListMap.forEach((tenantId, logStopageList) -> {
			AutoSuggestionRequiredFileds autoSuggestionRequiredFileds = new AutoSuggestionRequiredFileds();
//			productIPList.clear();
//			productHostNameList.clear();
//			collectorIPList.clear();
//			productNameList.clear();
//			productVendorList.clear();
//			productTypeList.clear();
			Set<String> productIPList = new HashSet<>();
			Set<String> productHostNameList = new HashSet<>();
			Set<String> collectorIPList = new HashSet<>();
			Set<String> productNameList = new HashSet<>();
			Set<String> productVendorList = new HashSet<>();
			Set<String> productTypeList = new HashSet<>();

			logStopageList.forEach(logStopageMaster -> {
				ProductMaster productMaster = null;
				if (ObjectUtils.isNotEmpty(logStopageMaster.getProductIP()))
					productIPList.add(logStopageMaster.getProductIP());

				if (ObjectUtils.isNotEmpty(logStopageMaster.getProductHostName()))
					productHostNameList.add(logStopageMaster.getProductHostName());

				if (ObjectUtils.isNotEmpty(logStopageMaster.getCollectorAddress()))
					collectorIPList.add(logStopageMaster.getCollectorAddress());
				if (Objects.isNull(logStopageMaster.getProductId())) {
					logStopageMaster.setProductId(0);
				}
				productMaster = productMasterMap.get(logStopageMaster.getProductId().longValue());
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

	@Override
	public Object getLFDBulkUploadTemplate(List<ProductMaster> productMastersList,
			ApiConfiguration filePathGlobalSettings, int exportLimit) {

		if (ObjectUtils.isEmpty(filePathGlobalSettings)
				|| StringUtils.isBlank(filePathGlobalSettings.getParamValue())) {
			throw new EntityNotFoundException("Export Path is not Configured in API Configurations ");
		}
		String path = filePathGlobalSettings.getParamValue();
		List<Tenant> tenants = tenantRepository.findAll();
		List<String> tenantNames = tenants.stream().map(Tenant::getTenantName).collect(Collectors.toList());

		List<ApplicationSettingsMaster> applicationSettingsMasters = applicationSettingsMasterRepository.findAll();

		Map<String, ApplicationSettingsMaster> applicationSettingsMasterMap = applicationSettingsMasters.stream()
				.collect(Collectors.toMap(o -> o.getSettingsType(), o -> o, (existing, newValue) -> existing));

		ApplicationSettingsMaster serverity = applicationSettingsMasterMap.get(AppConstants.LFD_SERVERITY_SETTINGS);
		ApplicationSettingsMaster monitorStatus = applicationSettingsMasterMap
				.get(AppConstants.LFD_MONITOR_STATUS_SETTINGS);
		ApplicationSettingsMaster customFalseSettings = applicationSettingsMasterMap
				.get(AppConstants.LFD_CUSTOM_FALSE_SETTINGS);

		List<SysParameterType> sysParameterTypes = sysParamTypeRepository
				.findAllByParamType(AppConstants.SYSPARAM_ASSET_TYPE);

		List<Long> sysParamTypeIds = sysParameterTypes.stream().map(SysParameterType::getParaTypeId)
				.collect(Collectors.toList());

		List<SysParameterValue> sysParameterValues = sysParamValueRepository.findByParamTypeIdIn(sysParamTypeIds)
				.orElseThrow(() -> new EntityNotFoundException("Application Settings not found for Asset Type"));

		List<String> assetTypeList = sysParameterValues.stream().map(SysParameterValue::getParamValue)
				.collect(Collectors.toList());

		if (serverity == null || StringUtils.isBlank(serverity.getDataList()) || monitorStatus == null
				|| StringUtils.isBlank(monitorStatus.getDataList())) {
			throw new EntityNotFoundException("Application Settings not found for LFD");
		}

		try {
			path = ExcelUtils.downloadToPath(
					new LFDBlukUploadTemplateExcel(productMastersList, tenantNames, assetTypeList,
							LogFlowDashboardUtils.removeSlashFromString(serverity.getDataList()),
							LogFlowDashboardUtils.removeSlashFromString(monitorStatus.getDataList()), exportLimit,
							LogFlowDashboardUtils.getListFromJsonString(customFalseSettings.getDataList())),
					path, AppConstants.LFD_BULK_UPLOAD_TEMPLATE_NAME);
		} catch (IOException e) {
		}
		return path;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object exportLogFlowMonitorList(LogFlowDashboardDetailsRequest request, List<ProductMaster> productMasters,
			Integer limit, String path) {
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIME_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		
		//when Date selected as local time format in UI
		if(StringUtils.isNotBlank(request.getDateString())) {
			currentDateTime=request.getDateString();
		}
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);

		Page<LogStopageMaster> logStopageMasterPage = commonCodeForLFMListingAndExport(request, productMasters);

		DatatablePage<Object> datatable = (DatatablePage<Object>) getLFMListWithRequiredResponse(logStopageMasterPage,
				request);

		List<Object> exportListData = datatable.getData();
		//Bu Name map for export
		Map<Long, String> assetIdBuNameMap = Collections.EMPTY_MAP;
		if (request.isBuRequired() && !exportListData.isEmpty()) {
			List<Long> assetIdList = exportListData.stream().map(obj -> ((LFMResponse) obj).getAssetId())
					.collect(Collectors.toList());
			List<AssetMaster> assetMastersList = assetMasterRepository.findAllById(assetIdList);
			List<Long> buIdList = assetMastersList.stream().map(AssetMaster::getBuId).collect(Collectors.toList());
			List<BusinessUnit> businessUnitsList = businessUnitRepository.findAllById(buIdList);
			Map<Long, String> buIdMap = businessUnitsList.stream()
					.collect(Collectors.toMap(BusinessUnit::getBuId, BusinessUnit::getBuName));

			assetIdBuNameMap = assetMastersList.stream().collect(Collectors.toMap(AssetMaster::getAssetId,
					o -> buIdMap.getOrDefault(o.getBuId(), AppConstants.EXCEL_NULL_VALUE)));
		}
		
		boolean moreThanOnePartsAvailable = Integer.compare((int) logStopageMasterPage.getTotalElements(), limit) > 0;

		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(
				AppConstants.LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START,
				AppConstants.LOG_FLOW_DASHBOARD_MONITORING_FILENAME, currentDateTime, moreThanOnePartsAvailable, part,
				itIsLastPart, AppConstants.LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX);
		try {
			path = ExcelUtils.downloadToPath(
					new LogFlowMonitorExcel(exportListData, assetIdBuNameMap, request.isBuRequired()), path, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			// first time only data is less than export limit
			if (((int) logStopageMasterPage.getTotalElements() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart(((int) logStopageMasterPage.getTotalElements() - offset) <= limit);
		}
		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.EXPORT_FILE_NAME, fileName,
				AppConstants.PART_FILE_INFO, returnDataTable);
	}
	
	private List<String[]> getRequiredListDataForLFMExport(Page<LogStopageMaster> logStopageMasterPage) {
		List<LogStopageMaster> logStopageMastersList = logStopageMasterPage.getContent();
		List<Long> logStopageRecIds = logStopageMastersList.stream().map(LogStopageMaster::getRecId)
				.collect(Collectors.toList());

		List<Notes> notes = notesRepository.findAllByTypeRecIdInAndNoteTypeOrderByCreatedDateDesc(logStopageRecIds,
				AppConstants.LOG_FLOW_MONITORING);
		List<User> users = userRepository.findAll();
		Map<Long, String> createdByAndIdMap = users.stream()
				.collect(Collectors.toMap(o -> o.getUserId(), o -> LogFlowDashboardUtils
						.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));

		Map<Long, List<NotesFormatDto>> noteMap = notes.stream()
				.collect(Collectors.groupingBy(Notes::getTypeRecId,
						Collectors.mapping(o -> LogFlowDashboardDomain.getNotesFormatDtoByNote(o, createdByAndIdMap),
								Collectors.toList())));

		List<SysParameterValue> sysParameterValues = sysParamValueRepository.findAll();
		Map<Long, String> assetTypeMapFromSysParam = sysParameterValues.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		List<ProductMaster> productMasters = productMasterRepository.findAllByDeletedFalse();

		Map<Long, ProductMaster> productMasterMap = productMasters.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> o));

		return LogStopageMasterMapper.export(logStopageMastersList, noteMap, assetTypeMapFromSysParam,
				productMasterMap);
	}
}
