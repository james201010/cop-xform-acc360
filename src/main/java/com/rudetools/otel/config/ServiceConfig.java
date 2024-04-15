/**
 * 
 */
package com.rudetools.otel.config;

import java.util.List;


/**
 * @author james101
 *
 */
public class ServiceConfig {

	private String copSolutionName;
	private int firstMonthOfFirstQuarter;
	String clientId;
	private String otelCollectorHttpProtoMetricsEndpoint;
	private String otelCollectorHttpProtoLogsEndpoint;
	private List<XFormConfig> xforms;
	
	
	/**
	 * 
	 */
	public ServiceConfig() {
		
	}


	public String getCopSolutionName() {
		return copSolutionName;
	}


	public void setCopSolutionName(String copSolutionName) {
		this.copSolutionName = copSolutionName;
	}


	public int getFirstMonthOfFirstQuarter() {
		return firstMonthOfFirstQuarter;
	}


	public void setFirstMonthOfFirstQuarter(int firstMonthOfFirstQuarter) {
		this.firstMonthOfFirstQuarter = firstMonthOfFirstQuarter;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getOtelCollectorHttpProtoMetricsEndpoint() {
		return otelCollectorHttpProtoMetricsEndpoint;
	}


	public void setOtelCollectorHttpProtoMetricsEndpoint(String otelCollectorHttpProtoMetricsEndpoint) {
		this.otelCollectorHttpProtoMetricsEndpoint = otelCollectorHttpProtoMetricsEndpoint;
	}


	public String getOtelCollectorHttpProtoLogsEndpoint() {
		return otelCollectorHttpProtoLogsEndpoint;
	}


	public void setOtelCollectorHttpProtoLogsEndpoint(String otelCollectorHttpProtoLogsEndpoint) {
		this.otelCollectorHttpProtoLogsEndpoint = otelCollectorHttpProtoLogsEndpoint;
	}


	public List<XFormConfig> getXforms() {
		return xforms;
	}


	public void setXforms(List<XFormConfig> xforms) {
		this.xforms = xforms;
	}

}
