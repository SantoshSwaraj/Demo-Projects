package aisaac.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.dto.LogStopageMasterAssetDto;
import aisaac.entities.ProductMaster;

public class DashboardUtils {

	public static long calculateDaysGapByDate(Date date1, Date date2) {

		long diffInMillies = date2.getTime() - date1.getTime();
		return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}
	
	public static long calculateDaysGapByLocalDateTime(LocalDateTime date1, LocalDateTime date2) {
		return ChronoUnit.DAYS.between(date1, date2);
	}

	public static String calculateCountIntoTextFormat(Long count) {
		count = Math.abs(count);
		if (count >= 1000 && count < 1000000)
			return Double.toString(calculateFraction(count , 1000)) + " K";
		else if (count >= 1000000)
			return Double.toString(calculateFraction(count , 1000000)) + " M";
		else
			return String.valueOf(count);
	}
	
	public static double calculateFraction(double number, double divisor) {
	    return Math.round( (number / divisor) * 10.0 ) / 10.0;
	}
	
	public static LocalDateTime getLocalDateTimeByEpochMilli(Long value) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.systemDefault());
	}
	public static String getProductNameFromProductMaster(ProductMaster productMaster) {
		if (Objects.isNull(productMaster)) {
			return "Others";
		}
		return productMaster.getProductName();
	}
	public static String getProductVendorFromProductMaster(ProductMaster productMaster) {
		if (Objects.isNull(productMaster)) {
			return StringUtils.EMPTY;
		}
		return productMaster.getProductVendor();
	}
	
	public static String calculatePercentage(Long part, Long total) {
		if (total == 0) {
			throw new IllegalArgumentException(ResponseOrMessageConstants.TOTAL_CANNOT_BE_ZERO);
		}
		return (((double) part / total) * 100) < 1
				? Double.toString(calculateFraction(((double) part / total) * 100, 10)) + AppConstants.PERCENTAGE
				: Math.round(((double) part / total) * 100) + AppConstants.PERCENTAGE;
	}
	
	public static Map<Long, ProductMaster> getProductMasterMap(List<ProductMaster> productMastersList) {
		return productMastersList.stream().collect(Collectors.toMap(o -> o.getProductId(), o -> o));
	}
	
	public static String getDisplayUsername(String displayName, String displayrole, Boolean isDeleted,
			Integer isDisabled) {

		String displayNameStr = displayName;
		if (displayName==null)
			return null;

		if (isDeleted == null)
			isDeleted = false;

		if (isDisabled == null)
			isDisabled = 0;

		if (isDeleted) {
			displayNameStr = "Deleted User";
		} else if ((AppConstants.USERSTATUS).equals(isDisabled)) {
			displayNameStr += " (Disabled User)";
		} else if (StringUtils.isNotEmpty(displayrole))
			displayNameStr += "(" + displayrole + ")";

		return displayNameStr;
	}
	
	public static Long getLocalDateTimeInMilliSec(LocalDateTime date) {
		if (Objects.isNull(date)) {
			return null;
		}
		return date.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	

	public static String getAgeGroupByDate(LocalDateTime createdDate) {
		LocalDateTime dayDifferences1 = LocalDateTime.now().minusDays(1);
		LocalDateTime dayDifferences7 = LocalDateTime.now().minusDays(7);
		LocalDateTime dayDifferences15 = LocalDateTime.now().minusDays(15);
		LocalDateTime dayDifferences30 = LocalDateTime.now().minusDays(30);

		if (Objects.nonNull(createdDate) && createdDate.compareTo(dayDifferences1) > 0) {
			return AppConstants.HOURS_DIFFERENCE_0_TO_24;
		} else if (Objects.nonNull(createdDate) && createdDate.compareTo(dayDifferences7) > 0 && createdDate.compareTo(dayDifferences1) < 0) {
			return AppConstants.DAYS_DIFFERENCE_2_TO_7;
		} else if (Objects.nonNull(createdDate) && createdDate.compareTo(dayDifferences15) > 0 && createdDate.compareTo(dayDifferences7) < 0) {
			return AppConstants.DAYS_DIFFERENCE_8_TO_15;
		} else if (Objects.nonNull(createdDate) && createdDate.compareTo(dayDifferences30) > 0 && createdDate.compareTo(dayDifferences15) < 0) {
			return AppConstants.DAYS_DIFFERENCE_16_TO_30;
		} else if (Objects.nonNull(createdDate) && createdDate.compareTo(dayDifferences30) < 0) {
			return AppConstants.DAYS_DIFFERENCE_30;
		} else {
			return null;
		}
	}
	
	public static String getAgeGroupByDate(LocalDateTime createdDate, LocalDateTime reopenedDate) {
		LocalDateTime dayDifferences1 = LocalDateTime.now().minusDays(1);
		LocalDateTime dayDifferences7 = LocalDateTime.now().minusDays(7);
		LocalDateTime dayDifferences15 = LocalDateTime.now().minusDays(15);
		LocalDateTime dayDifferences30 = LocalDateTime.now().minusDays(30);

		if (createdDate.compareTo(dayDifferences1) > 0
				|| (Objects.nonNull(reopenedDate) && reopenedDate.compareTo(dayDifferences1) > 0)) {
			return AppConstants.HOURS_DIFFERENCE_0_TO_24;
		} else if ((createdDate.compareTo(dayDifferences7) > 0 && createdDate.compareTo(dayDifferences1) < 0)
				|| (Objects.nonNull(reopenedDate) && reopenedDate.compareTo(dayDifferences7) > 0
						&& reopenedDate.compareTo(dayDifferences1) < 0)) {
			return AppConstants.DAYS_DIFFERENCE_2_TO_7;
		} else if ((createdDate.compareTo(dayDifferences15) > 0 && createdDate.compareTo(dayDifferences7) < 0)
				|| (Objects.nonNull(reopenedDate) && reopenedDate.compareTo(dayDifferences15) > 0
						&& reopenedDate.compareTo(dayDifferences7) < 0)) {
			return AppConstants.DAYS_DIFFERENCE_8_TO_15;
		} else if ((createdDate.compareTo(dayDifferences30) > 0 && createdDate.compareTo(dayDifferences15) < 0)
				|| (Objects.nonNull(reopenedDate) && reopenedDate.compareTo(dayDifferences30) > 0
						&& reopenedDate.compareTo(dayDifferences15) < 0)) {
			return AppConstants.DAYS_DIFFERENCE_16_TO_30;
		} else if (createdDate.compareTo(dayDifferences30) < 0
				|| (Objects.nonNull(reopenedDate) && reopenedDate.compareTo(dayDifferences30) < 0)) {
			return AppConstants.DAYS_DIFFERENCE_30;
		} else {
			return null;
		}
	}

	public static Long getAssetCountFromLogStopageMaster(List<LogStopageMasterAssetDto> logStopageMastersList) {
		return (long) logStopageMastersList.size();
	}
}
