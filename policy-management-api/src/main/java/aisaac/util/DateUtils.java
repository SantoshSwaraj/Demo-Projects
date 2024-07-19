package aisaac.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Anand Swamy.
 */
public class DateUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(AppConstants.EXPORT_GENERIC_DATETIME_FORMAT_STR);
    private static final DateTimeFormatter expiryDateTimeFormatter = DateTimeFormatter.ofPattern(AppConstants.EXPORT_EXPIRY_DATETIME_FORMAT_STR);

    /**
     * Returns today's date as java.util.Date object
     *
     * @return today's date as java.util.Date object
     */
    public static Date today() {
        return new Date();
    }

    /**
     * Returns today's date as yyyy-MM-dd format
     *
     * @return today's date as yyyy-MM-dd format
     */
    public static String todayStr() {
        return sdf.format(today());
    }

    /**
     * Returns the formatted String date for the passed java.util.Date object
     *
     * @param date
     * @return
     */
    public static String formattedDate(Date date) {
        return date != null ? sdf.format(date) : todayStr();
    }
	
	public static String getTimeZoneString() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter customFormat = DateTimeFormatter.ofPattern(AppConstants.DATETIME_TZ_FORMAT_STR);
		return currentDateTime.format(customFormat);
	}
	public static Long getDateInMilliSecs(LocalDateTime localDateTime) {
		Long dateInMilliSecs = 0l;
		if(Objects.nonNull(localDateTime))
			dateInMilliSecs = Timestamp.valueOf(localDateTime).getTime();
		
		return dateInMilliSecs;
	}
	public static LocalDateTime getLocalDateTime(Long epochMilliSeconds) {
		if(Objects.isNull(epochMilliSeconds) || epochMilliSeconds.intValue() == 0) {
			return null;
		}
		return Instant.ofEpochMilli(epochMilliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static String getUtcDateTime(Long epochMilliSeconds) {
		if(Objects.isNull(epochMilliSeconds) || epochMilliSeconds.intValue() == 0) {
			return null;
		}
		LocalDateTime localDateTime	= Instant.ofEpochMilli(epochMilliSeconds).atZone(ZoneId.of("UTC")).toLocalDateTime();
		 String formattedDate = localDateTime.format(dateTimeFormatter);
		return formattedDate;
	}

	
	public static Date calculateExpiryDate(final Date startDate, final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate.getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
	
	public static String getLocalDateTimeInStringFormat(LocalDateTime localDateTime, boolean itIsExpiryDate) {
		if(Objects.isNull(localDateTime))
			return null;
		if(itIsExpiryDate)
			return localDateTime.format(expiryDateTimeFormatter);
		else
			return localDateTime.format(dateTimeFormatter);
	}

	public static String fetchLocalDateTimeInStringFormat(LocalDateTime localDateTime) {
		if (Objects.isNull(localDateTime)) {
			return null;
		} else {
			return localDateTime.format(dateTimeFormatter);
		}

	}
}
