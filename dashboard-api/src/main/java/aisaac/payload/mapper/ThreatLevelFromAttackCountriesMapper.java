package aisaac.payload.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import aisaac.entities.GeoCountryMaster;
import aisaac.entities.ThreatLevelStatsCountrySrcipHour;
import aisaac.payload.response.ThreatLevelFromAttackCountriesMapResponse;

public class ThreatLevelFromAttackCountriesMapper {

	public static Object map(List<ThreatLevelStatsCountrySrcipHour> threatLevelStatsCountrySrcipHourList,
			Map<String, GeoCountryMaster> countryCodeAndGeoDataMap, Map<String, Long> countryCodeAndLevelMap) {

		return threatLevelStatsCountrySrcipHourList.stream().map(o -> {
			GeoCountryMaster geoCountryMaster = countryCodeAndGeoDataMap.get(o.getCountryCode());
			ThreatLevelFromAttackCountriesMapResponse response = new ThreatLevelFromAttackCountriesMapResponse()
					.setGeoCountrycode(o.getCountryCode())
					.setLat(geoCountryMaster.getLatitude())
					.setLong(geoCountryMaster.getLongitude())
					.setCityName(geoCountryMaster.getCountryName())
					.setThreatCount(o.getPriorityCount())
					.setThreatLevel(getThreatLevelName(countryCodeAndLevelMap.get(o.getCountryCode())));
			return response;
		}).collect(Collectors.toList());
	}
	
	public static String getThreatLevelName(Long threatLevelCode) {
		if (Objects.isNull(threatLevelCode))
			return null;
		
		switch (threatLevelCode.intValue()) {
		case 4:
			return "Severe";
		case 3:
			return "Elevated";
		case 2:
			return "Guarded";
		case 1:
			return "Normal";
		}
		return null;
	}
}
