/**
 * 
 */
package com.rudetools.otel.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * @author james101
 *
 */
public class Metric {
	
	
	private String metricName;  // the actual name of the metric in the cop topology definition
	private String metricTagName;  // the name of the tag within the templateFile that matches the placeholder for the value of the metric ( without the ${} )
	private String metricType;
	private long timeUnixNano;
	private long startTimeUnixNano;
	private long longValue;
	private double doubleValue;
	private boolean longVal;
	private Map<String, String> attributes;
	
	
	/**
	 * 
	 */
	public Metric(boolean isLong) {
		this.longVal = isLong;
		
	}
	
	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public void addAttribute(String key, String value) {
		
		if (this.attributes == null) {
			this.attributes = new HashMap<String, String>();
		}
		
		this.attributes.put(key, value);
	}	
	
	public Map<String, String> getAttributes() {
		return attributes;
	}


	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}


	public String getMetricName() {
		return metricName;
	}


	public void setMetricName(String metric_name) {
		this.metricName = metric_name;
	}


	public String getMetricTagName() {
		return metricTagName;
	}

	public void setMetricTagName(String metricTagName) {
		this.metricTagName = metricTagName;
	}

	public long getTimeUnixNano() {
		return timeUnixNano;
	}


	public void setTimeUnixNano(long time_unix_nano) {
		this.timeUnixNano = time_unix_nano;
	}


	public long getStartTimeUnixNano() {
		return startTimeUnixNano;
	}


	public void setStartTimeUnixNano(long startTimeUnixNano) {
		this.startTimeUnixNano = startTimeUnixNano;
	}


	public long getLongValue() {
		return longValue;
	}


	public void setLongValue(long long_value) {
		this.longValue = long_value;
	}


	public double getDoubleValue() {
		return doubleValue;
	}


	public void setDoubleValue(double double_value) {
		this.doubleValue = double_value;
	}


	public boolean isLongVal() {
		return longVal;
	}


	public void setIsLongVal(boolean longVal) {
		this.longVal = longVal;
	}






	
}
