package aisaac.util;

import java.time.format.DateTimeFormatter;

public class AuditTrailLableConstans {

	public static final String ORGNIZATION="Organization";
	public static final String ASSET_TYPE="Asset Type";
	public static final String MONITORING_STATUS="Monitoring Status";
	public static final String PRODUCT_VENDOR="Product Vendor";
	public static final String PRODUCT_NAME="Product Name";
	public static final String PRODUCT_TYPE="Product Type";
	public static final String PRODUCT_IP="Product IP";
	public static final String PRODUCT_HOST_NAME="Product Hostname";
	public static final String CLOUD_RESOURCE_ID="Cloud Resource ID";
	public static final String SEVERITY="Severity";
	public static final String COLLECTOR_IP="Collector IP";
	public static final String COLLECTOR_HOSTNAME="Collector Hostname";
	public static final String LOG_STOPPAGE_THRESHOLD="Log Stoppage Threshold";
	public static final String EMAIL_NOTIFICATION_FREQUENCY="Email Notification Frequency";
	public static final String TO_EMAIL_ADDRESS="Email Address(es) (To Email)";
	public static final String CC_EMAIL_ADDRESS="Email Address(es) (CC Email)";
	public static final String IS_MAIL_SEND="Send Email notification for Log stoppage";
	public static final String NOTE="Note";
	public static final String PRODUCT_ID="Product ID";
	public static final String MDR_SCANNER_CODE="MDR Agent Scanner Code";
	public static final String ASSET_ENTRY_METHOD_ID="Asset Entry Method ID";
	public static final String ASSET_ID="Asset ID";
	public static final String LAST_EVENT_RECEIVED="Last Event Received";
	public static final String TENANT_ID="Tenant ID";
	public static final String DEVICE_STATUS="Device Status";
	public static final String LOG_STOPPAGE_TIME="Log Stoppage Time";
	public static final String LOG_RECIEVED_TIME="Log Received Time";
	public static final String DISABLED="Disabled";
	public static final String SUPPRESSED="Suppressed";
	public static final String LOGGER_STATUS="Logger Status";
	public static final String LOGGER_DATE="Logger Date";
	public static final String DELETED="Deleted";
	public static final String CREATED_DATE="Created Date";
	public static final String UPDATED_DATE="Updated Date";
	public static final String CREATED_BY="Created By";
	public static final String UPDATED_BY="Updated By";
	public static final String EMAIL_STATUS="Email Status";
	public static final String EMAIL_THREASHOLD_REACHED="Email Threashold Reached";
	public static final String EMAIL_TIME="Email Time";
	public static final String DEVICE_RECEIPT_TIME="Device Receipt Time";
	public static final String SEND_EMAIL_ALERT_AUDIT="Send Audit Email Alert";
	public static final String LOG_STOPPAGE_THRESHOLD_JSON="Log Stoppage Threshold Json";
	public static final String EMAIL_NOTIFICATION_FREQUENCY_JSON="Email Notification Frequency Json";
	public static final String DESCRIPTION="Description";
	public static final String WHILTELIST="WhilteList";
	public static final String PRODUCT_TYPE_ID="Product Type ID";

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
	public static final String BOOLEN_TRUE_STRING_VALUE="1";
	public static final String BOOLEN_FALSE_STRING_VALUE="0";
	public static final String DISABLED_STRING_VALUE="Disabled";
	public static final String ENABLE_STRING_VALUE="Enabled";
}
