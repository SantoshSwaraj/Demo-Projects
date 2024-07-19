package aisaac.service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import aisaac.domain.DashboardDomain;
import aisaac.domain.datatable.DatatablePage;
import aisaac.dto.AssetIdDto;
import aisaac.dto.CountryCodeAndLevelDto;
import aisaac.dto.DashBoardCountDto;
import aisaac.dto.LogStopageMasterAssetDto;
import aisaac.dto.SpeedOfResponseForValidationTrendDataDto;
import aisaac.dto.ThreatCountDto;
import aisaac.entities.AgeOfTicketModel;
import aisaac.entities.AssetMaster;
import aisaac.entities.AvgResponseTimeForTicketCategoryModel;
import aisaac.entities.AvgResponseTimeModel;
import aisaac.entities.GeoCountryMaster;
import aisaac.entities.GlobalSettings;
import aisaac.entities.HourlyThreatCount;
import aisaac.entities.InvestigationSave;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.ProductMaster;
import aisaac.entities.SysParameterType;
import aisaac.entities.SysParameterValue;
import aisaac.entities.Threat;
import aisaac.entities.ThreatLevelStatsCountrySrcipHour;
import aisaac.entities.Ticket;
import aisaac.entities.TicketAssetMapping;
import aisaac.entities.TicketThreatDetails;
import aisaac.entities.TicketWithOutFormula;
import aisaac.entities.User;
import aisaac.exception.EntityNotFoundException;
import aisaac.exception.ResourceNotFoundException;
import aisaac.payload.mapper.AgeOfTicketsMapping;
import aisaac.payload.mapper.AssetByLogFlowStatusMapper;
import aisaac.payload.mapper.AssetByProductNamesAPIMapper;
import aisaac.payload.mapper.AssetByTicketPriorityLevelMapper;
import aisaac.payload.mapper.AssetByTicketPriorityMapping;
import aisaac.payload.mapper.AssetCriticalityMapper;
import aisaac.payload.mapper.AverageResponseTimeForTicketCategoryMapper;
import aisaac.payload.mapper.AvgResponseTimeMapper;
import aisaac.payload.mapper.ClosedTicketsMapper;
import aisaac.payload.mapper.CountryMapAlertDetailsMapper;
import aisaac.payload.mapper.ThreatByProductNamesMapper;
import aisaac.payload.mapper.ThreatLevelFromAttackCountriesMapper;
import aisaac.payload.mapper.ThreatListMapper;
import aisaac.payload.mapper.TicketCountAndPriorityMapping;
import aisaac.payload.mapper.TicketsCategoryMapping;
import aisaac.payload.mapper.TicketsListMapping;
import aisaac.payload.request.CountryMapAlertRequest;
import aisaac.payload.request.DashboardCountRequest;
import aisaac.payload.response.DashboardXcountResponse;
import aisaac.repository.AgeOfTicketModelRepository;
import aisaac.repository.AssetMasterRepository;
import aisaac.repository.AvgResponseTimeForTicketCategoryModelRepository;
import aisaac.repository.AvgResponseTimeModelRepository;
import aisaac.repository.GeoCountryMasterRepository;
import aisaac.repository.GlobalSettingsRepository;
import aisaac.repository.HourlyThreatCountRepository;
import aisaac.repository.InvestigationSaveRepository;
import aisaac.repository.LogStopageMasterRepository;
import aisaac.repository.ProductMasterRepository;
import aisaac.repository.SysParamTypeRepository;
import aisaac.repository.SysParameterValueRepository;
import aisaac.repository.ThreatLevelStatsCountryDayRepository;
import aisaac.repository.ThreatLevelStatsCountrySrcipHourRepository;
import aisaac.repository.ThreatRepository;
import aisaac.repository.ThreatResponsespeedHourRepository;
import aisaac.repository.TicketAssetMappingRepository;
import aisaac.repository.TicketRepository;
import aisaac.repository.TicketThreatDetailsRepository;
import aisaac.repository.TicketWithOutFormulaRepository;
import aisaac.repository.UserRepository;
import aisaac.util.AppConstants;
import aisaac.util.DashboardUtils;
import aisaac.util.ResponseOrMessageConstants;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private AssetMasterRepository assetMasterRepository;

	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private ProductMasterRepository productMasterRepository;

	@Autowired
	private SysParamTypeRepository sysParamTypeRepository;
	
	@Autowired
	private SysParameterValueRepository sysParameterValueRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ThreatRepository threatRepository;
	
	@Autowired
	private TicketThreatDetailsRepository ticketThreatDetailsRepository;
	
	@Autowired
	private GlobalSettingsRepository globalSettingsRepository;
	
	@Autowired
	private HourlyThreatCountRepository hourlyThreatCountRepository;
	
	@Autowired
	private TicketAssetMappingRepository ticketAssetMappingRepository;
	
	@Autowired
	private GeoCountryMasterRepository geoCountryMasterRepository;
	
	@Autowired
	private ThreatLevelStatsCountryDayRepository threatLevelStatsCountryDayRepository;
	
	@Autowired
	private ThreatLevelStatsCountrySrcipHourRepository threatLevelStatsCountrySrcipHourRepository;
	
	@Autowired
	private LogStopageMasterRepository logStopageMasterRepository;
	
	@Autowired
	private ThreatResponsespeedHourRepository threatResponsespeedHourRepository;
	
	@Autowired
	private InvestigationSaveRepository investigationSaveRepository;
	
	@Autowired
	private TicketWithOutFormulaRepository ticketWithOutFormulaRepository;
	
	@Autowired
	private AgeOfTicketModelRepository ageOfTicketModelRepository;
	
	@Autowired
	private AvgResponseTimeModelRepository avgResponseTimeModelRepository;
	
	@Autowired
	private AvgResponseTimeForTicketCategoryModelRepository avgResponseTimeForTicketCategoryModelRepository;
	
	@Override
	public Long getAssetCountByTenantId(Long tenantId) {
		List<LogStopageMasterAssetDto> logStopageMastersList = logStopageMasterRepository
				.findAllByTenantIdAndDeletedFalseAndDisabledFalseAndSuppressedFalse(tenantId);
		return DashboardUtils.getAssetCountFromLogStopageMaster(logStopageMastersList);

	}

	@Override
	public Object getTicketsDetails(DashboardCountRequest countRequest) {
		Specification<Ticket> specification = DashboardDomain.getTicketCountFilterSpecification(countRequest);
		Long count = ticketRepository.count(specification);
		specification = DashboardDomain.getTicketDeltaCountFilterSpecification(countRequest);
		Long deltaCount = ticketRepository.count(specification);
		Long difference=count-deltaCount;
		
		return new DashboardXcountResponse()
				.setCount(count)
				.setDeltaCount(difference)
				.setCountStr(DashboardUtils.calculateCountIntoTextFormat(count))
				.setDeltaCountStr(DashboardUtils.calculateCountIntoTextFormat(difference))
				.setColor(difference<0?AppConstants.COLOR_GREEN_STR:AppConstants.COLOR_RED_STR);
	}

	@Override
	public Object getAssetByProductNamesAPI(Long tenantId) {
		Specification<LogStopageMaster> specification = DashboardDomain.getAssetsByProductNamesFilter(tenantId);
		List<LogStopageMaster> data = logStopageMasterRepository.findAll(specification);
		Long specificationCount = (long) data.size();

		data = data.stream().limit(30).collect(Collectors.toList());

		List<ProductMaster> productMastersList = productMasterRepository.findAllByDeletedFalse();

		List<LogStopageMasterAssetDto> logStopageMastersList = logStopageMasterRepository
				.findAllByTenantIdAndDeletedFalseAndDisabledFalseAndSuppressedFalse(tenantId);
		Long totalAssetCount = DashboardUtils.getAssetCountFromLogStopageMaster(logStopageMastersList);
		
		return AssetByProductNamesAPIMapper.map(data, DashboardUtils.getProductMasterMap(productMastersList),
				totalAssetCount, specificationCount);
	}

	@Override
	public Object getOpenTicketsCountDetails(DashboardCountRequest request) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValueMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		Specification<TicketWithOutFormula> tiSpecification = DashboardDomain
				.getOpenAndReopenedTicketCountFilterSpecification(request, false);
		List<TicketWithOutFormula> ticketlist = ticketWithOutFormulaRepository.findAll(tiSpecification);

		Long totalOpenTicketCount = ticketWithOutFormulaRepository.count(tiSpecification);

		return TicketCountAndPriorityMapping.map(ticketlist, sysParamValueMap, totalOpenTicketCount);
	}

	@Override
	public Object getReOpenedTicketsCountDetails(DashboardCountRequest request) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValueMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		Specification<TicketWithOutFormula> tiSpecification = DashboardDomain
				.getOpenAndReopenedTicketCountFilterSpecification(request, true);
		List<TicketWithOutFormula> ticketlist = ticketWithOutFormulaRepository.findAll(tiSpecification);

		Long totalOpenTicketCount = ticketWithOutFormulaRepository.count(tiSpecification);

		return TicketCountAndPriorityMapping.map(ticketlist, sysParamValueMap, totalOpenTicketCount);
	}

	@Override
	public Object getTicketsByCategoryAPI(DashboardCountRequest request) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValuePriorityMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		sysParameterType = sysParamTypeRepository.findByParamType(AppConstants.SYS_PARAM_TICKET_CATEGORY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_CATEGORY));
		sysParameterValuesList = sysParameterValueRepository.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_CATEGORY));

		Map<Long, String> sysParamValueCategoryMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));
		Specification<TicketWithOutFormula> tiSpecification = DashboardDomain
				.getTicketByCategoryFilterSpecification(request);
		List<TicketWithOutFormula> ticketlist = ticketWithOutFormulaRepository.findAll(tiSpecification);
		return TicketsCategoryMapping.map(ticketlist, sysParamValuePriorityMap, sysParamValueCategoryMap);
	}

	@Override
	public Object getTicketsList(DashboardCountRequest request) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValuePriorityMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		sysParameterType = sysParamTypeRepository.findByParamType(AppConstants.SYS_PARAM_TICKET_CATEGORY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_CATEGORY));
		sysParameterValuesList = sysParameterValueRepository.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_CATEGORY));

		Map<Long, String> sysParamValueCategoryMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));
		List<User> users = userRepository.findAll();
		Map<Long, String> createdByAndNameMap = users.stream()
				.collect(Collectors.toMap(o -> o.getUserId(), o -> DashboardUtils.getDisplayUsername(o.getDisplayName(),
						o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));

		DashBoardCountDto dashBoardCountDto = DashboardDomain.getTicketListRequestDto(request);
//		List<TicketListDto> ticketlist = ticketRepository
//				.findFirst10ByCreatedDateBetweenOrReOpenedDateBetweenAndTenantIdAndStatusAndPriorityNotOrderByCreatedDateDescReOpenedDateDescPriorityAsc(
//						dashBoardCountDto.getFromDate(), dashBoardCountDto.getToDate(), dashBoardCountDto.getFromDate(),
//						dashBoardCountDto.getToDate(), request.getTenantId(), AppConstants.TICKET_STATUS_70,
//						AppConstants.TICKET_PRIORITY_69);
		Specification<Ticket> tiSpecification = DashboardDomain.getTicketListSpecification(request);
		List<Ticket> ticketlist = ticketRepository.findAll(tiSpecification, PageRequest.of(0, 10)).getContent();
		return TicketsListMapping.map(ticketlist, sysParamValuePriorityMap, sysParamValueCategoryMap,
				createdByAndNameMap);
	}

	@Override
	public Object getThreatAlertDetails(BigInteger threatId) {
		Threat threat = threatRepository.findById(threatId)
				.orElseThrow(() -> new EntityNotFoundException(ResponseOrMessageConstants.THREAT_ENTITY_NOT_FOUND_MSG));
		return ThreatListMapper.map(threat);
	}

	@Override
	public Object getThreatAlertListByTicketId(Long ticketId) {
		List<TicketThreatDetails> ticketThreatDetailsList = ticketThreatDetailsRepository.findAllByTicketId(ticketId);
		return ticketThreatDetailsList.stream().map(TicketThreatDetails::getThreatId).distinct()
				.collect(Collectors.toList());
	}

	@Override
	public Object getAssetByCriticalityDetails(Long tenantId) {
		List<LogStopageMasterAssetDto> logStopageMasterAssetDtosList = logStopageMasterRepository
				.findAllByTenantIdAndDeletedFalseAndDisabledFalseAndSuppressedFalse(tenantId);
		List<Long> assetIdList = logStopageMasterAssetDtosList.stream().filter(o -> o.getAssetId() != null)
				.map(LogStopageMasterAssetDto::getAssetId).distinct().collect(Collectors.toList());

		Specification<AssetMaster> specification = DashboardDomain.getAssetsByCriticalityFilter(tenantId, assetIdList);
		List<AssetMaster> assetMastersList = assetMasterRepository.findAll(specification);
		return AssetCriticalityMapper.map(assetMastersList, Long.valueOf(logStopageMasterAssetDtosList.size()));
	}

	@Override
	public Object getAgeOfTicketsDetails(Long tenantId) {

		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValueMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		GlobalSettings globalSettings = globalSettingsRepository
				.findByParamTypeAndParamName(AppConstants.TICKET_MAX_SEARCH_DAYS_SYS_PARAM_TYPE,
						AppConstants.TICKET_MAX_SEARCH_DAYS_SYS_PARAM_NAME)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.TICKET_MAX_SEARCH_DAYS_SYS_PARAM_NAME));
		Long maxSearchDays = Objects.nonNull(globalSettings.getParamValue())
				? Long.parseLong(globalSettings.getParamValue())
				: 365L;
		Specification<AgeOfTicketModel> specification = DashboardDomain.getAgeOfTicketsFilter(tenantId, maxSearchDays);
		List<AgeOfTicketModel> ticketList = ageOfTicketModelRepository.findAll(specification);

		return AgeOfTicketsMapping.map(ticketList, sysParamValueMap);
	}

	@Override
	public Object getClosedTicketsCountDetails(DashboardCountRequest dashboardCountRequest) {
		Specification<TicketWithOutFormula> specification = DashboardDomain
				.getClosedTicketsFilterSpecification(dashboardCountRequest);
		List<TicketWithOutFormula> ticketsList = ticketWithOutFormulaRepository.findAll(specification);

		return ClosedTicketsMapper.map(ticketsList);
	}

	@Override
	public Object getThreatCountDetails(DashboardCountRequest dashboardCountRequest) {
		DashBoardCountDto dashBoardCountDto = DashboardDomain.getThreatCountFilterDto(dashboardCountRequest);
		ThreatCountDto threatCountDto = hourlyThreatCountRepository.findByIntervalDateBetweenAndTenantId(
				dashBoardCountDto.getFromDate(), dashBoardCountDto.getToDate(), dashboardCountRequest.getTenantId());

		Long count = threatCountDto.getSumCount();
		Long deltaCount = 0L;
		boolean dateValidationFlag=false;
		
		// validation as per specs
		if (Objects.nonNull(dashboardCountRequest.getFromDate()) && Objects.nonNull(dashboardCountRequest.getToDate())
				&& "custom".equalsIgnoreCase(dashboardCountRequest.getDateType())) {
			Long days = DashboardUtils.calculateDaysGapByLocalDateTime(dashboardCountRequest.getFromDate(),
					LocalDateTime.now());
			if (days <= 7) {
				dashBoardCountDto = DashboardDomain.getThreatDeltaCountFilterDto(dashboardCountRequest);
				threatCountDto = hourlyThreatCountRepository.findByIntervalDateBetweenAndTenantId(
						dashBoardCountDto.getFromDate(), dashBoardCountDto.getToDate(),
						dashboardCountRequest.getTenantId());
				deltaCount = threatCountDto.getSumCount();
			} else {
				dashboardCountRequest.setDateType("7");
				dashBoardCountDto = DashboardDomain.getThreatDeltaCountFilterDto(dashboardCountRequest);
				threatCountDto = hourlyThreatCountRepository.findByIntervalDateBetweenAndTenantId(
						dashBoardCountDto.getFromDate(), dashBoardCountDto.getToDate(),
						dashboardCountRequest.getTenantId());
				deltaCount = threatCountDto.getSumCount();
			}

		} else if ("1".equalsIgnoreCase(dashboardCountRequest.getDateType())
				|| "7".equalsIgnoreCase(dashboardCountRequest.getDateType())) {
			dashBoardCountDto = DashboardDomain.getThreatDeltaCountFilterDto(dashboardCountRequest);
			threatCountDto = hourlyThreatCountRepository.findByIntervalDateBetweenAndTenantId(
					dashBoardCountDto.getFromDate(), dashBoardCountDto.getToDate(),
					dashboardCountRequest.getTenantId());
			deltaCount = threatCountDto.getSumCount();
		}else {
			dateValidationFlag=true;
		}

		Long difference = count - deltaCount;

		return new DashboardXcountResponse()
				.setCount(count)
				.setDeltaCount(dateValidationFlag ? 0 :difference)
				.setCountStr(DashboardUtils.calculateCountIntoTextFormat(count))
				.setDeltaCountStr(dateValidationFlag ? "0" : DashboardUtils.calculateCountIntoTextFormat(difference))
				.setColor(difference < 0 ? AppConstants.COLOR_GREEN_STR : AppConstants.COLOR_RED_STR);
	}

	@Override
	public Object getThreatCountByProductName(DashboardCountRequest dashboardCountRequest) {
		Specification<HourlyThreatCount> specification = DashboardDomain
				.getThreatCountByProductNameFilterSpecification(dashboardCountRequest);
		Pageable pageable = PageRequest.of(0, 10);

		// validation as per specs
		if (Objects.nonNull(dashboardCountRequest.getFromDate()) && Objects.nonNull(dashboardCountRequest.getToDate())
				&& "custom".equalsIgnoreCase(dashboardCountRequest.getDateType())) {
			Long days = DashboardUtils.calculateDaysGapByLocalDateTime(dashboardCountRequest.getFromDate(),
					LocalDateTime.now());
			if (days <= 7) {
				List<HourlyThreatCount> hourlyThreatCountsList = hourlyThreatCountRepository
						.findAll(specification, pageable).getContent();

				List<ProductMaster> productMastersList = productMasterRepository.findAllByDeletedFalse();

				return ThreatByProductNamesMapper.map(hourlyThreatCountsList,
						DashboardUtils.getProductMasterMap(productMastersList));
			}

		} else  {
			List<HourlyThreatCount> hourlyThreatCountsList = hourlyThreatCountRepository
					.findAll(specification, pageable).getContent();

			List<ProductMaster> productMastersList = productMasterRepository.findAllByDeletedFalse();

			return ThreatByProductNamesMapper.map(hourlyThreatCountsList,
					DashboardUtils.getProductMasterMap(productMastersList));
		}
		return CollectionUtils.EMPTY_COLLECTION;

	}

	@Override
	public Object getAssetByTicketPriorityCount(DashboardCountRequest dashboardCountRequest) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValueMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		List<LogStopageMasterAssetDto> logStopageMasterAssetDtosList = logStopageMasterRepository
				.findAllByTenantIdAndDeletedFalseAndDisabledFalseAndSuppressedFalse(
						dashboardCountRequest.getTenantId());
		List<Long> assetIds = logStopageMasterAssetDtosList.stream().filter(o -> o.getAssetId() != null)
				.map(LogStopageMasterAssetDto::getAssetId).distinct().collect(Collectors.toList());

		Specification<TicketAssetMapping> specification = DashboardDomain
				.getAssetByTicketPriorityFilterSpecification(dashboardCountRequest,assetIds);
		List<TicketAssetMapping> ticketAssetMappingsList = ticketAssetMappingRepository.findAll(specification);

		Long totalAssetCount = DashboardUtils.getAssetCountFromLogStopageMaster(logStopageMasterAssetDtosList);

		return AssetByTicketPriorityMapping.map(ticketAssetMappingsList, sysParamValueMap, totalAssetCount);
	}

	@Override
	public Object getAssetByTicketPriorityLevelCount(DashboardCountRequest dashboardCountRequest) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_PRIORITY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_PRIORITY));

		Map<Long, String> sysParamValueMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		Specification<LogStopageMaster> LogStopageMasterSpecification = DashboardDomain
				.getAssetsByTicketPriorityTotalCountFilter(dashboardCountRequest.getTenantId());
		
		List<LogStopageMaster> logStopageMastersList = logStopageMasterRepository.findAll(LogStopageMasterSpecification);
		List<Long> assetIdList=logStopageMastersList.stream().filter(o->o.getAssetId()!=null).map(LogStopageMaster::getAssetId).distinct().collect(Collectors.toList());
		logStopageMastersList=logStopageMastersList.stream().limit(10).collect(Collectors.toList());

		Specification<TicketAssetMapping> ticketAssetMappingSpecification = DashboardDomain
				.getAssetsByTicketPriorityLevelCountFilter(dashboardCountRequest, assetIdList);

		List<TicketAssetMapping> ticketAssetMappingsList = ticketAssetMappingRepository
				.findAll(ticketAssetMappingSpecification);

		List<ProductMaster> productMastersList = productMasterRepository.findAllByDeletedFalse();
		Map<Long, String> productIdAndNameMap = productMastersList.stream()
				.collect(Collectors.toMap(o -> o.getProductId(), o -> o.getProductVendor() + " " + o.getProductName()));

		return AssetByTicketPriorityLevelMapper.map(ticketAssetMappingsList, logStopageMastersList, sysParamValueMap,
				productIdAndNameMap);
	}

	@Override
	public Object getThreatCountriesData(DashboardCountRequest dashboardCountRequest) {

		LocalDateTime fromDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
		LocalDateTime toDate = LocalDateTime.now().with(LocalTime.MAX);

		List<CountryCodeAndLevelDto> countryCodeAndLevelDtosList = threatLevelStatsCountryDayRepository
				.findAllByCreatedDateBetweenAndTenantId(fromDate, toDate, dashboardCountRequest.getTenantId());
		Map<String, Long> countryCodeAndLevelMap = countryCodeAndLevelDtosList.stream()
				.collect(Collectors.toMap(CountryCodeAndLevelDto::getGeoCountryCode,
						CountryCodeAndLevelDto::getThreatLevelCode, (newValue, existing) -> existing));

		List<GeoCountryMaster> geoCountryMastersList = geoCountryMasterRepository
				.findAllByCountryCodeIn(countryCodeAndLevelMap.keySet());
		Map<String, GeoCountryMaster> countryCodeAndGeoDataMap = geoCountryMastersList.stream()
				.collect(Collectors.toMap(GeoCountryMaster::getCountryCode, o -> o));

		Specification<ThreatLevelStatsCountrySrcipHour> specification = DashboardDomain
				.getThreatCountriesSpecificationFileter(dashboardCountRequest, countryCodeAndGeoDataMap.keySet());
		List<ThreatLevelStatsCountrySrcipHour> threatLevelStatsCountrySrcipHoursList = threatLevelStatsCountrySrcipHourRepository
				.findAll(specification);

		return ThreatLevelFromAttackCountriesMapper.map(threatLevelStatsCountrySrcipHoursList, countryCodeAndGeoDataMap,
				countryCodeAndLevelMap);
	}

	@Override
	public Object getCountryMapDetailsForAlert(CountryMapAlertRequest alertRequest) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hours24Ago = now.minusHours(24);
		Specification<ThreatLevelStatsCountrySrcipHour> specification = DashboardDomain
				.getThreatCountriesAlertSpecificationFileter(alertRequest, hours24Ago, now);

