package aisaac.payload.mapper;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import aisaac.entities.ThreatLevelStatsCountrySrcipHour;
import aisaac.payload.response.CountryMapAlertResponse;
import aisaac.util.AppConstants;

public class CountryMapAlertDetailsMapper {

	public static List<Object> map(List<ThreatLevelStatsCountrySrcipHour> data,
			LocalDateTime startDate, LocalDateTime endDate) {

		Map<String, List<ThreatLevelStatsCountrySrcipHour>> mapData = data.stream().collect(Collectors.groupingBy(
				ThreatLevelStatsCountrySrcipHour::getSourceIp, Collectors.mapping(o -> o, Collectors.toList())));
		List<String> hourList = new ArrayList<String>();
		if (startDate != null && endDate != null) {
			Duration duration = Duration.between(startDate, endDate);
			for (int k = 1; k <= duration.toHours(); k++) {
				hourList.add(startDate.plusHours(k).format(AppConstants.DATE_FORMATTER_HH_MM_SS));
			}
		}
		List<Object> popupList = new ArrayList<>();
		AtomicInteger count = new AtomicInteger();
		mapData.forEach((key, value) -> {
			CountryMapAlertResponse alertResponse = new CountryMapAlertResponse();
			alertResponse = getPopupData(hourList, value);
			if (alertResponse != null) {
				alertResponse.setRecId(count.getAndIncrement());
				popupList.add(alertResponse);
			}
		});

		if (popupList != null && popupList.size() > 0) {
			Collections.sort(popupList, new Comparator<Object>() {
				@Override
				public int compare(Object object1, Object object2) {
					int result = 0;
					CountryMapAlertResponse o1 = (CountryMapAlertResponse) object1;
					CountryMapAlertResponse o2 = (CountryMapAlertResponse) object1;
					if (o1.getDestinationIpCount() == null && o2.getDestinationIpCount() == null) {
						result = 0;
					} else if (o1.getDestinationIpCount() == null && o2.getDestinationIpCount() != null) {
						result = -1;
					} else if (o1.getDestinationIpCount() != null && o2.getDestinationIpCount() == null) {
						result = 1;
					} else {
						result = Integer.compare(Integer.valueOf(o2.getDestinationIpCount()),
								Integer.valueOf(o1.getDestinationIpCount()));
					}

					return result;
				}
			});
		}

		return popupList;
	}

	public static CountryMapAlertResponse getPopupData(List<String> hourList,
			List<ThreatLevelStatsCountrySrcipHour> dtoList) {
		CountryMapAlertResponse popupObj = new CountryMapAlertResponse();
		BigInteger dipCountArr[] = new BigInteger[24];
		BigInteger dipCount = new BigInteger("0");
		String dipCountTxt = "";
		int hourIndex = -1;
		LocalDateTime dateTimeTxt = null;
		try {

			for (int i = 0; i < dipCountArr.length; i++) {
				dipCountArr[i] = new BigInteger("0");
			}

			for (int i = 0; i < dtoList.size(); i++) {
				if (i == 0) {
					popupObj.setGeoCountrycode(dtoList.get(i).getCountryCode());
					popupObj.setSourceIp(dtoList.get(i).getSourceIp());
					dateTimeTxt = dtoList.get(i).getIntervalEndTime();
					dipCount = dipCount.add(dtoList.get(i).getDestinationIpCount());
					hourIndex = hourList.indexOf(dateTimeTxt.format(AppConstants.DATE_FORMATTER_HH_MM_SS));
					if (hourIndex != -1)
						dipCountArr[hourIndex] = dtoList.get(i).getDestinationIpCount();
				} else {
					dateTimeTxt = dtoList.get(i).getIntervalEndTime();
					hourIndex = hourList.indexOf(dateTimeTxt.format(AppConstants.DATE_FORMATTER_HH_MM_SS));
					dipCount = dipCount.add(dtoList.get(i).getDestinationIpCount());
					if (hourIndex != -1)
						dipCountArr[hourIndex] = dtoList.get(i).getDestinationIpCount();
				}
			}

			for (int i = 0; i < dipCountArr.length; i++) {
				if (i == dipCountArr.length - 1)
					dipCountTxt = dipCountTxt + dipCountArr[i];
				else
					dipCountTxt = dipCountTxt + dipCountArr[i] + ",";
			}

			popupObj.setDestinationIpCountTxt(dipCountTxt);
			popupObj.setDestinationIpCount(dipCount.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return popupObj;
	}

}
