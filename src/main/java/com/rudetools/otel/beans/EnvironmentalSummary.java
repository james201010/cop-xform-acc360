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
public class EnvironmentalSummary {

	private String travelYear;
	//private String emissionType;
	private long totalTravelRecords;
	private boolean isByQuarter;
	private String travelQuarter = "YEARLY";
	
	private Map<String, List<Metric>> metricsMap;
	private List<Environmental> environmentals;
	
	/**
	 * 
	 */
	public EnvironmentalSummary() {
		
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


	public String getTravelQuarter() {
		return travelQuarter;
	}


	public void setTravelQuarter(String travelQuarter) {
		this.travelQuarter = travelQuarter;
	}


	public String getTravelYear() {
		return travelYear;
	}


	public void setTravelYear(String travelYear) {
		this.travelYear = travelYear;
	}


//	public String getEmissionType() {
//		return emissionType;
//	}
//
//
//	public void setEmissionType(String emissionType) {
//		this.emissionType = emissionType;
//	}


	public long getTotalTravelRecords() {
		return totalTravelRecords;
	}


	public void setTotalTravelRecords(long totalTravelRecords) {
		this.totalTravelRecords = totalTravelRecords;
	}


	public boolean isByQuarter() {
		return isByQuarter;
	}


	public void setIsByQuarter(boolean isByQuarter) {
		this.isByQuarter = isByQuarter;
	}


	public List<Environmental> getEnvironmentals() {
		return environmentals;
	}


	public void setEnvironmentals(List<Environmental> environmentals) {
		this.environmentals = environmentals;
	}

	public void addEnvironmental(Environmental env) {
		
		if (this.environmentals == null) {
			this.environmentals = new ArrayList<Environmental>();
		}
		
		this.environmentals.add(env);
	}		
	
}
