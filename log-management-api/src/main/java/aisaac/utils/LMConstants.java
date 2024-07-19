package aisaac.utils;

public class LMConstants {
	
	public static final Integer DEFAULT_USER_ID = 1;
	public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";
    public static final Integer ONE = Integer.valueOf(1);
    public static final Integer TWO = Integer.valueOf(2);
    public static final Integer THREE = Integer.valueOf(3);
    
	public static final String SINGLE_QUOTE = "'";
	public static final String LOG_MANAGEMENT_FREE_SEARCH_OPERATOR = " LIKE_CI ";
	public static final String LOG_MANAGEMENT_STRING_FIELDS_DATATYPE = "STRING";
	public static final String LOG_MANAGEMENT_FREE_SEARCH_CONDITION = " OR ";

    public static final String BLANK_SPACE = " ";
	
	//LOG_MANAGEMENT 
    public static final String LOG_MANAGEMENT_PARAM_TYPE = "log_management";
    public static final String LOG_MANAGEMENT_VISIBILITY_PARAM_NAME = "lm_tab_visibility";
    public static final String LOG_MANAGEMENT_MAX_RESULTS_PARAM_NAME = "lm_max_results";
    public static final String LOG_MANAGEMENT_MAX_SAVED_QUERIES_PARAM_NAME = "lm_max_saved_queries";
    public static final String LOG_MANAGEMENT_MAX_SEARCH_HISTORY_PARAM_NAME = "lm_max_search_history";
    public static final String LOG_MANAGEMENT_PARAM_ELASTIC_SEARCH_URL = "lm_elastic_search_url";
    public static final String LOG_MANAGEMENT_MOST_USED_FIELDS_LIMIT = "lm_most_used_fields_limit";
    public static final String LOG_MANAGEMENT_CSV_EXPORT_PATH = "csv_export_path";
    public static final String LOG_MANAGEMENT_STRING_ARRAY_FIELDS = "lm_string_array_fields";
	public static final String LOG_MANAGEMENT_DEEP_SEARCH_COLUMNS = "lm_free_search_deep_search_columns";
	
	public static final Long MILLISECONDS_IN_0_SECOND = 0L;
	public static final Long MILLISECONDS_IN_1_SECOND = 1000L;
	public static final Long MILLISECONDS_IN_5_SECONDS = 5000L;
	public static final Long MILLISECONDS_IN_10_SECONDS = 10000L;
	public static final Long MILLISECONDS_IN_30_SECONDS = 30000L;
	public static final Long MILLISECONDS_IN_1_MINUTE = 60000L;
	public static final Long MILLISECONDS_IN_2_MINUTES = 120000L;
	public static final Long MILLISECONDS_IN_5_MINUTES = 300000L;
	public static final Long MILLISECONDS_IN_10_MINUTES = 600000L;
	public static final Long MILLISECONDS_IN_30_MINUTES = 1800000L;
	public static final Long MILLISECONDS_IN_1_HOUR = 3600000L;
	public static final Long MILLISECONDS_IN_2_HOURS = 7200000L;
	public static final Long MILLISECONDS_IN_6_HOURS = 21600000L;
	public static final Long MILLISECONDS_IN_12_HOURS = 43200000L;
	public static final Long MILLISECONDS_IN_1_DAY = 86400000L;
	public static final Long MILLISECONDS_IN_2_DAYS = 172800000L;
	public static final Long MILLISECONDS_IN_5_DAYS = 432000000L;
	public static final Long MILLISECONDS_IN_10_DAYS = 864000000L;
	public static final Long MILLISECONDS_IN_15_DAYS = 1296000000L;
    
