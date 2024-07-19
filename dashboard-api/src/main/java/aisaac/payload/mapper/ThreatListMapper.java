package aisaac.payload.mapper;

import aisaac.entities.Threat;
import aisaac.payload.response.ThreatListResponse;

public class ThreatListMapper {

	public static Object map(Threat threat) {
		return new ThreatListResponse()
				.setThreatId(threat.getThreatId())
				.setSourceIp(threat.getSourceIp())
				.setDestinationIp(threat.getDestinationIp())
				.setThreatName(threat.getThreatName())
				.setCount(threat.getCount())
				.setScore(threat.getFinalScore())
				.setDestinationPort(threat.getDestinationPort())
				.setDestinationUrl(threat.getDestinationUrl());
	}
}
