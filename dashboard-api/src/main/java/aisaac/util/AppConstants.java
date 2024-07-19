package aisaac.util;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AppConstants {
	public static final String DATETIME_TZ_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String DATE_FORMAT_STR = "yyyy-MM-dd";
	public static final String MESSAGE = "message";
	public static final Long ASSET_MASTER_ASSET_STATE_SYSPARAM = 1L;
	public static final boolean FALSE = false;
	public static final Integer TICKET_STATUS = 154;
	public static final Integer TICKET_STATUS_70 = 70;
	public static final Integer TICKET_STATUS_71 = 71;
	public static final Integer TICKET_PRIORITY_69 = 69;
	public static final Integer USERSTATUS = 359;
	public static final String SYS_PARAM_TICKET_PRIORITY = "ticket_priority";
	public static final String SYS_PARAM_TICKET_CATEGORY = "ticket_category";
	public static final String COLOR_RED_STR = "red";
	public static final String COLOR_GREEN_STR = "green";
	public static final String PERCENTAGE = "%";
	public static final String DATE_TIME_HOUR_FORMAT_STR = "yyyy-MM-dd HH':00:00'";
	public static final DateTimeFormatter DATE_TIME_HOUR_FORMATTER = DateTimeFormatter
			.ofPattern(AppConstants.DATE_TIME_HOUR_FORMAT_STR);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_STR);
	public static Map<Integer, String> ASSET_CRITICAL_STATUS_MAP = Map.of(1, "Low", 2, "Low", 3, "Medium", 4, "Medium",
			5, "Medium", 6, "High", 7, "High", 8, "High", 9, "Critical", 10, "Critical");
	public static final String ASSET_CRITICAL_STATUS_UNDEFINED = "Undefined";
	public static final String TICKET_MAX_SEARCH_DAYS_SYS_PARAM_TYPE = "containment";
	public static final String TICKET_MAX_SEARCH_DAYS_SYS_PARAM_NAME = "max_search_days";
	public static final String HOURS_DIFFERENCE_0_TO_24 = "24 hrs";
	public static final String DAYS_DIFFERENCE_2_TO_7 = "2 to 7 days";
	public static final String DAYS_DIFFERENCE_8_TO_15 = "8 to 15 days";
	public static final String DAYS_DIFFERENCE_16_TO_30 = "16 to 30 days";
	public static final String DAYS_DIFFERENCE_30 = "More than 30 days";
	public static final String DATE_FORMAT_STR_HH_MM_SS = "yyyy-MM-dd HH':00:00'";
	public static final DateTimeFormatter DATE_FORMATTER_HH_MM_SS = DateTimeFormatter
			.ofPattern(AppConstants.DATE_FORMAT_STR_HH_MM_SS);
	public static final boolean BOOLEAN_FALSE = false;
	public static final Integer DEVICE_STATUS_ZERO = 0;
	public static final Integer DEVICE_STATUS_ONE = 1;
	public static final String LOG_STOPAGE_MASTER_FIELD_NAME_LOG_RECIEVE_TIME = "logReceiveTime";
	public static final String LOG_STOPAGE_MASTER_FIELD_NAME_LOG_STOPPAGE_TIME = "logStoppageTime";
	public static final List<String> ASSET_BY_LOG_FLOW_STATUS_DEFAULT_RESPONSE_PARAMS = List.of(HOURS_DIFFERENCE_0_TO_24, DAYS_DIFFERENCE_2_TO_7,
			DAYS_DIFFERENCE_8_TO_15, DAYS_DIFFERENCE_16_TO_30, DAYS_DIFFERENCE_30);
	public static final String SYS_PARAM_TYPE_SINGLE_DASHBOARD="single_dashboard";
	public static final String SYS_PARAM_NAME_DEFAULT_DAYS="default_days";
}
