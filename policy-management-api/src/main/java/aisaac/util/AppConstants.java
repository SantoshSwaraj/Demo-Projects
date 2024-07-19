package aisaac.util;

import java.util.List;

import jakarta.persistence.metamodel.SingularAttribute;

public class AppConstants {

	public static final String DATETIME_TZ_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String DATETIME_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String EXPORT_EXPIRY_DATETIME_FORMAT_STR = "dd-MMM-yyyy";
	public static final String EXPORT_GENERIC_DATETIME_FORMAT_STR = "dd-MMM-yyyy HH:mm:ss";
	public static final String EXPORT_FILENAME_DATETIME_FORMAT_STR = "yyyyMMdd_HHmmss";
	public static final String MOBILENO_PATTERN = "^([+]?\\d{1,3})?\\d{1,15}$";
	public static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[^\\w])(?=.*[a-z])(?=.*[A-Z])\\S{8,}$";
	public static final String MESSAGE = "message";

	public static final String POLICY_MANAGEMENT_FILENAME_PREFIX = "\"";
	public static final String POLICY_MANAGEMENT_FILENAME_SEPARATOR_START = "Policy Management";
	public static final String POLICY_MANAGEMENT_SEPARATOR_UNDERSCORE = "_";
	public static final String POLICY_MANAGEMENT_FILENAME_SEPARATOR_PART = " Part ";
	public static final String POLICY_MANAGEMENT_FILENAME_SEPARATOR_LAST = "(last)";
	public static final String POLICY_MANAGEMENT_FILENAME_EXTENSION_XLSX = ".xlsx";
	public static final String POLICY_MANAGEMENT_FILENAME_XLSX = "xlsx";
	public static final String POLICY_MANAGEMENT_FILENAME_EXTENSION_CSV = ".csv";
	public static final String POLICY_MANAGEMENT_FILENAME_SUFFIX = "\"";
	public static final String FILENAME_FORWARD_SLASH = "/";


	public static final String GLOBAL_SETTINGS_PARAM_TYPE_APP_SETTINGS = "mvc_app_settings";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_EXPORT_LIMIT = "export_max_limit";
	public static final String EXPORT_FILENAME_DATETIMEMS_FORMAT_STR = "ddMMMyyyy";
	public static final String POLICY_MANAGEMENT_FILENAME_FORWARD_SLASH = "/";
	public static final String API_CONFIGURATION_SETTINGS_PARAM_NAME_PM_EXPORT_PATH = "PM_export_path";
	public static final String API_CONFIGURATION_SETTINGS_PARAM_TYPE_POLICY_MANAGEMENT = "policy_management";
	public static final Integer DEFAULT_APP_SETTINGS_EXPORT_LIMIT = 5000;

	public static final String POLICY_MANAGEMENT_FINDINGS_FILENAME = "AWS_Findings";
	public static final String POLICY_MANAGEMENT_AZURE_FINDINGS_FILENAME = "Azure_recommendations";
	public static final String POLICY_MANAGEMENT_PRISMA_CLOUD_FINDINGS_FILENAME = "Prisma Cloud_Policy Name";


	public static final Boolean LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT = false;
	public static final Boolean LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT = false;
	public static final String LOG_STOPPAGE_MASTER_DELETE_COLUMN_NAME_IN_ENTITY = "isDeleted";
	public static final Boolean POLICY_MANAGEMENT_DELETE_COLUMN_DEFAULT = false;
	public static final String POLICY_MANAGEMENT_RECORD_STATE_IN_ENTITY = "recordState";
	public static final String POLICY_MANAGEMENT_SOURCE_NAME_IN_ENTITY = "sourceName";
	public static final String POLICY_MANAGEMENT_SOURCE_NAME = "CSPM";
	public static final String POLICY_MANAGEMENT_RECORD_STATE = "Active";

	public static final String DOWNLOAD_RESPONSE_HEADER_VALUE_PREFIX = "attachment; filename=";

	public static final List<String> POLICY_MANAGEMENT_EXPORT_COLUMNS = List.of("Organization", "Finding",
			"Cloud Resource ID", "Security Control ID", "Tenant ID", "Severity", "Updated On", "Reported On", "Compliance Status",
			"Workflow Status", " Remediation Playbook Name", "Standards","Created On", "Remediation Status");

	public static final List<String> POLICY_MANAGEMENT_EXPORT_COLUMNS_SINGLE_USER = List.of("Finding",
			"Cloud Resource ID", "Security Control ID", "Tenant ID", "Severity", "Updated On", "Reported On", "Compliance Status",
			"Workflow Status", " Remediation Playbook Name", "Standards","Created On", "Remediation Status");
	
	public static final List<String> POLICY_MANAGEMENT_AZURE_EXPORT_COLUMNS = List.of("Organization", "Recommedation",
			"Cloud Resource ID", "Security Control ID", "Tenant ID", "Severity", "Updated On", "Reported On", "Compliance Status",
			"Standards","Created On");

	public static final List<String> POLICY_MANAGEMENT_AZURE_EXPORT_COLUMNS_SINGLE_USER = List.of("Recommedation",
			"Cloud Resource ID", "Security Control ID", "Tenant ID", "Severity", "Updated On", "Reported On", "Compliance Status",
		    "Standards","Created On");
	
	public static final List<String> POLICY_MANAGEMENT_PLASMA_CLOUD_EXPORT_COLUMNS = List.of("Organization", "Policy Name",
			"Cloud Resource ID", "Security Control ID", "Tenant ID", "Severity", "Updated On", "Reported On", "Compliance Status",
			"Standards","Cloud Service Name","Created On");

	public static final List<String> POLICY_MANAGEMENT_PLASMA_CLOUD_EXPORT_COLUMNS_SINGLE_USER = List.of("Policy Name",
			"Cloud Resource ID", "Security Control ID", "Tenant ID", "Severity", "Updated On", "Reported On", "Compliance Status",
		    "Standards","Cloud Service Name","Created On");

	
	public static final String POLICY_MANAGEMENT_USER_CUSTOMIZATION_TABS = "policy-management";
	public static final String POLICY_MANAGEMENT_AZURE_SECTION_NAME = "policy-management-azure";
	public static final String POLICY_MANAGEMENT_PRISMA_CLOUD_SECTION_NAME = "policy-management-prisma-cloud";

	public static final String POLICY_MANAGEMENT_NAME = "Policy Management";
	public static final String POLICY_MANAGEMENT_USER_CUSTOMIZATION_SUCCESS = "User Customization Details Applied Successfully ";

	// tenant_setting param name
	public static final String TENANT_SETTINGS_PARAM_TYPE_PM_REMEDIATION = "pm_remediation";

	// Policy Management Tenant Settings Configuration Param Names:
	public static final String PM_REMEDIATION_ENABLE = "pm_remediation_enable";
	public static final String PM_REMEDIATION_SERVER_ADDRESS = "pm_remediation_server_address";
	public static final String PM_REMEDIATION_USE_PROXY = "pm_remediation_use_proxy";
	public static final String PM_REMEDIATION_TENANTID = "pm_remediation_tenantid";

	public static final String GLOBAL_SETTINGS_PARAM_TYPE_PROXY_SETTING = "proxy_setting";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PROXY_ENABLED = "proxy_enabled";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PROXY_HOSTNAME = "proxy_hostname";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PROXY_PORTNUMBER = "proxy_portnumber";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PROXY_IS_AUTHORIZED = "proxy_is_authorized";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PROXY_USERNAME = "proxy_username";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PROXY_PASSWORD = "proxy_password";

	public static final String CSPM_COMPILANCE_STATUS_PASSED = "Passed";
	public static final String CSPM_FINDING_STATUS_RESOLVED = "Resolved";
	public static final String CSPM_FINDING_STATUS_NOTIFIED = "Notified";
	public static final String CSPM_FINDING_STATUS_SUPPRESSED = "Suppressed";


	public static final String CSPM_FINDING_STATUS_PROCESSING = "In Progress";
	public static final String CSPM_FINDING_STATUS_SUCCESSFUL = "Successful";
	public static final String CSPM_FINDING_STATUS_FAILED = "Failed";

	public static final String CSPM_REMEDIATION_STATUS_NEW = "New";
	public static final String CSPM_REMEDIATION_STATUS_NA = "NA";
	
	public static final String CSPM_COMPILANCE_STATUS_FAILED = "Failed";	
	public static final String CSPM_WORK_FLOW_STATUS_NEW = "New";
	public static final String PLAYBOOK_NAME = "remediationPlaybookName";
	public static final String SORTING_ORDER_ASC = "asc";
	
	public static final String EXPORT_FILE_PATH = "exportFilePath";
	public static final String PART_FILE_INFO = "partFileInfo";
	

	public static final String GLOBAL_SETTINGS_PARAM_TYPE_POLICY_MANAGMENT="policy_management";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_SOURCE_NAME="pm_aws_config_source_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_CSPM_SOURCE_NAME="pm_cspm_source_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_VENDOR_PARAM_NAME="pm_aws_vendor_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_AWS_PRODUCT_PARAM_NAME="pm_aws_product_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_AZURE_VENDOR_PARAM_NAME="pm_azure_vendor_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_AZURE_PRODUCT_PARAM_NAME="pm_azure_product_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_PRISMA_CLOUD_VENDOR_PARAM_NAME="pm_prisma_cloud_vendor_name";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_PM_PRISMA_CLOUD_PRODUCT_PARAM_NAME="pm_prisma_cloud_product_name";

	
	public static final String CSPM_FINDING_RECORD_STATE_ACTIVE = "active";
	public static final String CSPM_FINDING_AWS_VENDOR_NAME = "AWS";
	public static final String CSPM_FINDING_AWS_PRODUCT_NAME = "Security Hub";
	public static final String CSPM_FINDING_VENDOR_NAME = "Microsoft";
	public static final String CSPM_FINDING_PRODUCT_NAME = "Defender For Cloud (CSPM)";
	public static final String CSPM_FINDING_PRISMA_CLOUD_VENDOR_NAME = "Palo Alto Networks";
	public static final String CSPM_FINDING_PRISMA_CLOUD_PRODUCT_NAME = "Prisma CSPM";
	public static final String GLOBAL_SETTINGS_PARAM_NAME_DEFAULT_SEARCH_DAYS_POLICY_MANAGMENT="default_search_days";

	
	public static final String CSPM_FINDING_COLUMN_CREATED_DATE="createdDate";
	
	

}
