/**
 * 
 */
package com.rudetools.otel.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author james101
 *
 */
public class PredictabilitySummary {

	private String predYear;
	private String predQuarter;
	private String yearTypeText;
	private String quarterYearText;
	private long totalIncidents;
	
	private Map<String, List<Metric>> metricsMap;
	
	private List<Predictability> incidents;
	
	
	/**
	 * 
	 */
	public PredictabilitySummary() {
		
	}

	public String getQuarterYearText() {
		return quarterYearText;
	}

	public void setQuarterYearText(String quarterYearText) {
		this.quarterYearText = quarterYearText;
	}

	public String getPredYear() {
		return predYear;
	}

	public void setPredYear(String predYear) {
		this.predYear = predYear;
	}

	public String getPredQuarter() {
		return predQuarter;
	}

	public void setPredQuarter(String predQuarter) {
		this.predQuarter = predQuarter;
	}

	public String getYearTypeText() {
		return yearTypeText;
	}

	public void setYearTypeText(String yearTypeText) {
		this.yearTypeText = yearTypeText;
	}

	public Map<String, List<Metric>> getMetricsMap() {
		return metricsMap;
	}

	public void addMetric(Metric met) {
		if (this.metricsMap == null) {
			this.metricsMap = new HashMap<String, List<Metric>>();
		}
		
		if (!this.metricsMap.containsKey(met.getMetricName())) {
			this.metricsMap.put(met.getMetricName(), new ArrayList<Metric>());
		}
		
		this.metricsMap.get(met.getMetricName()).add(met);
		
	}

	public List<Predictability> getIncidents() {
		return incidents;
	}

	public void setIncidents(List<Predictability> incidents) {
		this.incidents = incidents;
	}	
	
	public void addIncident(Predictability inc) {
		
		if (this.incidents == null) {
			this.incidents = new ArrayList<Predictability>();
		}
		
		this.incidents.add(inc);
	}

	public long getTotalIncidents() {
		return totalIncidents;
	}

	public void setTotalIncidents(long totalIncidents) {
		this.totalIncidents = totalIncidents;
	}	
	
	
	
}
