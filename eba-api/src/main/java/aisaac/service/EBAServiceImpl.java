package aisaac.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import aisaac.domain.EBADomain;
import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.dto.AdrEnitityScoreAuditTrail;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AuditTrailNewOldValueDto;
import aisaac.dto.EntityScoreDto;
import aisaac.entities.AdUserDetail;
import aisaac.entities.AdrEntityScore;
import aisaac.entities.AssetMaster;
import aisaac.entities.AssetTag;
import aisaac.entities.AuditTrail;
import aisaac.entities.AuditTrailMaster;
import aisaac.entities.CtmtAffectedEntitiesOthers;
import aisaac.entities.ProductMaster;
import aisaac.entities.SysParameterType;
import aisaac.entities.SysParameterValue;
import aisaac.entities.TagValue;
import aisaac.entities.ThreatCopy;
import aisaac.entities.Ticket;
import aisaac.entities.User;
import aisaac.exception.ResourceNotFoundException;
import aisaac.payload.mapper.AssetListMapper;
import aisaac.payload.mapper.EntitiesTileListMapper;
import aisaac.payload.mapper.EntityScoreMapper;
import aisaac.payload.mapper.RiskScoreDonutMapper;
import aisaac.payload.mapper.ThreatListMapper;
import aisaac.payload.mapper.TicketListMapper;
import aisaac.payload.request.AddToWatchListRequest;
import aisaac.payload.request.AssetsListRequest;
import aisaac.payload.request.EntitiesTileListRequest;
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.TicketListRequest;
import aisaac.payload.response.EBASettingsResponse;
import aisaac.payload.response.WatchListAuditTrailResponse;
import aisaac.repository.AdUserDetailsRepository;
import aisaac.repository.AdrEntityScoreRepository;
import aisaac.repository.AssetMasterRepository;
import aisaac.repository.AssetTagRepository;
import aisaac.repository.AuditTrailMasterRepository;
import aisaac.repository.AuditTrailRepository;
import aisaac.repository.CtmtAffectedEntitiesOthersRepository;
import aisaac.repository.ProductMasterRepository;
import aisaac.repository.SysParamTypeRepository;
import aisaac.repository.SysParameterValueRepository;
import aisaac.repository.TagValueRepository;
import aisaac.repository.ThreatCopyRepository;
import aisaac.repository.TicketRepository;
import aisaac.repository.UserRepository;
import aisaac.util.AppConstants;
import aisaac.util.EbaUtil;
import aisaac.util.ExcelUtils;
import aisaac.util.MappedAssetExportExcel;
import aisaac.util.ThreatExportExcel;
import jakarta.persistence.EntityNotFoundException;
@Service
public class EBAServiceImpl implements EBAService{

	@Autowired
	private AdrEntityScoreRepository adrEntityScoreRepository;
	
	@Autowired
	private ThreatCopyRepository threatCopyRepository;
	
	@Autowired
	private AdUserDetailsRepository adUserDetailsRepository;
	
	@Autowired
	private AuditTrailMasterRepository auditTrailMasterRepository;
	
	@Autowired
	private AuditTrailRepository auditTrailRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductMasterRepository productMasterRepository;
	
	@Autowired
	private AssetMasterRepository assetMasterRepository;
	
	@Autowired
	private TagValueRepository tagValueRepository;
	
	@Autowired
	private AssetTagRepository assetTagRepository;

	@Autowired
	private SysParamTypeRepository sysParamTypeRepository;
	
	@Autowired
	private SysParameterValueRepository sysParameterValueRepository;
	
