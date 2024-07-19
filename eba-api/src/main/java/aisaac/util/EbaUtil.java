package aisaac.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class EbaUtil {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern(AppConstants.EXPORT_GENERIC_DATETIME_FORMAT_STR);

	public static Long getLocalDateTimeInMilliSec(LocalDateTime date) {
		if (Objects.isNull(date)) {
			return null;
		}
		return date.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	
	public static LocalDateTime getUtcDateTime(Long epochMilliSeconds) {
		if (Objects.isNull(epochMilliSeconds) || epochMilliSeconds.intValue() == 0) {
			return null;
		}
		return Instant.ofEpochMilli(epochMilliSeconds).atZone(ZoneId.of("UTC")).toLocalDateTime();
	}
	
	public static String getLocalDateTimeInStringFormat(LocalDateTime localDateTime) {
		if (Objects.isNull(localDateTime))
			return null;

		return localDateTime.format(dateTimeFormatter);
	}
	
	public static String getSeverityType(Float value) {
		if (Objects.nonNull(value) && value > 75)
			return AppConstants.CRITICAL;
		else if (Objects.nonNull(value) && value <= 75 && value > 50)
			return AppConstants.HIGH;
		else if (Objects.nonNull(value) && value <= 50 && value > 25)
			return AppConstants.MEDIUM;
		else if (Objects.nonNull(value) && value <= 25)
			return AppConstants.LOW;
		else
			return AppConstants.NO_SCORE;
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
	
	public static String getEntityNameWithType(String entityName, String type) {
		if (StringUtils.isNotBlank(type) && type.toUpperCase().contains(AppConstants.PARAM_SOURCE.toUpperCase())) {
			return entityName + StringUtils.SPACE + AppConstants.OPEN_PARENTHESES + AppConstants.PARAM_SOURCE
					+ AppConstants.CLOSE_PARENTHESES;
		} else if (StringUtils.isNotBlank(type)
				&& type.toUpperCase().contains(AppConstants.PARAM_DESTINATION.toUpperCase())) {
			return entityName + StringUtils.SPACE + AppConstants.OPEN_PARENTHESES + AppConstants.PARAM_DESTINATION
					+ AppConstants.CLOSE_PARENTHESES;
		}
		return entityName;
	}

	public static String getEntityType(String type) {
		if (StringUtils.isNotBlank(type) && (type.toUpperCase().contains(AppConstants.PARAM_SOURCE_IP)
				|| type.toUpperCase().contains(AppConstants.PARAM_DESTINATION_IP))) {
			return AppConstants.PARAM_IP;
		} else if (StringUtils.isNotBlank(type) && (type.toUpperCase().contains(AppConstants.PARAM_SOURCE_HOSTNAME)
				|| type.toUpperCase().contains(AppConstants.PARAM_DESTINATION_HOSTNAME))) {
			return AppConstants.PARAM_HOST;
		}
		return AppConstants.PARAM_CLOUD;
	}
	
}
