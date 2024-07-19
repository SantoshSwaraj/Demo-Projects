package aisaac.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisaac.domain.UbaDomain;
import aisaac.domain.datatable.DataTableRequest;
import aisaac.domain.datatable.DatatablePage;
import aisaac.dto.AdUserDetailAuditTrail;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AuditTrailNewOldValueDto;
import aisaac.entities.AdUserDetail;
import aisaac.entities.AuditTrail;
import aisaac.entities.AuditTrailMaster;
import aisaac.entities.CtmtAffectedEntitiesUser;
import aisaac.entities.SysParameterType;
import aisaac.entities.SysParameterValue;
import aisaac.entities.Threat;
import aisaac.entities.Ticket;
import aisaac.entities.UbaPgOneUserTile;
import aisaac.entities.UbaPgOneUserTileEntity;
import aisaac.entities.User;
import aisaac.exception.ResourceNotFoundException;
import aisaac.payload.mapper.RiskScoreDonutMapper;
import aisaac.payload.mapper.ThreatListMapper;
import aisaac.payload.mapper.TicketListMapper;
import aisaac.payload.mapper.UserScoreMapper;
import aisaac.payload.mapper.UserTileListMapper;
import aisaac.payload.request.AddToWatchListRequest;
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.TicketListRequest;
import aisaac.payload.request.UserTileListRequest;
import aisaac.payload.response.WatchListAuditTrailResponse;
import aisaac.repository.AdUserDetailsRepository;
import aisaac.repository.AuditTrailMasterRepository;
import aisaac.repository.AuditTrailRepository;
import aisaac.repository.CtmtAffectedEntitiesUserRepository;
import aisaac.repository.SysParamTypeRepository;
import aisaac.repository.SysParameterValueRepository;
import aisaac.repository.ThreatRepository;
import aisaac.repository.TicketRepository;
import aisaac.repository.UbaPgOneUserTileEntityRepository;
import aisaac.repository.UbaPgOneUserTileRepository;
import aisaac.repository.UserRepository;
import aisaac.util.AppConstants;
import aisaac.util.ExcelUtils;
import aisaac.util.ThreatExportExcel;
import aisaac.util.UbaUtil;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class UbaSeriveImpl implements UbaService {

	@Autowired
	private AdUserDetailsRepository adUserDetailsRepository;

	@Autowired
	private ThreatRepository threatRepository;

	@Autowired
	private UbaPgOneUserTileRepository ubaPgOneUserTileRepository;

	@Autowired
	private AuditTrailMasterRepository auditTrailMasterRepository;

	@Autowired
	private AuditTrailRepository auditTrailRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UbaPgOneUserTileEntityRepository ubaPgOneUserTileEntityRepository;
	
	@Autowired
	private CtmtAffectedEntitiesUserRepository ctmtAffectedEntitiesUserRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private SysParamTypeRepository sysParamTypeRepository;
	
	@Autowired
	private SysParameterValueRepository sysParameterValueRepository;

	@Override
	public Object getAdUserDetailDepartmentList(Long tenantId) {
		List<AdUserDetail> adUserDetails = adUserDetailsRepository
				.findAllByTenantIdAndDepartmentNameIsNotNull(tenantId);
		return adUserDetails.stream().map(o -> o.getDepartmentName()).distinct().collect(Collectors.toList());
	}

	@Override
	public Object getThreatList(ThreatListRequest request) {
		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		Specification<Threat> threatSpecification = UbaDomain.getUbaThreatListSpecification(request);
		Page<Threat> threatPage = threatRepository.findAll(threatSpecification, pageable);
		List<Threat> threatList = threatPage.getContent();

		AdUserDetail adUserDetails = adUserDetailsRepository
				.findByRecIdAndTenantId(request.getAdUserId().intValue(), request.getTenantId()).orElse(null);

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) threatPage.getTotalElements());
		results.setRecordsFiltered((int) threatPage.getTotalElements());
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(ThreatListMapper.map(threatList,
				Objects.nonNull(adUserDetails) ? adUserDetails.getAccountName() : StringUtils.EMPTY));

		return results;
	}

	@Override
	public Object addOrDeleteWatchListDetails(AddToWatchListRequest addToWatchListRequest) {
		// process watch list information in ad_user_detail table
		AdUserDetail adUserDetail = adUserDetailsRepository
				.findByRecIdAndTenantId(addToWatchListRequest.getAdUserId(), addToWatchListRequest.getTenantId())
				.orElseThrow(() -> new EntityNotFoundException("No data Available for AD User"));
		adUserDetail
				.setWatchlisted(AppConstants.CREATE.equalsIgnoreCase(addToWatchListRequest.getAction()) ? true : false);
		adUserDetail.setWatchlistedBy(addToWatchListRequest.getUserId().intValue());
		adUserDetail.setWatchlistedDate(LocalDateTime.now());
		adUserDetail.setUpdatedDate(LocalDateTime.now());
		adUserDetailsRepository.save(adUserDetail);

		// process watch list information in uba_pg1_user_tile table
		ubaPgOneUserTileRepository.updateWatchListDataInUbaPgOneUserTileTableByTenantIdAndAdUserId(
				addToWatchListRequest.getTenantId(), addToWatchListRequest.getAdUserId(),
				AppConstants.CREATE.equalsIgnoreCase(addToWatchListRequest.getAction()) ? true : false,
				addToWatchListRequest.getUserId());

		// process watch list information in audit_trail table
		insertAuditTrailForAdUserDetaisl(addToWatchListRequest.getAction(), adUserDetail,
				addToWatchListRequest.getUserId());

		return AppConstants.SUCCESS;
	}

	private void insertAuditTrailForAdUserDetaisl(String action, AdUserDetail adUserDetail, Long userId) {

		AuditTrailMaster auditTrailMaster = auditTrailMasterRepository
				.findByActionTypeAndModuleName(action, AppConstants.UBA_WATCHLIST, AuditTrailMaster.class)
				.orElseThrow(() -> new EntityNotFoundException("Audit Trail Master Details Not Found"));

		if (Objects.isNull(auditTrailMaster)) {
			return;
		}

		AuditTrailDetailsOnly auditTrailDetailsOnly = auditTrailRepository
				.findTop1ByAuditTrailMasterIdOrderByRecIdDesc(auditTrailMaster.getRecId(), AuditTrailDetailsOnly.class);

		AuditTrail auditTrail = new AuditTrail().setCreatedBy(userId).setCreatedDate(LocalDateTime.now())
				.setModuleRecId(adUserDetail.getRecId()).setAuditTrailMasterId(auditTrailMaster.getRecId())
				.setTenantId(adUserDetail.getTenantId()).setDetails(UbaDomain
						.getAuditTrailJsonResponseByAdUserDetail(action, adUserDetail, auditTrailDetailsOnly, userId));
		auditTrailRepository.save(auditTrail);
	}

	@Override
	public Object getWatchListAuditTrail(Long adUserId) {

		List<AuditTrailMaster> auditTrailMasterList = auditTrailMasterRepository
				.findAllByModuleName(AppConstants.UBA_WATCHLIST);

		Map<Long, String> auditTrailasterMap = new HashMap<>();
		List<Long> auditTrailMasterRecIds = new ArrayList<>();

		auditTrailMasterList.forEach(o -> {
			auditTrailasterMap.put(o.getRecId(), o.getActionDesc());
			auditTrailMasterRecIds.add(o.getRecId());
		});

		List<AuditTrail> auditTrailList = auditTrailRepository
				.findAllByAuditTrailMasterIdInAndModuleRecIdOrderByCreatedDateDesc(auditTrailMasterRecIds, adUserId);

		List<User> userList = userRepository.findAll();
		Map<Long, String> uerMap = userList.stream()
				.collect(Collectors.toMap(o -> o.getUserId(), o -> o.getDisplayName()));

		List<WatchListAuditTrailResponse> response = auditTrailList.stream().map(o -> {
			WatchListAuditTrailResponse watchListAuditTrailResponse = new WatchListAuditTrailResponse()
					.setAction(auditTrailasterMap.get(o.getAuditTrailMasterId()));

			AdUserDetailAuditTrail adUserDetailAuditTrail = UbaDomain
					.getAdUserDetailAuditTrailFromAuditTrailJson(o.getDetails());

			AuditTrailNewOldValueDto data = adUserDetailAuditTrail.getPerformedBy();

			watchListAuditTrailResponse.setPerformedBy(uerMap.get(Long.valueOf(data.getNewValue())));

			data = adUserDetailAuditTrail.getPerformedOn();
			if (StringUtils.isNotBlank(data.getNewValue()))
				watchListAuditTrailResponse.setPerformedOn(UbaUtil.getLocalDateTimeInMilliSec(
						LocalDateTime.parse(data.getNewValue(), AppConstants.DATE_TIME_FORMATTER_WATCHLIST)));

			return watchListAuditTrailResponse;
		}).collect(Collectors.toList());
		return response;
	}

	@Override
	public Object getUserTileList(UserTileListRequest request) {

		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();

		// create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		LocalDateTime fromDate = LocalDateTime.parse(LocalDateTime.now().minusHours(1).atZone(ZoneId.of("UTC"))
				.toLocalDateTime().format(AppConstants.DATE_TIME_HOUR_FORMATTER),
				AppConstants.DATE_TIME_HOUR_FORMATTER);
		LocalDateTime toDate = LocalDateTime.parse(LocalDateTime.now().atZone(ZoneId.of("UTC")).toLocalDateTime()
				.format(AppConstants.DATE_TIME_HOUR_FORMATTER), AppConstants.DATE_TIME_HOUR_FORMATTER);

		long count = ubaPgOneUserTileRepository.countByTenantIdAndCreatedDateGreaterThanAndCreatedDateLessThanEqual(
				request.getTenantId(), fromDate, toDate);
		if (count < 1 && "1".equalsIgnoreCase(request.getDateType())) {
			request.setDateType("2");// setting 48 hours
		}
		Specification<UbaPgOneUserTile> specification = UbaDomain.getUbaUserTileFilter(request);
		Page<UbaPgOneUserTile> page = ubaPgOneUserTileRepository.findAll(specification, pageable);
		int totalCount = ubaPgOneUserTileRepository.findAll(specification).size();

		List<UbaPgOneUserTile> ubaPgOneUserTilesList = page.getContent();
		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal(totalCount);
		results.setRecordsFiltered(totalCount);
