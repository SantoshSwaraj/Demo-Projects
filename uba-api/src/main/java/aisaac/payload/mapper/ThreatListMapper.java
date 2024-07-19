package aisaac.payload.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.domain.UbaDomain;
import aisaac.entities.Threat;
import aisaac.payload.response.ThreatListResponse;
import aisaac.util.AppConstants;
import aisaac.util.UbaUtil;

public class ThreatListMapper {

	public static List<Object> map(List<Threat> data, String name) {
		
		return data.stream().map(o->new ThreatListResponse()
				.setThreatName(o.getThreatName())
				.setThreatSource(o.getThreatSource())
				.setSourceIp(o.getSourceIp())
				.setSourcePort(o.getSourcePort())
				.setDestinationIp(o.getDestinationIp())
				.setDestinationPort(o.getDestinationPort())
				.setSourceHostName(o.getSourceHostName())
				.setDestinationHostName(o.getDestinationHostName())
				.setDestinationUrl(o.getDestinationUrl())
				.setEventTime(UbaUtil.getLocalDateTimeInMilliSec(o.getEventTime()))
				.setSourceLocation(o.getSourceLocation())
				.setDestinationLocation(o.getDestinationLocation())
				.setSeverity(o.getSeverity())
				.setUserScore(o.getSourceIpUserScore())
				.setUserScoreText(UbaDomain.getUserScoreTextByThreatUserScore(o.getSourceIpUserScore()))
				.setUserName(name)
				).collect(Collectors.toList());
		
	}
	
	public static List<String[]> export(List<Threat> data,Map<Long, String> adUserMap) {
		List<String[]> result = new ArrayList<>();
		result.add(AppConstants.UBA_THREAT_EXPORT_FILE_HEADER
				.toArray(new String[AppConstants.UBA_THREAT_EXPORT_FILE_HEADER.size()]));
		

		data.forEach(obj -> {
			result.add(new String[] { 
					StringUtils.wrap(obj.getThreatName(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getThreatSource(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getSourceIp(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getDestinationIp(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getSourceHostName(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getDestinationHostName(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getDestinationUrl(), AppConstants.FILENAME_PREFIX),
					UbaUtil.getLocalDateTimeInStringFormat(obj.getEventTime()),
					StringUtils.wrap(Objects.nonNull(obj.getSourcePort()) ? String.valueOf(obj.getSourcePort())
							: StringUtils.EMPTY, AppConstants.FILENAME_PREFIX),
					StringUtils
							.wrap(Objects.nonNull(obj.getDestinationPort()) ? String.valueOf(obj.getDestinationPort())
									: StringUtils.EMPTY, AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getSourceLocation(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(obj.getDestinationLocation(), AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(
							Objects.nonNull(obj.getSeverity()) ? String.valueOf(obj.getSeverity()) : StringUtils.EMPTY,
							AppConstants.FILENAME_PREFIX),
					StringUtils.wrap(adUserMap.getOrDefault(obj.getAdUserId(), StringUtils.EMPTY),
							AppConstants.FILENAME_PREFIX) });
		});
		return result;
	}
}