//		int pageNumber = alertRequest.getStart() / alertRequest.getLength(); // Calculate page number
//		int length = alertRequest.getLength();

		// create Pageable instance
//		Pageable pageable = PageRequest.of(pageNumber, length);

		List<ThreatLevelStatsCountrySrcipHour> threatLevelStatsCountrySrcipHoursList = threatLevelStatsCountrySrcipHourRepository
				.findAll(specification);

//		int totalRecords = threatLevelStatsCountrySrcipHourRepository.findAll(specification).size();

		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal(threatLevelStatsCountrySrcipHoursList.size());
		results.setRecordsFiltered(threatLevelStatsCountrySrcipHoursList.size());
//		results.setDraw(alertRequest.getDraw());
//		results.setLength(alertRequest.getLength());
		results.setData(CountryMapAlertDetailsMapper.map(threatLevelStatsCountrySrcipHoursList,
				LocalDateTime.of(hours24Ago.getYear(), hours24Ago.getMonth(), hours24Ago.getDayOfMonth(),
						hours24Ago.getHour(), 0, 0),
				now));

		return results;
	}

	@Override
	public Object getAssetByLogFlowStatusDetails(Long tenantId) {
		// Device Stopped Specification
		Specification<LogStopageMaster> specification = DashboardDomain.getAssetByLogFlowStatusFilterSpecification(
				tenantId, AppConstants.LOG_STOPAGE_MASTER_FIELD_NAME_LOG_STOPPAGE_TIME,
				AppConstants.DEVICE_STATUS_ZERO);
		List<LogStopageMaster> logStopageMastersList = logStopageMasterRepository.findAll(specification);
		Map<String, LogStopageMaster> deviceStoppedMap = logStopageMastersList.stream()
				.collect(Collectors.toMap(o -> DashboardUtils.getAgeGroupByDate(o.getLogStoppageTime()),
						o->o));

		// Device Running Specification
		specification = DashboardDomain.getAssetByLogFlowStatusFilterSpecification(tenantId,
				AppConstants.LOG_STOPAGE_MASTER_FIELD_NAME_LOG_RECIEVE_TIME, AppConstants.DEVICE_STATUS_ONE);
		logStopageMastersList = logStopageMasterRepository.findAll(specification);
		Map<String, LogStopageMaster> deviceRunningMap = logStopageMastersList.stream()
				.collect(Collectors.toMap(o -> DashboardUtils.getAgeGroupByDate(o.getLogReceiveTime()),
						o->o));

		return AssetByLogFlowStatusMapper.map(deviceStoppedMap, deviceRunningMap);
	}

	@Override
	public Object getAverageResponseTimeForTicketCategory(Long tenantId) {
		SysParameterType sysParameterType = sysParamTypeRepository
				.findByParamType(AppConstants.SYS_PARAM_TICKET_CATEGORY)
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_TYPE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_CATEGORY));
		List<SysParameterValue> sysParameterValuesList = sysParameterValueRepository
				.findByParamTypeId(sysParameterType.getParaTypeId())
				.orElseThrow(() -> new ResourceNotFoundException(ResponseOrMessageConstants.SYS_PARAM_VALUE_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TICKET_CATEGORY));

		Map<Long, String> sysParamValueCategoryMap = sysParameterValuesList.stream()
				.collect(Collectors.toMap(o -> o.getParamValueId(), o -> o.getParamValue()));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hours24Ago = now.minusHours(24);
		Specification<AvgResponseTimeForTicketCategoryModel> specification = DashboardDomain
				.getAverageResponseTimeForTicketCategorySpecificationFileter(tenantId, hours24Ago, now);
		List<AvgResponseTimeForTicketCategoryModel> ticketsList = avgResponseTimeForTicketCategoryModelRepository
				.findAll(specification);

		return AverageResponseTimeForTicketCategoryMapper.map(ticketsList, sysParamValueCategoryMap);
	}

	@Override
	public Object getAverageResponseTime(Long tenantId) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime hours24Ago = now.minusHours(24);
		hours24Ago = LocalDateTime.of(hours24Ago.getYear(), hours24Ago.getMonth(), hours24Ago.getDayOfMonth(),
				hours24Ago.getHour(), 0, 0);
		now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

		Specification<AvgResponseTimeModel> ticketSpecification = DashboardDomain
				.getSpeedOfResponseForAvgResponseTrendFilterSpecification(tenantId, hours24Ago, now);
		List<AvgResponseTimeModel> ticketsList = avgResponseTimeModelRepository.findAll(ticketSpecification);

		Specification<InvestigationSave> investigationSpecification = DashboardDomain
				.getSpeedOfResponseForInvestigationTrendFilterSpecification(tenantId, hours24Ago, now);
		List<InvestigationSave> investigationSavesList = investigationSaveRepository
				.findAll(investigationSpecification);

		List<SpeedOfResponseForValidationTrendDataDto> threatResponsespeedHoursList = threatResponsespeedHourRepository
				.findAllByCreatedDateBetweenAndTenantId(hours24Ago, now, tenantId);

		return AvgResponseTimeMapper.map(ticketsList, investigationSavesList, threatResponsespeedHoursList);
	}

	@Override
	public Object getDefaultSettings() {

		GlobalSettings globalSettings = globalSettingsRepository
				.findByParamTypeAndParamName(AppConstants.SYS_PARAM_TYPE_SINGLE_DASHBOARD,
						AppConstants.SYS_PARAM_NAME_DEFAULT_DAYS)
				.orElseThrow(() -> new EntityNotFoundException(ResponseOrMessageConstants.GLOBAL_SETTINGS_NOT_FOUND
						+ StringUtils.SPACE + AppConstants.SYS_PARAM_TYPE_SINGLE_DASHBOARD + StringUtils.SPACE
						+ AppConstants.SYS_PARAM_NAME_DEFAULT_DAYS));

		return Map.of(ResponseOrMessageConstants.CUSTOM_DATE_RANGE, globalSettings.getParamValue());
	}

	
}