//		results.setRecordsTotal((page.getTotalPages()-1)*length);
//		results.setRecordsFiltered((page.getTotalPages()-1)*length);
//		results.setRecordsTotal((int) page.getTotalElements());
//		results.setRecordsFiltered((int) page.getTotalElements());
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(UserTileListMapper.map(ubaPgOneUserTilesList));

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
		Specification<Threat> threatSpecification = UbaDomain.getUbaThreatListSpecification(request);
		Page<Threat> threatPage = threatRepository.findAll(threatSpecification, pageable);
		List<Threat> threatList = threatPage.getContent();

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

	@SuppressWarnings("unchecked")
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
	public Object getRiskScoreDonutDetails(Long tenantId) {

		Specification<UbaPgOneUserTileEntity> specification = UbaDomain.getRiskScoreDonutFilter(tenantId);
		List<UbaPgOneUserTileEntity> ubaPgOneUserTileEntitiesList = ubaPgOneUserTileEntityRepository
				.findAll(specification);

		return RiskScoreDonutMapper.map(ubaPgOneUserTileEntitiesList);
	}

	@Override
	public Object getWatchlistedUsers(Long tenantId) {
		Pageable pageable = PageRequest.of(0, 10);
		Specification<UbaPgOneUserTileEntity> specification = UbaDomain.getWatchListScoreFilter(tenantId);
		List<UbaPgOneUserTileEntity> ubaPgOneUserTileEntitiesList = ubaPgOneUserTileEntityRepository
				.findAll(specification,pageable).getContent();
		return UserScoreMapper.mapUserScore(ubaPgOneUserTileEntitiesList);
	}

	@Override
	public Object getRiskUsersList(Long tenantId) {
		Pageable pageable = PageRequest.of(0, 10);
		Specification<UbaPgOneUserTileEntity> specification = UbaDomain.getRiskUsersFilter(tenantId);
		List<UbaPgOneUserTileEntity> ubaPgOneUserTileEntitiesList = ubaPgOneUserTileEntityRepository
				.findAll(specification,pageable).getContent();
		return UserScoreMapper.mapUserScoreDiff(ubaPgOneUserTileEntitiesList);
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
		Map<Long, String> userMap = userList.stream().collect(Collectors.toMap(o -> o.getUserId(), o -> UbaUtil
				.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));

		LocalDateTime toDate = LocalDateTime.now();
		LocalDateTime fromDate = toDate.minusHours(24);
		List<CtmtAffectedEntitiesUser> ctmtAffectedEntitiesUsersList = ctmtAffectedEntitiesUserRepository
				.findAllByUpdatedDateBetweenAndTenantId(fromDate, toDate, ticketListRequest.getTenantId());

		if (!ctmtAffectedEntitiesUsersList.isEmpty()) {
			Map<Long, List<String>> ctmtTicketUsersMap = ctmtAffectedEntitiesUsersList.stream()
					.collect(Collectors.groupingBy(CtmtAffectedEntitiesUser::getTicketId, Collectors.mapping(
							o -> UbaUtil.getUserNameWithType(o.getUsername(), o.getUserType()), Collectors.toList())));

			List<Ticket> ticketsList = ticketRepository.findTop10ByTicketIdInAndUpdatedDateBetweenAndStatus(
					ctmtTicketUsersMap.keySet(), fromDate, toDate, AppConstants.SYS_PARAM_OPEN_TICKET_STATUS);

			return TicketListMapper.map(ticketsList, priorityMap, categoryMap, ctmtTicketUsersMap, userMap);

		}

		return CollectionUtils.EMPTY_COLLECTION;
	}
	

}
 
