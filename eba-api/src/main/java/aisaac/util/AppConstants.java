package aisaac.util;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class AppConstants {
	public static final String DATETIME_TZ_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String EXPORT_FILENAME_DATETIMEMS_FORMAT_STR = "yyyyMMdd_hhmmss";
	public static final String EXPORT_GENERIC_DATETIME_FORMAT_STR = "HH:mm:ss";
	public static final String DATE_TIME_HOUR_FORMAT_STR = "yyyy-MM-dd HH':00:00'";
	public static final String DATE_TIME_FOR_WATCHLIST = "dd-MMM-yyyy HH:mm:ss";

	public static final String FILENAME_SEPARATOR_START = "Security_Analytics";
	public static final String FILENAME = "EBA";
	public static final String MAPPED_ASSETS_FILENAME = "Mapped Assets";
	public static final String PERCENTAGE = "%";
	public static final String FILENAME_PREFIX = "\"";
	public static final String SEPARATOR_UNDERSCORE = "_";
	public static final String FILENAME_SEPARATOR_PART = " Part ";
	public static final String FILENAME_SEPARATOR_LAST = "(last)";
	public static final String FILENAME_EXTENSION_XLSX = ".xlsx";
	public static final String FILENAME_EXTENSION_CSV = ".csv";
	public static final String FILENAME_SUFFIX = "\"";
	public static final String FILENAME_FORWARD_SLASH = "/";
	public static final Integer DEFAULT_APP_SETTINGS_EXPORT_LIMIT = 5000;
	public static final String GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS = "mvc_app_settings";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT = "export_max_limit";

	public static final String CREATE = "create";
	public static final String DELETE = "delete";
	public static final String EBA_WATCHLIST = "eba_watchlist";
	public static final String SUCCESS = "Success";
	public static final String EXPORT_FILE_PATH = "exportFilePath";
	public static final String EXPORT_FILE_NAME = "exportFileName";
	public static final String PART_FILE_INFO = "partFileInfo";
	public static final String EXCEL_NULL_VALUE = "-";

	public static final String API_CONFIGURATION_SETTINGS_PARAM_NAME_EBA_EXPORT_PATH = "eba_export_path";
	public static final String API_CONFIGURATION_SETTINGS_PARAM_TYPE_EBA = "eba";

	public static final DateTimeFormatter DATE_TIME_HOUR_FORMATTER = DateTimeFormatter
			.ofPattern(AppConstants.DATE_TIME_HOUR_FORMAT_STR);
	public static final DateTimeFormatter EXPORT_GENERIC_DATETIME_FORMATTER = DateTimeFormatter
			.ofPattern(AppConstants.EXPORT_GENERIC_DATETIME_FORMAT_STR);

	public static final DateTimeFormatter DATE_TIME_FORMATTER_WATCHLIST = DateTimeFormatter
			.ofPattern(AppConstants.DATE_TIME_FOR_WATCHLIST);

	public static final List<String> EBA_THREAT_EXPORT_FILE_HEADER = List.of("Threat Name", "Threat Source",
			"Source IP", "Destination IP", "Product Hostname", "Destination Hostname", "URL", "Time", "Source Port",
			"Destination Port", "Source Location", "Destination Location", "Severity", "Usernames");

	public static final String CRITICAL = "Critical";
	public static final String HIGH = "High";
	public static final String MEDIUM = "Medium";
	public static final String LOW = "Low";
	public static final String NO_SCORE = "No Score";
	public static final Map<String, Long> SEVERITY_MAP = Map.of("Critical", 0L, "High", 0L, "Medium", 0L, "Low", 0L,
			"No Score", 0L);
	public static final String CHART_DATA = "chartData";
	public static final String TOTAL_ENTITY_COUNT = "totalEntityCount";
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	public static final String ENTITY_SCORE_DIFF = "entityScoreDiff";
	public static final String CLOUD_RESOURE_ID = "cloudresourceid";
	public static final String SYS_PARAM_TYPE_PARAMETER_ENTITY_TYPE = "entity_type";
	public static final String SYS_PARAM_TYPE_NOT_FOUND = "Sys Parameter Type not Found -";
	public static final String SYS_PARAM_VALUE_NOT_FOUND = "Sys Parameter Value not Found -";
	public static final Long SYS_PARAM_VALUE_IP_VALUE = 425L;
	public static final Long SYS_PARAM_VALUE_HOSTANAME_VALUE = 426L;
	public static final Long SYS_PARAM_VALUE_CLOUD_RESOURCE_ID_VALUE = 435L;
	public static final Long SYS_PARAM_OPEN_TICKET_STATUS = 70L;
	public static final String SYS_PARAM_TICKET_PRIORITY = "ticket_priority";
	public static final String SYS_PARAM_TICKET_CATEGORY = "ticket_category";
	public static final Integer USERSTATUS = 359;
	public static final String PARAM_SOURCE_IP = "SOURCE_IP";
	public static final String PARAM_DESTINATION_IP = "DESTINATION_IP";
	public static final String PARAM_SOURCE = "Source";
	public static final String PARAM_SOURCE_HOSTNAME = "SOURCE_HOSTNAME";
	public static final String PARAM_DESTINATION_HOSTNAME = "DESTINATION_HOSTNAME";
	public static final String PARAM_DESTINATION = "Destination";
	public static final String OPEN_PARENTHESES="(";
	public static final String CLOSE_PARENTHESES=")";
	public static final String TICKET_STATUS_STR_OPEN="OPEN";
	public static final String TICKET_STATUS_STR_REOPENED="REOPENED";
	public static final String PARAM_IP="ip";
	public static final String PARAM_HOST="host";
	public static final String PARAM_CLOUD="cloud";
	public static final List<String> EBA_MAPPED_ASSET_EXPORT_FILE_HEADER = List.of("Asset Name", "IP Address",
			"Hostname", "Cloud Resource ID", "Product Vendor", "Product Name", "Asset Tag(s)");

}
