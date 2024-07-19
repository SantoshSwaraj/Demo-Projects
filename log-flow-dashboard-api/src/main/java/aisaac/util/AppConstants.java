package aisaac.util;

import java.util.List;

public class AppConstants {
	public static final String DATETIME_TZ_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String DATE_FORMAT_STR = "yyyy-MM-dd";
	public static final String DATETIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String EXPORT_EXPIRY_DATETIME_FORMAT_STR = "dd-MMM-yyyy";
	public static final String EXPORT_GENERIC_DATETIME_FORMAT_STR = "dd-MMM-yyyy HH:mm:ss";
	public static final String EXPORT_FILENAME_DATETIME_FORMAT_STR = "yyyyMMdd_HHmmss";
	public static final String EXPORT_FILENAME_DATETIMEMS_FORMAT_STR = "yyyyMMdd_HHmmssSS";
	public static final String MESSAGE = "message";

	public static final String LOG_FLOW_DASHBOARD_FILENAME_PREFIX = "\"";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_START = "Log Flow Dashboard";
	public static final String LOG_FLOW_DASHBOARD_SEPARATOR_UNDERSCORE = "_";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_PART = " Part ";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_SEPARATOR_LAST = "(last)";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_XLSX = ".xlsx";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_EXTENSION_CSV = ".csv";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_SUFFIX = "\"";
	public static final String LOG_FLOW_DASHBOARD_FILENAME_FORWARD_SLASH = "/";

	public static final String GLOBAL_SETTINGS_PARAM_TYPE_LFD_SETTINGS = "lfd_settings";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_SAMPLE_FILE = "bulk_upload_template";
	public static final String GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS = "mvc_app_settings";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT = "export_max_limit";
	public static final String API_CONFIGURATION_SETTINGS_PARAM_NAME_LFD_EXPORT_PATH = "LFD_export_path";
	public static final String API_CONFIGURATION_SETTINGS_PARAM_TYPE_LFD = "log_flow_dashboard";
	public static final Integer DEFAULT_APP_SETTINGS_EXPORT_LIMIT = 5000;

	public static final String LOG_FLOW_DASHBOARD_MONITORING_FILENAME = "Monitoring";
	public static final String LOG_FLOW_DASHBOARD_ADD_TO_LOGFLOW_MONITORING_FILENAME = "Add To Log Flow Monitoring";
	public static final String LOG_FLOW_DASHBOARD_ALLOWLIST_FILENAME = "Allow List";
	public static final boolean LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT = false;
	public static final boolean LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT = false;

	public static final boolean DETECTED_DEVICE_WHITELIST_DEFAULT_VALUE = false;

	public static final String LOG_STOPPAGE_MASTER_DELETE_COLUMN_NAME_IN_ENTITY = "deleted";
	public static final String COLUMN_NAME_CUSTOMER_ID = "tenantId";

	public static final String DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX = "attachment; filename=";

	public static final List<String> LOG_FLOW_MONITOT_UPLOAD_ACCESSIBLE_FORMAT = List.of("XLS", "XLSX");

	public static final List<String> LOG_FLOW_MONITOR_EXPORT_COLUMNS = List.of("Organization", "Product Type",
			"Product Vendor", "Product Name", "Product IP", "Product Hostname", "Asset Type", "Cloud Resource ID",
			"Severity", "Collector IP", "Collector Hostname", "MDR Agent Scanner Code", "Log Stoppage Threshold",
			"Email Notification Frequency", "AIsaac Current Flow State Since", "Last Event Received At AIsaac",
			"Email Address(es) (To Email)", "Email Address(es) (Cc Email)", "Created On", "Updated On", "AIsaac Flow Status",
			"Monitoring Status", "Note");
	
	public static final List<String> LOG_FLOW_MONITOR_EXPORT_COLUMNS_WITH_BU = List.of("Organization", "Product Type",
			"Product Vendor", "Product Name", "Product IP", "Product Hostname", "Asset Type", "Cloud Resource ID",
			"Severity", "Collector IP", "Collector Hostname", "MDR Agent Scanner Code", "Log Stoppage Threshold",
			"Email Notification Frequency", "AIsaac Current Flow State Since", "Last Event Received At AIsaac",
			"Email Address(es) (To Email)", "Email Address(es) (Cc Email)", "Created On", "Updated On",
			"AIsaac Flow Status", "Monitoring Status", "Note", "Business Units");

	public static final List<String> LOG_FLOW_DETECTED_DEVICES_EXPORT_COLUMN = List.of("Organization", "Product IP",
			"Product Hostname", "Product Vendor", "Product Name", "Collector IP", "Collector Hostname",
			"MDR Agent Scanner Code", "Detected On", "Last Event Received At AIsaac");
	public static final List<String> LOG_FLOW_ALLOW_LIST_DEVICES_EXPORT_COLUMN = List.of("Organization",
			"Product Vendor", "Product Name", "Description", "Allowlisted On");