    public static final String FILTERS_SPACE = "FILTERS ";
    public static final String GROUP_SPACE = "GROUP ";
    public static final String SORT_SPACE = "SORT ";
	public static final String TOP_SPACE = "TOP ";
	public static final String STORAGE_GROUP_SPACE = "STORAGE_GROUP ";
	public static final String FIELDS_SPACE = "FIELDS ";
    public static final String FILTERS = "FILTERS";
	public static final String GROUP = "GROUP";
	public static final String SORT = "SORT";
	public static final String TOP = "TOP";
	public static final String FIELDS = "FIELDS";
	public static final long DEFAULT_OFFSET = 0L;
	public static final String AGGR = "AGGR";
	public static final String HAVING = "HAVING";
	public static final String TOP_TEN_QUERY = "| FIELDS count(*) AS countVal | GROUP <groupBy> | SORT countVal ASC";
	public static final String GROUP_BY_COLUMN = "<groupBy>";
	public static final String COUNT_VAL_FIELD = "count(*) AS countVal";
	public static final String COUNT_VAL_COLUMN = "countVal";
	public static final Object HISTOGRAM_SELECT_CLAUSE = "| FIELDS ";
	public static final long HISTOGRAM_QUERY_LIMIT = 40;
	public static final long TOP_TEN_QUERY_LIMIT = 10;
	public static final String COUNT= "count";
	public static final String SUM = "sum";
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String MEAN = "mean";
	public static final String CARDINALITYDISTINCT= "CARDINALITY(DISTINCT(";
	public static final String EXACTDISTINCTCOUNT= "EXACTDISTINCTCOUNT(DISTINCT(";
	public static final String SORT_STRING_TYPE = "LEXICOGRAPHIC(";
	public static final String[] VALIDATION_IGNORE_KEYS = new String[] {"dataSource", "queryType", "intervals", "granularity", "virtualColumns"};
	public static final String AGGREGATIONS_JSON_KEY = "aggregations";
	public static final String HAVING_JSON_KEY = "having";
	public static final String FILTER_JSON_KEY = "filter";
	public static final String DIMENSIONS_JSON_KEY = "dimensions";
	public static final String REGEX = "REGEX";
	public static final String SIMILAR_TO = "SIMILAR TO";
	public static final String LIKE_CI_STRING = "LIKE_CI '";
	public static final String SIMILAR_TO_STRING = "SIMILAR TO '(?i)";
	
	public static final String SUMLONG = "sumlong";
	public static final String MINLONG = "minlong";
	public static final String MAXLONG = "maxlong";
	public static final String MEANDOUBLE = "meandouble";
	
	public static final String LOG_MANAGEMENT_DATASOURCE_TYPE_EVENTS = "events";
	public static final String LOG_MANAGEMENT_DATASOURCE_TYPE_HISTORICAL_LOGS = "historical_logs";
	public static final String LOG_MANAGEMENT_CUSTOMIZATION_TYPE_MOST_USED = "most_used";
	public static final String LOG_MANAGEMENT_CUSTOMIZATION_TYPE_DATATABLE_CUSTOMIZE = "datatable_customize";
	public static final String LOG_MANAGEMENT_DATE_FIELDS_DATATYPE = "LONGDATE";

	public static final String ROLE_GLOBAL_USER = "ROLE_GLOBAL_USER";
	public static final String ROLE_SIEM_ADMIN = "ROLE_SIEM_ADMIN";
	public static final String ROLE_GLOBAL_MANAGER = "ROLE_GLOBAL_MANAGER";
	
	public static final int CELL_MAX_CONTENT_LENGHT_LIMIT = 32760;
	
	public static final String MESSAGE = "message";
	public static final String PARAM_APP_SETTING_TYPE = "mvc_app_settings";
	public static final String PARAM_MVC_APP_SETTING_NAME_API_TIMEOUT="api_timeout";
	
	public static final String HISTORICAL_LOGS_MAX_QUERIES_RUN_PARAM_NAME = "historical_logs_max_queries_run";
	public static final String HISTORICAL_LOGS_MAX_QUERIES_RUN_DEFAULT = "5";
	
	public static final String HISTORICAL_LOGS_MAX_DAYS_SEARCH_PARAM_NAME = "historical_logs_max_days_search";
	public static final String HISTORICAL_LOGS_MAX_DAYS_SEARCH_DEFAULT = "365";
	
 }

