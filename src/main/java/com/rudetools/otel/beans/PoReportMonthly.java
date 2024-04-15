/**
 * 
 */
package com.rudetools.otel.beans;

/**
 * @author james101
 *
 */
public class PoReportMonthly {

	private String solutionName;
	private String entityName;
	private String por_identifier;
	private String por_month;
	private String por_year;
	private String telemetry_sdk_name;
	private String scope_name;
	private String scope_version;
	
	private long por_total_count;
	private long por_total_sales;
	private long so_total_count;
	private long so_total_sales;	
	
	private Metric total_po_report_count;
	private Metric total_so_count;
	
	private Metric total_po_report_sales;
	private Metric total_so_sales;
	
	
	
	/**
	 * 
	 */
	public PoReportMonthly() {
		
	}



	public String getPorIdentifier() {
		return por_identifier;
	}



	public void setPorIdentifier(String por_identifier) {
		this.por_identifier = por_identifier;
	}



	public String getPorMonth() {
		return por_month;
	}



	public void setPorMonth(String por_month) {
		this.por_month = por_month;
	}



	public String getPorYear() {
		return por_year;
	}



	public void setPorYear(String por_year) {
		this.por_year = por_year;
	}



	public String getTelemetrySdkName() {
		return telemetry_sdk_name;
	}



	public void setTelemetrySdkName(String telemetry_sdk_name) {
		this.telemetry_sdk_name = telemetry_sdk_name;
	}



	public Metric getMetricTotalPoReportCount() {
		return total_po_report_count;
	}



	public long getPorTotalCount() {
		return por_total_count;
	}



	public void setPorTotalCount(long por_total_count) {
		this.por_total_count = por_total_count;
	}



	public long getPorTotalSales() {
		return por_total_sales;
	}



	public void setPorTotalSales(long por_total_sales) {
		this.por_total_sales = por_total_sales;
	}



	public long getSoTotalCount() {
		return so_total_count;
	}



	public void setSoTotalCount(long so_total_count) {
		this.so_total_count = so_total_count;
	}



	public long getSoTotalSales() {
		return so_total_sales;
	}



	public void setSoTotalSales(long so_total_sales) {
		this.so_total_sales = so_total_sales;
	}



	public void setTotalPoReportCount(Metric total_po_report_count) {
		this.total_po_report_count = total_po_report_count;
	}



	public Metric getMetricTotalSoCount() {
		return total_so_count;
	}



	public void setTotalSoCount(Metric total_so_count) {
		this.total_so_count = total_so_count;
	}



	public Metric getMetricTotalPoReportSales() {
		return total_po_report_sales;
	}



	public void setTotalPoReportSales(Metric total_po_report_sales) {
		this.total_po_report_sales = total_po_report_sales;
	}



	public Metric getMetricTotalSoSales() {
		return total_so_sales;
	}



	public void setTotalSoSales(Metric total_so_sales) {
		this.total_so_sales = total_so_sales;
	}



	public String getScopeName() {
		return scope_name;
	}



	public void setScopeName(String scope_name) {
		this.scope_name = scope_name;
	}



	public String getScopeVersion() {
		return scope_version;
	}



	public void setScopeVersion(String scope_version) {
		this.scope_version = scope_version;
	}



	public String getSolutionName() {
		return solutionName;
	}



	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}



	public String getEntityName() {
		return entityName;
	}



	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	
	

}