	public static final List<String> LOG_FLOW_MONITOR_USER_ACTIONS = List.of("enable", "disable", "suppress", "resume",
			"delete", "addNote");
	public static final List<String> ADD_TO_LOG_FLOW_MONITOR_USER_ACTIONS = List.of("delete", "addNote");

	public static final String LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE = "addNote";
	public static final String LOG_FLOW_MONITOR_USER_ACTION_ENABLE_VALUE = "enable";
	public static final String LOG_FLOW_MONITOR_USER_ACTION_DISABLE_VALUE = "disable";
	public static final String LOG_FLOW_MONITOR_USER_ACTION_RESUME_VALUE = "resume";
	public static final String LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS_VALUE = "suppress";
	public static final String LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE = "delete";

	public static final String MONITOR_STATUS_ACTIVE = "Active";
	public static final String MONITOR_STATUS_DISABLED = "Disabled";
	public static final String MONITOR_STATUS_SUPPRESSED = "Suppressed";

	public static final String LOG_FLOW_MONITOR_PRODUCT_DUPLICATE = "Device already Exist";
	public static final String LOG_FLOW_MONITOR_PRODUCT_SAVE = " Products added to Log Flow Monitoring successfully";
	public static final String LOG_FLOW_MONITOR_PRODUCT_FAILED = "Fialed to add the products Into Log Flow Monitor";
	public static final String LOG_FLOW_MONITOR_PRODUCT_EDIT = "Product Details Updated Successfully to the Log Flow Monitor";
	public static final String LOG_FLOW_MONITOR_PRODUCT_EDIT_FAILED = "Fialed to Update the products Into Log Flow Monitor";

	public static final String ALLOW_LIST_PRODUCT_SAVE = "Products added successfully to the Allow List";
	public static final String ALLOW_LIST_PRODUCT_FAILED = "Fialed to add the products Into Allow List";
	public static final String ALLOW_LIST_PRODUCT_EDIT = "Product Details Updated successfully to the Allow List";
	public static final String ALLOW_LIST_PRODUCT_EDIT_FAILED = "Fialed to Update the products Into Allow List";

	public static final String LOG_FLOW_MONITOR_USER_ACTION_SUCCESS = "Successfully Performed the User Action ";
	public static final String USER_ACTION_FAILED = "User Action Request Failed";
	public static final String ADD_TO_LOG_FLOW_MONITOR_USER_ACTION_SUCCESS = "Successfully Performed the User Action ";
	public static final String ADD_TO_LOG_FLOW_MONITOR_USER_ACTION_FAILED = "Failed to Performed the User Action ";
	public static final String ALLOWLIST_USER_ACTION_SUCCESS = "Successfully Performed the User Action Delete";
	public static final String DIGEST_MAIL_SETTINGS_SUCCESS = "Digest Mail Settings Saved Successfully";

	public static final String LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_SUPPRESS = "suppressed";
	public static final String LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DISABLE = "disabled";
	public static final String LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DELETE = "deleted";
	public static final String LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_NOTE = "note";
	public static final String LOG_FLOW_DASHOARD_TABLE_COLUMN_NAME_DESCRIPTION = "description";

	public static final boolean LOG_FLOW_MONITOR_USER_ACTION_ENABLE = false;
	public static final boolean LOG_FLOW_MONITOR_USER_ACTION_DISABLE = true;
	public static final boolean LOG_FLOW_MONITOR_USER_ACTION_RESUME = false;
	public static final boolean LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS = true;
	public static final boolean LOG_FLOW_MONITOR_USER_ACTION_DELETE = true;
	public static final boolean ADD_TO_LOG_FLOW_MONITOR_USER_ACTION_DELETE = true;

	public static final String EMAIL_STATUS_DEFALUT_VALUE = "N";
	public static final String IS_MAIL_SEND_DEFALUT_VALUE = "No";
	public static final boolean MAIL_SEND_DEFALUT_VALUE = false;

	public static final List<String> LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_TABS = List.of("log-flow-monitoring",
			"detected-devices", "allowlist");
	public static final String LOG_FLOW_MONITORING_TAB = "log-flow-monitoring";
	public static final String LOGFLOW_LISTING = "logflow_listing";

	public static final String DETECTED_DEVICES_TAB = "detected-devices";
	public static final String ADDTOLOGFLOW_LISTING = "addtologflow_listing";

	public static final String ALLOWLIST_TAB = "allowlist";
	public static final String ALLOWLIST_LISTING = "allowlist_listing";

	public static final String LOG_FLOW_DASHBOARD_NAME = "logflowdashboard";
	public static final String LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_SUCCESS = "User Customization Details Applied Successfully ";
	public static final String LOG_FLOW_DASHBOARD_USER_CUSTOMIZATION_RESET = "Success";

