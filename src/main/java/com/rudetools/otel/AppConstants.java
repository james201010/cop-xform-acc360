/**
 * 
 */
package com.rudetools.otel;

/**
 * @author James Schneider
 *
 */
public interface AppConstants {

	public static final String SERVICE_CONF_KEY = "config.file.path";
	public static final String ATTR_TELEM_SDK_NAME = "telemetry.sdk.name";
	
	public static final String TAG_BEGIN = "${";
	public static final String TAG_END = "}";
	
	
	public static final String TAG_SOLUTION_NAME = "SOLUTION_NAME";
	public static final String TAG_METRICS = "METRICS";
	public static final String TAG_METRIC_NAME = "METRIC_NAME";
	public static final String TAG_METRIC_DATAPOINTS = "DATA_POINTS";
	public static final String TAG_METRIC_DP_ATTRS = "METRIC_DP_ATTRS";
	public static final String TAG_METRIC_VALUE = "METRIC_VALUE";
	public static final String TAG_METRIC_ATTR_NAME = "METRIC_ATTR_NAME";
	public static final String TAG_METRIC_ATTR_VAL = "METRIC_ATTR_VAL";
	public static final String TAG_METRIC_TYPE = "METRIC_TYPE";
	public static final String TAG_METRIC_NUMERIC_TYPE = "METRIC_NUMERIC_TYPE";
	
	public static final String TAG_METRIC_TYPE_GAUGE = "gauge";
	public static final String TAG_METRIC_TYPE_AS_INT = "asInt";
	public static final String TAG_METRIC_TYPE_AS_DBL = "asDouble";
	
	
	public static final String START_TIME_UNIX_NANO = "START_TIME_UNIX_NANO";
	public static final String TIME_UNIX_NANO = "TIME_UNIX_NANO";

		
	
}