	@Autowired
	private CtmtAffectedEntitiesOthersRepository ctmtAffectedEntitiesOthersRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Override
	public Object getEntitiesTileList(EntitiesTileListRequest request) {
		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);

		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TYPE_PARAMETER_ENTITY_TYPE)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TYPE_PARAMETER_ENTITY_TYPE));

		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TYPE_PARAMETER_ENTITY_TYPE));

		Specification<AdrEntityScore> specification = EBADomain.getEbaEntitiesTileFilter(request);
		Page<AdrEntityScore> page = adrEntityScoreRepository.findAll(specification, pageable);

		List<AdrEntityScore> adrEntityScoresList = page.getContent();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) page.getTotalElements());
		results.setRecordsFiltered((int) page.getTotalElements());
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(EntitiesTileListMapper.map(adrEntityScoresList, sysParameterValuesList));
		return results;
	}

	@Override
	public Object getThreatList(ThreatListRequest request) {
		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		Specification<ThreatCopy> threatSpecification = EBADomain.getEbaThreatListSpecification(request);
		Page<ThreatCopy> threatPage = threatCopyRepository.findAll(threatSpecification, pageable);
		List<ThreatCopy> threatList = threatPage.getContent();

		List<Long> adUserList = threatList.stream().map(ThreatCopy::getAdUserId).collect(Collectors.toList());

		List<AdUserDetail> adUserDetailsList = adUserDetailsRepository.findAllById(adUserList);
		Map<Long, String> adUserIdMap = adUserDetailsList.stream()
				.collect(Collectors.toMap(o -> o.getRecId(), o -> o.getAccountName()));

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) threatPage.getTotalElements());
		results.setRecordsFiltered((int) threatPage.getTotalElements());
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(ThreatListMapper.map(threatList, adUserIdMap));

		return results;
	}

	@Override
	public Object updateWatchListDetails(AddToWatchListRequest addToWatchListRequest) {

		AdrEntityScore adrEntityScore = adrEntityScoreRepository.findById(addToWatchListRequest.getRecId())
				.orElseThrow(() -> new EntityNotFoundException("ADR Entity Score Details not Available!"));
		adrEntityScore
				.setWatchlisted(AppConstants.CREATE.equalsIgnoreCase(addToWatchListRequest.getAction()) ? true : false);
		adrEntityScore.setWatchlistedBy(addToWatchListRequest.getUserId());
		adrEntityScore.setWatchlistedDate(LocalDateTime.now());
		adrEntityScore.setUpdatedDate(LocalDateTime.now());
		adrEntityScoreRepository.save(adrEntityScore);

		insertAuditTrailForAdUserDetaisl(addToWatchListRequest.getAction(), adrEntityScore,
				addToWatchListRequest.getUserId());

		return AppConstants.SUCCESS;
	}

	private void insertAuditTrailForAdUserDetaisl(String action, AdrEntityScore adrEntityScore, Long userId) {

		AuditTrailMaster auditTrailMaster = auditTrailMasterRepository
				.findByActionTypeAndModuleName(action, AppConstants.EBA_WATCHLIST, AuditTrailMaster.class)
				.orElseThrow(() -> new EntityNotFoundException("Audit Trail Master Details Not Found"));

		if (Objects.isNull(auditTrailMaster)) {
			return;
		}

		AuditTrailDetailsOnly auditTrailDetailsOnly = auditTrailRepository
				.findTop1ByAuditTrailMasterIdOrderByRecIdDesc(auditTrailMaster.getRecId(), AuditTrailDetailsOnly.class);

		AuditTrail auditTrail =new AuditTrail()
				.setCreatedBy(userId)
				.setCreatedDate(LocalDateTime.now())
				.setModuleRecId(adrEntityScore.getRecId())
				.setAuditTrailMasterId(auditTrailMaster.getRecId())
				.setTenantId(adrEntityScore.getTenantId())
				.setDetails(EBADomain.getAuditTrailJsonResponseByAdrEntities(action, adrEntityScore,
						auditTrailDetailsOnly, userId));
		auditTrailRepository.save(auditTrail);
	}

	@Override
	public Object getWatchListAuditTrail(Long recId) {

		List<AuditTrailMaster> auditTrailMasterList = auditTrailMasterRepository
				.findAllByModuleName(AppConstants.EBA_WATCHLIST);
		
		Map<Long, String> auditTrailasterMap = new HashMap<>();
		List<Long> auditTrailMasterRecIds = new ArrayList<>();
		
		auditTrailMasterList.forEach(o -> {
			auditTrailasterMap.put(o.getRecId(), o.getActionDesc());
			auditTrailMasterRecIds.add(o.getRecId());
		});

		List<AuditTrail> auditTrailList = auditTrailRepository
				.findAllByAuditTrailMasterIdInAndModuleRecIdOrderByCreatedDateDesc(auditTrailMasterRecIds, recId);

		List<User> userList = userRepository.findAll();
		Map<Long, String> uerMap = userList.stream()
				.collect(Collectors.toMap(o -> o.getUserId(), o -> o.getDisplayName()));

		List<WatchListAuditTrailResponse> response = auditTrailList.stream().map(o -> {
			WatchListAuditTrailResponse watchListAuditTrailResponse = new WatchListAuditTrailResponse()
					.setAction(auditTrailasterMap.get(o.getAuditTrailMasterId()));

			AdrEnitityScoreAuditTrail adUserDetailAuditTrail = EBADomain
					.getAdrEnitityScoreAuditTrailFromAuditTrailJson(o.getDetails());

			AuditTrailNewOldValueDto data = adUserDetailAuditTrail.getPerformedBy();

			watchListAuditTrailResponse.setPerformedBy(uerMap.get(Long.valueOf(data.getNewValue())));

			data = adUserDetailAuditTrail.getPerformedOn();
			if (StringUtils.isNotBlank(data.getNewValue()))
				watchListAuditTrailResponse.setPerformedOn(EbaUtil.getLocalDateTimeInMilliSec(
						LocalDateTime.parse(data.getNewValue(), AppConstants.DATE_TIME_FORMATTER_WATCHLIST)));

			return watchListAuditTrailResponse;
		}).collect(Collectors.toList());
		return response;
	}

	@Override
	public Object getEBASettings() {
		List<ProductMaster> productMastersList = productMasterRepository.findAll();
		Map<String, List<ProductMaster>> map = productMastersList.stream()
				.collect(Collectors.groupingBy(a -> a.getProductVendor()));
		Set<String> deviceVendorList = (map != null && map.size() > 0) ? map.keySet() : new HashSet<String>();
		Map<String, Object> pvpnmap = new HashMap<>();
		pvpnmap.put("vendors", deviceVendorList);
		pvpnmap.put("vendors_names", map);
		return new EBASettingsResponse().setDeviceMasterList(pvpnmap);

	}

	@Override
	public Object getAssetsByUserEntityId(AssetsListRequest request) {
		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		Page<AssetMaster> assetsPage = assetMasterRepository.findAllByTenantIdAndIpAddressAndIsDeletedFalse(
				request.getTenantId(), request.getIpAddress(), pageable);
		List<AssetMaster> assetMastersList = assetsPage.getContent();
		List<Long> assetsIdList = assetMastersList.stream().map(AssetMaster::getAssetId).collect(Collectors.toList());

		List<AssetTag> assetTagsList = assetTagRepository.findAllByAssetIdIn(assetsIdList);
		List<Long> tagIdList = new ArrayList<>();
		Map<Long, List<Long>> assetIdAndTagIdMap = new HashMap<>();
		tagIdList = assetTagsList.stream().map(AssetTag::getTagId).collect(Collectors.toList());
		assetIdAndTagIdMap = assetTagsList.stream().collect(Collectors.groupingBy(AssetTag::getAssetId,
				Collectors.mapping(AssetTag::getTagId, Collectors.toList())));

		List<TagValue> tagValuesList = tagValueRepository.findAllByTagIdIn(tagIdList);
		Map<Long, String> tagValuesMap = tagValuesList.stream().collect(Collectors.toMap(TagValue::getTagId,
				o -> o.getTagType() + o.getTagValue(), ((existingValue, newValue) -> existingValue)));

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) assetsPage.getTotalElements());
		results.setRecordsFiltered((int) assetsPage.getTotalElements());
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(AssetListMapper.map(assetMastersList, assetIdAndTagIdMap, tagValuesMap));
		return results;
	}

	@Override
	public Object exportThreatListToCSV(ThreatListRequest request, Integer limit, String path) {
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		
		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);
		
		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		Specification<ThreatCopy> threatSpecification = EBADomain.getEbaThreatListSpecification(request);
		Page<ThreatCopy> threatPage = threatCopyRepository.findAll(threatSpecification, pageable);
		List<ThreatCopy> threatList = threatPage.getContent();
		List<AdUserDetail> adUserDetailsList = adUserDetailsRepository.findAll();
		Map<Long, String> adUserIdMap = adUserDetailsList.stream()
				.collect(Collectors.toMap(o -> o.getRecId(), o -> o.getAccountName()));

		boolean moreThanOnePartsAvailable = Integer.compare((int)threatPage.getTotalElements(), limit) > 0;
		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(AppConstants.FILENAME_SEPARATOR_START,
				AppConstants.FILENAME, currentDateTime, moreThanOnePartsAvailable, part, itIsLastPart,
				AppConstants.FILENAME_EXTENSION_CSV);

		try {
			path=ExcelUtils.downloadCSVToPath(ThreatListMapper.export(threatList, adUserIdMap), path, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			// first time only data is less than export limit
			if (((int)threatPage.getTotalElements() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart(((int)threatPage.getTotalElements() - offset) <= limit);
		}

		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable);
	}
	
	@Override
	public Object exportThreatList(ThreatListRequest request, Integer limit, String path) {
		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();
		
		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);
		
		DatatablePage threatPage=	 (DatatablePage) getThreatList(request);
		List<Object> datList=threatPage.getData();
		
		boolean moreThanOnePartsAvailable = Integer.compare((int)threatPage.getRecordsTotal(), limit) > 0;
		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(AppConstants.FILENAME_SEPARATOR_START,
				AppConstants.FILENAME, currentDateTime, moreThanOnePartsAvailable, part, itIsLastPart,
				AppConstants.FILENAME_EXTENSION_XLSX);
		
		try {
			path=ExcelUtils.downloadToPath(new ThreatExportExcel(datList), path, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			// first time only data is less than export limit
			if (((int)threatPage.getRecordsTotal() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart(((int)threatPage.getRecordsTotal() - offset) <= limit);
		}
		
		return Map.of(AppConstants.EXPORT_FILE_PATH, path, AppConstants.PART_FILE_INFO, returnDataTable);
	}

	@Override
	public Object getRiskScoreDonutList(Long tenantId) {

		List<EntityScoreDto> adrEntityScoresList = adrEntityScoreRepository.findAllByTenantId(tenantId);
		return RiskScoreDonutMapper.map(adrEntityScoresList);
	}

	@Override
	public Object getWatchlistedEntities(Long tenantId) {
		List<EntityScoreDto> entityScoreDtosList = adrEntityScoreRepository
				.findTop10ByWatchlistedTrueAndTenantIdOrderByEntityScoreDesc(tenantId);

		return EntityScoreMapper.mapEntityScore(entityScoreDtosList);
	}

	@Override
	public Object getRiskEntitiesList(Long tenantId) {
		List<EntityScoreDto> entityScoreDtosList = adrEntityScoreRepository
				.findTop10ByTenantIdAndEntityScoreDiffGreaterThanOrderByEntityScoreDiffDesc(tenantId, 0f);

		return EntityScoreMapper.mapEntityScoreDiff(entityScoreDtosList);
	}

	@Override
	public Object getTicketList(TicketListRequest ticketListRequest) {

		List<SysParameterType> sysParameterTypesList = sysParamTypeRepository
				.findAllByParamTypeIn(
						List.of(AppConstants.SYS_PARAM_TICKET_PRIORITY, AppConstants.SYS_PARAM_TICKET_CATEGORY))
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY + StringUtils.SPACE
						+ AppConstants.SYS_PARAM_TICKET_CATEGORY));
		Map<String, Long> sysParamTypeMap = sysParameterTypesList.stream()
				.collect(Collectors.toMap(SysParameterType::getParamType, SysParameterType::getParaTypeId));

		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findAllByParamTypeIdIn(List.of(sysParamTypeMap.get(AppConstants.SYS_PARAM_TICKET_PRIORITY),
						sysParamTypeMap.get(AppConstants.SYS_PARAM_TICKET_CATEGORY)))
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY + StringUtils.SPACE
						+ AppConstants.SYS_PARAM_TICKET_CATEGORY));

		Map<Long, Map<Long, String>> sysParamValueMap = sysParameterValuesList.stream()
				.collect(Collectors.groupingBy(SysParameterValue::getParamTypeId,
						Collectors.toMap(SysParameterValue::getParamValueId, SysParameterValue::getParamValue)));

		Map<Long, String> priorityMap = sysParamValueMap
				.getOrDefault(sysParamTypeMap.get(AppConstants.SYS_PARAM_TICKET_PRIORITY), Collections.emptyMap());
		Map<Long, String> categoryMap = sysParamValueMap
				.getOrDefault(sysParamTypeMap.get(AppConstants.SYS_PARAM_TICKET_CATEGORY), Collections.emptyMap());

		List<User> userList = userRepository.findAll();
		Map<Long, String> userMap = userList.stream().collect(Collectors.toMap(o -> o.getUserId(), o -> EbaUtil
				.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));

		LocalDateTime toDate = LocalDateTime.now();
		LocalDateTime fromDate = toDate.minusHours(24);
		List<CtmtAffectedEntitiesOthers> ctmtAffectedEntitiesUsersList = ctmtAffectedEntitiesOthersRepository
				.findAllByCreatedDateBetweenAndTenantId(fromDate, toDate, ticketListRequest.getTenantId());

		if (!ctmtAffectedEntitiesUsersList.isEmpty()) {
			Map<Long, List<CtmtAffectedEntitiesOthers>> ctmtTicketEntitiesMap = ctmtAffectedEntitiesUsersList.stream()
					.collect(Collectors.groupingBy(CtmtAffectedEntitiesOthers::getTicketId));

			List<Ticket> ticketsList = ticketRepository.findTop10ByTicketIdInAndUpdatedDateBetweenAndStatus(
					ctmtTicketEntitiesMap.keySet(), fromDate, toDate, AppConstants.SYS_PARAM_OPEN_TICKET_STATUS);

			return TicketListMapper.map(ticketsList, priorityMap, categoryMap, ctmtTicketEntitiesMap, userMap);

		}

		return CollectionUtils.EMPTY_COLLECTION;
	}

	@Override
	public Object getMappedAssetExport(AssetsListRequest request, Integer limit, String path) {

		DateFormat dateFormatter = new SimpleDateFormat(AppConstants.EXPORT_FILENAME_DATETIMEMS_FORMAT_STR);
		String currentDateTime = dateFormatter.format(new Date());
		// when Date selected as local time format in UI
		if (StringUtils.isNotBlank(request.getDateString())) {
			currentDateTime = request.getDateString();
		}
		Integer part = request.getPart();
		boolean itIsLastPart = request.isLastPart();

		Integer offset = (part - 1) * limit;
		request.setStart(offset);
		request.setLength(limit);

		DatatablePage assetDatatablePage = (DatatablePage) getAssetsByUserEntityId(request);
		List<Object> datList = assetDatatablePage.getData();

		boolean moreThanOnePartsAvailable = Integer.compare((int) assetDatatablePage.getRecordsTotal(), limit) > 0;
		String fileName = ExcelUtils.getFileNameWithoutPrefixAndSuffix(AppConstants.FILENAME,
				AppConstants.MAPPED_ASSETS_FILENAME, currentDateTime, moreThanOnePartsAvailable, part, itIsLastPart,
				AppConstants.FILENAME_EXTENSION_XLSX);

		try {
			path = ExcelUtils.downloadToPath(new MappedAssetExportExcel(datList), path, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataTableRequest returnDataTable = new DataTableRequest();
		if (!request.isLastPart()) {
			returnDataTable.setPart(request.getPart() + 1);
			// first time only data is less than export limit
			if (((int) assetDatatablePage.getRecordsTotal() - offset) <= limit) {
				returnDataTable.setPart(null);
			}
			offset = (part) * limit;
			returnDataTable.setLastPart(((int) assetDatatablePage.getRecordsTotal() - offset) <= limit);
		}

		return Map.of(AppConstants.EXPORT_FILE_NAME, fileName, AppConstants.EXPORT_FILE_PATH, path,
				AppConstants.PART_FILE_INFO, returnDataTable);

	}
	
	
}