	public static final String LOG_FLOW_MONITOR_DOWNLOAD_FILE_NAME = "log-flow-monitor-sample-file.xlsx";

	public static final String PRODUCT_VENDOR_FIELD_IN_PRODUCT_MASTER = "productVendor";
	public static final String PRODUCT_NAME_FIELD_IN_PRODUCT_MASTER = "productName";
	public static final String PRODUCT_TYPE_FIELD_IN_PRODUCT_MASTER = "productType";

	public static final List<String> AUTO_POPULATE_FIELDS_FOR_LFM = List.of("productIP", "productHostName",
			"cloudResourceID", "collectorAddress", "collectorHostName", "mdrScannerCode", "toEmailAddress",
			"ccEmailAddress");

	public static final List<String> AUTO_POPULATE_FIELDS_FOR_ADD_TO_LFM = List.of("productIP", "productHostName",
			"cloudResourceID", "collectorAddress", "collectorHostName", "mdrScannerCode");

	public static final String LFD_CUSTOM_FALSE_SETTINGS = "lfd_custom_false_settings";
	public static final String LFD_CUSTOM_TRUE_SETTINGS = "lfd_custom_true_settings";
	public static final String LFD_SERVERITY_SETTINGS = "lfd_serverity_settings";
	public static final String LFD_MONITOR_STATUS_SETTINGS = "lfd_monitor_status_settings";
	public static int HOUR_TO_MINUTE = 60;
	public static final String LOG_FLOW_MONITORING = "LFD Log Flow Monitoring";
	public static final String ALLOWLIST = "LFD Allowlist";
	public static final String DETECTED_DEVICES = "LFD Detected Devices";

	public static final String LFD_ACTION_TYPE_ADDED = "create";
	public static final String LFD_ACTION_TYPE_ADDED_BULK = "bulk_create";
	public static final String LFD_ACTION_TYPE_DELETED_BULK = "bulk_delete";
	public static final String LFD_ACTION_TYPE_ADDED_LOG_FLOW_MONITORING = "added_from_add_to_log_flow_monitoring";
	public static final String LFD_ACTION_TYPE_EDITED = "edit";
	public static final String LFD_ACTION_TYPE_RESUMED = "resumed";
	public static final String LFD_ACTION_TYPE_SUPPRESSED = "suppressed";
	public static final String LFD_ACTION_TYPE_ADD_TO_ALLOWLIST_FROM_ADD_TO_LOG_FLOW_MONITORING = "added_to_allowlist_from_add_to_log_flow_monitoring";
	public static final String LFD_ACTION_TYPE_ADDED_TO_ALLOWLIST = "added_to_allowlist";
	public static final String LFD_ACTION_TYPE_EDITED_IN_ALLOWLIST = "edited_in_allowlist";
	public static final String LFD_ACTION_TYPE_DELETED_FROM_ALLOWLIST = "deleted_from_allowlist";
	public static final String LFD_ACTION_TYPE_BULK_ENABLE = "enable_bulk";
	public static final String LFD_ACTION_TYPE_BULK_DISABLE = "disable_bulk";
	public static final String LFD_ACTION_TYPE_BULK_SUPRESS = "suppress_bulk";
	public static final String LFD_ACTION_TYPE_BULK_RESUME = "resume_bulk";
	public static final String LFD_ACTION_TYPE_ADDED_LOG_FLOW_MONITORING_BULK = "added_from_add_to_log_flow_monitoring_bulk";
	public static final String LFD_ACTION_TYPE_DELETED_ADD_TO_LOG_FLOW_MONITORING = "deleted_from_add_to_log_flow_monitoring";
	public static final String LFD_ACTION_TYPE_DELETED_ADD_TO_LOG_FLOW_MONITORING_BULK = "bulk_deleted_from_add_to_log_flow_monitoring";
	public static final String LFD_ACTION_TYPE_BULK_DELETED_FROM_ALLOWLIST = "bulk_deleted_from_allowlist";
	public static final String LFD_ACTION_TYPE_NOTE = "note";
	public static final String LFD_ACTION_TYPE_NOTE_BULK = "note_bulk";
	public static final String LFD_ACTION_TYPE_NOTE_ADDED_TO_ADD_TO_LOG_FLOW_MONITORING = "note_added_to_add_to_log_flow_monitoring";
	public static final String LFD_ACTION_TYPE_NOTE_BULK_ADDED_TO_ADD_TO_LOG_FLOW_MONITORING = "note_bulk_added_to_add_to_log_flow_monitoring";

	public static final Integer USERSTATUS = 359;

