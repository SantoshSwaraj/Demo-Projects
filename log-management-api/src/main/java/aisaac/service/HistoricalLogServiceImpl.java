package aisaac.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisaac.dao.HistoricalLogRepository;
import aisaac.dao.UserRepository;
import aisaac.domain.LmHistoricalLogDomain;
import aisaac.domain.datatable.DatatablePage;
import aisaac.dto.HistoricalLogDto;
import aisaac.exception.ValidationException;
import aisaac.model.LmHistoricalLog;
import aisaac.model.User;
import aisaac.payload.mapper.LmHistoricalLogMapper;
import aisaac.payload.request.HistoricalLogsDetailsRequest;
import aisaac.payload.response.HistoricalLogResponse;
import aisaac.payload.response.LmHistoricalLogReponseMapper;
import aisaac.utils.LMUtils;
import aisaac.utils.LmHistoricalStatus;

@Service
@Transactional
public class HistoricalLogServiceImpl implements HistoricalLogService{
	
	private HistoricalLogRepository historicalLogRepo;
	private UserRepository userRepo;
	
	public HistoricalLogServiceImpl(
			HistoricalLogRepository historicalLogRepo,
			UserRepository userRepo) {
		this.historicalLogRepo = historicalLogRepo;
		this.userRepo = userRepo;
	}

	@Override
	public boolean isMoreThanMaxQueryRunning(long historical_logs_max_queries_run) {
		long count = this.historicalLogRepo.countByStatus(LmHistoricalStatus.RUNNING);
		return (count >= historical_logs_max_queries_run);
	}
	
	@Override
	public HistoricalLogDto saveHistoricalLog(HistoricalLogDto dto) throws Exception {
		LmHistoricalLog lmHistoricalLog = LmHistoricalLogMapper.mapper(dto);
		lmHistoricalLog.setExecutedOn(new Date());
		
		LmHistoricalLog savedLmHistoricalLog = this.historicalLogRepo.save(lmHistoricalLog);
		return LmHistoricalLogMapper.mapper(savedLmHistoricalLog);
	}
	
	@Override
	public HistoricalLogResponse findByQueryId(String queryId) {
		LmHistoricalLog lmHistoricalLog = this.historicalLogRepo.findByQueryId(queryId)
			.orElseThrow(()->new ValidationException(String.format("No historical logs found for queryId: %s", queryId)));
		
		List<User> users = userRepo.findAll();
		
		Map<Integer, String> executedByAndNameMap = users.stream()
				.collect(Collectors.toMap(o -> o.getUserId(),
						o -> LMUtils.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));
		
		HistoricalLogResponse dto = LmHistoricalLogReponseMapper.map(lmHistoricalLog, executedByAndNameMap);
		
		return dto;
	}

	@Override
	public Object getHistoricalLogList(
			HistoricalLogsDetailsRequest request) {

		int pageNumber = request.getStart() / request.getLength(); // Calculate page number
		int length = request.getLength();
		
		//create Pageable instance
		Pageable pageable = PageRequest.of(pageNumber, length);
		
		Specification<LmHistoricalLog> logSpecification =
		LmHistoricalLogDomain.getLmHistoricalLogFilterSpecification(request);
		Page<LmHistoricalLog> page = historicalLogRepo.findAll(logSpecification,pageable);
		
		List<LmHistoricalLog> lmHistoricalLogList = page.getContent();
		
		List<User> users = userRepo.findAll();
		
		Map<Integer, String> executedByAndNameMap = users.stream()
				.collect(Collectors.toMap(o -> o.getUserId(),
						o -> LMUtils.getDisplayUsername(o.getDisplayName(), o.getDisplayRole(), o.isDeleted(), o.getUserStatus())));
		
		DatatablePage<Object> results = new DatatablePage<>();
		results.setRecordsTotal((int) page.getTotalElements());
		results.setRecordsFiltered((int) page.getTotalElements());
		results.setDraw(request.getDraw());
		results.setLength(request.getLength());
		results.setData(LmHistoricalLogReponseMapper.map(lmHistoricalLogList, executedByAndNameMap));
		return results;
	}

	@Override
	public List<HistoricalLogDto> findByTenantIdAndStatusNotIn(Integer tenantId, List<LmHistoricalStatus> statuses) {
		List<LmHistoricalLog> lmHistoricalLogs = this.historicalLogRepo
									.findByTenantIdAndStatusNotIn(tenantId, statuses);
		return LmHistoricalLogMapper.mapper(lmHistoricalLogs);
	}

	@Override
	public void updateHistoricalLogStatuses(Map<String, String> queryStatusMap) {
		
		if(MapUtils.isEmpty(queryStatusMap))
			return;
		
		queryStatusMap.forEach((k,v)->{
			this.historicalLogRepo.updateByQueryId(k, LmHistoricalStatus.fromString(v));
		});
	}

	@Override
	public void deleteQuery(String queryId) {
		this.historicalLogRepo.deleteQuery(queryId);
	}
}
