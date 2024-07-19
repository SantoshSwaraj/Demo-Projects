package aisaac.util;

import java.util.List;

public class AppConstants {
	
	public static final String DATETIME_TZ_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String EXPORT_FILENAME_DATETIME_FORMAT_STR = "yyyyMMdd_HHmmss";
	public static final String EXPORT_EXPIRY_DATETIME_FORMAT_STR = "dd-MMM-yyyy";
	public static final String EXPORT_GENERIC_DATETIME_FORMAT_STR = "dd-MMM-yyyy HH:mm:ss";
	
	public static final String GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS = "mvc_app_settings";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT = "export_max_limit";
	public static final Integer DEFAULT_APP_SETTINGS_EXPORT_LIMIT = 5000;
	
	public static final String DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX = "attachment; filename=";
	
	public static final String EXPOSURES_REPORT_FILENAME = "Report";
	public static final String EXPOSURES_FILENAME_FORWARD_SLASH = "/";
	
	public static final String EXPOSURES_FILENAME_PREFIX = "\"";
	public static final String EXPOSURES_FILENAME_SEPARATOR_START = "Exposures";
	public static final String EXPOSURES_SEPARATOR_SPACE = " ";
	public static final String EXPOSURES_FILENAME_SEPARATOR_PART = " Part ";
	public static final String EXPOSURES_FILENAME_SEPARATOR_LAST = "(last)";
	public static final String EXPOSURES_FILENAME_EXTENSION_XLSX = ".xlsx";
	public static final String EXPOSURES_FILENAME_XLSX = "xlsx";
	public static final String EXPOSURES_FILENAME_EXTENSION_CSV = ".csv";
	public static final String EXPOSURES_FILENAME_SUFFIX = "\"";
	
	public static final List<String> EXPOSURES_EXPORT_COLUMNS = List.of("Organization", "Title", "Confidence",
			"Reported On", "Issue Category", "Issue ID", "Issue Sub-category", "MITRE Technique", "Severity",
			"Severity Score", "Affected IP Address", "CVE ID", "Potential Impact", "Summary", "Asset Owner", "Location",
			"First Detected On");
	
	public static final List<String> EXPOSURES_EXPORT_COLUMNS_SINGLE_USER = List.of("Organization", "Title", "Confidence",
			"Reported On", "Issue Category", "Issue ID", "Issue Sub-category", "MITRE Technique", "Severity",
			"Severity Score", "Affected IP Address", "CVE ID", "Potential Impact", "Summary", "Asset Owner", "Location",
			"First Detected On");

	public static final String EXPOSURES_NAME = "exposures";

	public static final String API_CONFIGURATION_SETTINGS_PARAM_NAME_EXPOSURES_EXPORT_PATH = "exposures_export_path";
	public static final String API_CONFIGURATION_SETTINGS_PARAM_TYPE_EXPOSURES = "exposures";
	
	public static final String EXPORT_FILE_PATH = "exportFilePath";
	public static final String PART_FILE_INFO = "partFileInfo";

}