	public static final String LOG_FLOW_DELETE_ALL_PRODUCTS_SUCCESS = "Products Deleted Successfully";
	public static final String LOG_FLOW_DELETE_ALL_PRODUCTS_FAILED = "Failed to Delete the Products";
	public static final String SPACE = " ";

	public static final String SYSPARAM_ASSET_TYPE = "asset_type";

	public static final String LFD_BULK_UPLOAD_TEMPLATE_NAME = "Bulk_Upload_Template.xlsx";
	public static final List<String> LFD_BULK_UPLOAD_TEMPLATE_HEADERS = List.of("Organization*", "Asset Type*",
			"Monitoring Status*", "Product IP", "Product Hostname", "Product Vendor*", "Product Name*",
			"Cloud Resource ID", "Severity*", "Collector IP", "Collector Hostname", "MDR Agent Scanner Code",
			"Log Stoppage Threshold*", "Email Notification frequency*", "Send Email Notification for Log Stoppage",
			"Email Address(es) (To Email)", "Email Address(es) (Cc Email)", "Note");
	public static final String[] SEND_MAIL_DROP_DOWN_VALUES= {"Yes","No"};
	public static final String LFD_DEVICE_STATUS_STOPPED = "Stopped";
	public static final String LFD_DEVICE_STATUS_RUNNING = "Running";
	
	public static final String EXPORT_FILE_PATH = "exportFilePath";
	public static final String PART_FILE_INFO = "partFileInfo";
	public static final String EXPORT_FILE_NAME="exportFileName";
	
	public static final String MONITOR_STATUS="monitorStatus";
	public static final String DISABLED="disabled";
	public static final String SUPPRESSED="suppressed";
	public static final String ASSET_TYPE="assetType";
	public static final String PRODUCT_TYPE="productType";
	public static final List<String> PRODUCT_MASTER_FIELDS=List.of("productName","productVendor");
	public static final String SORTING_ORDER_ASC="asc";
	public static final String SORTING_ORDER_DESC="desc";
	public static final String TENANT_NAME="tenantName";
	public static final String LOG_STOPAGE_THRESHOLD_TIME="logStopageThresholdTime";
	public static final String EMAIL_ALERT_FREQUENCY="emailAlertFrequency";
	public static final String LOG_STOPPAGE_THRESHOLD_JSON="logStoppageThresholdJson";
	public static final String EMAIL_NOTIFICATION_FREQUENCY_JSON="emailNotificationFrequencyJson";
	
	
	public static final String SYSPARAM_TYPE_STATUS = "status";
    public static final String SYSPARAM_TYPE_STATUS_VALUE_NEW = "new";
    public static final String SYSPARAM_TYPE_STATUS_VALUE_IN_PROGRESS = "in progress";
    public static final String SYSPARAM_TYPE_STATUS_VALUE_SUCCESS = "success";
    public static final String SYSPARAM_TYPE_STATUS_VALUE_FAILED = "failed";
	
	public static final String MODULE_MASTER_NAME_LOG_FLOW_DASHBOARD = "log_flow_dashboard";
	public static final String MODULE_MASTER_SECTION_NAME_LOG_MONITORING = "log_monitoring";
	
	public static final String PERCENTAGE="%";
	public static final List<String> AUDIT_TRAIL_EXPORT_FIELDS = List.of("Organization", "Product Vendor & Name",
			"Action", "Performed By", "Performed On", "Details");
	public static final String LOG_FLOW_DASHBOARD_AUDIT_TRAIL_FILENAME = "Audit Trail";
	public static final List<String> AUDIT_TRAIL_DETAIL_FILED = List.of("customerName", "assetType",
			"monitorStatus", "deviceVendor", "deviceProduct", "deviceGroup", "deviceAddress", "deviceHostName",
			"cloudResourceID", "description", "note");
	public static final List<String> AUDIT_TRAIL_DETAIL_FILED_EDIT_ACTION = List.of("customerName", "assetType",
			"monitorStatus", "deviceVendor", "deviceProduct", "deviceGroup", "deviceAddress", "deviceHostName",
			"cloudResourceID", "severity", "collectorIp", "collectorHostName", "mdrScannerCode",
			"logStoppageThresholdJson", "emailNotificationFrequencyJson", "toEmailAddress",
			"ccEmailAddress", "isMailSend", "description", "note");
	
	public static final String GLOBAL_SETTINSG_PARAM_TYPE_CENTRAL_SIEM = "central_siem";
    public static final String GLOBAL_SETTINGS_PARAM_NAME_ENABLE_CENTRAL_SIEM_RULE = "enable_central_siem_rule";
    public static final String GLOBAL_SETTINGS_PARAM_VALUE_TRUE = "true";
    public static final String GLOBAL_SETTINGS_PARAM_VALUE_FALSE = "false";
    public static final String EXCEL_NULL_VALUE="-";
}
