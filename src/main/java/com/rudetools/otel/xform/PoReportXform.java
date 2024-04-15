/**
 * 
 */
package com.rudetools.otel.xform;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.AppConstants;
import com.rudetools.otel.ApplicationCtx;
import com.rudetools.otel.beans.Metric;
import com.rudetools.otel.beans.PoReportMonthly;
import com.rudetools.otel.config.ServiceConfig;
import com.rudetools.otel.config.XFormConfig;
import com.rudetools.otel.utils.StringUtils;

/**
 * @author james101
 *
 */
public class PoReportXform implements XForm2OtlpJsonProto, AppConstants {
	
	public final static Logger logger = LoggerFactory.getLogger(PoReportXform.class);

	public static final String ENTITY_NAME = "po_report";
	
	public static final String ATTR_POR_ID = "por_identifier";
	public static final String ATTR_POR_MONTH_YEAR = "por_month_year";
	public static final String ATTR_POR_MONTH = "por_month";
	public static final String ATTR_POR_YEAR = "por_year";
	
	public static final String ATTR_POR_COUNT = "total_count";
	public static final String ATTR_POR_SALES = "total_sales";
	public static final String ATTR_SO_COUNT = "so_total_count";
	public static final String ATTR_SO_SALES = "so_total_sales";
	
	public static final String METRIC_POR_COUNT = "total_po_report_count";
	public static final String METRIC_POR_SALES = "total_po_report_sales";
	public static final String METRIC_SO_COUNT = "total_so_count";
	public static final String METRIC_SO_SALES = "total_so_sales";	
	
	// ${po_report.por_identifier}
	public static final String OUT_ATTR_POR_ID = TAG_BEGIN + ENTITY_NAME + "." + ATTR_POR_ID + TAG_END;
	// ${po_report.por_month_year}
	public static final String OUT_ATTR_POR_MONTH_YEAR = TAG_BEGIN + ENTITY_NAME + "." + ATTR_POR_MONTH_YEAR + TAG_END;
	// ${po_report.por_month}
	public static final String OUT_ATTR_POR_MONTH = TAG_BEGIN + ENTITY_NAME + "." + ATTR_POR_MONTH + TAG_END;
	// ${po_report.por_year}
	public static final String OUT_ATTR_POR_YEAR = TAG_BEGIN + ENTITY_NAME + "." + ATTR_POR_YEAR + TAG_END;
	// ${po_report.total_count}
	public static final String OUT_ATTR_POR_COUNT = TAG_BEGIN + ENTITY_NAME + "." + ATTR_POR_COUNT + TAG_END;
	// ${po_report.total_sales}
	public static final String OUT_ATTR_POR_SALES = TAG_BEGIN + ENTITY_NAME + "." + ATTR_POR_SALES + TAG_END;
	// ${po_report.so_total_count}
	public static final String OUT_ATTR_SO_COUNT = TAG_BEGIN + ENTITY_NAME + "." + ATTR_SO_COUNT + TAG_END;
	// ${po_report.so_total_count}
	public static final String OUT_ATTR_SO_SALES = TAG_BEGIN + ENTITY_NAME + "." + ATTR_SO_SALES + TAG_END;	
		
	// ${total_po_report_count}
	public static final String OUT_METRIC_POR_COUNT = TAG_BEGIN + METRIC_POR_COUNT + TAG_END;
	// ${total_po_report_sales}
	public static final String OUT_METRIC_POR_SALES = TAG_BEGIN + METRIC_POR_SALES + TAG_END;
	// ${total_so_count}
	public static final String OUT_METRIC_SO_COUNT = TAG_BEGIN + METRIC_SO_COUNT + TAG_END;
	// ${total_so_sales}
	public static final String OUT_METRIC_SO_SALES = TAG_BEGIN + METRIC_SO_SALES + TAG_END;

	public static String IN_ATTR_POR_ID = ApplicationCtx.SOLUTION_NAME + "." + ENTITY_NAME + "." + ATTR_POR_ID;
	public static String IN_ATTR_POR_MONTH = ApplicationCtx.SOLUTION_NAME + "." + ENTITY_NAME + "." + ATTR_POR_MONTH;
	public static String IN_ATTR_POR_YEAR = ApplicationCtx.SOLUTION_NAME + "." + ENTITY_NAME + "." + ATTR_POR_YEAR;

	public static String IN_METRIC_POR_COUNT = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_POR_COUNT;
	public static String IN_METRIC_POR_SALES = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_POR_SALES;
	public static String IN_METRIC_SO_COUNT = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_SO_COUNT;
	public static String IN_METRIC_SO_SALES = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_SO_SALES;

	
	/**
	 * 
	 */
	public PoReportXform() {
		
		IN_ATTR_POR_ID = ApplicationCtx.SOLUTION_NAME + "." + ENTITY_NAME + "." + ATTR_POR_ID;
		IN_ATTR_POR_MONTH = ApplicationCtx.SOLUTION_NAME + "." + ENTITY_NAME + "." + ATTR_POR_MONTH;
		IN_ATTR_POR_YEAR = ApplicationCtx.SOLUTION_NAME + "." + ENTITY_NAME + "." + ATTR_POR_YEAR;

		IN_METRIC_POR_COUNT = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_POR_COUNT;
		IN_METRIC_POR_SALES = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_POR_SALES;
		IN_METRIC_SO_COUNT = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_SO_COUNT;
		IN_METRIC_SO_SALES = ApplicationCtx.SOLUTION_NAME + ":" + METRIC_SO_SALES;
		
	}

	@Override
	public Map<String, String[]> xform(ServiceConfig srvcConf, XFormConfig xformConf, String data2Xform) throws Throwable {
		
		Map<String, String[]> payloadMap = new HashMap<String, String[]>();
		
		PoReportMonthly obj = xformInPoMonthlyReport(data2Xform);
		
		String tmplt = StringUtils.getFileAsString(xformConf.getTemplates().get(0).getTemplateFilePath());
		
		tmplt = StringUtils.replaceAll(tmplt, TAG_BEGIN + TAG_SOLUTION_NAME + TAG_END, ApplicationCtx.SOLUTION_NAME);
		
		long timeStamp = ApplicationCtx.getTimeInNanos();
		
		tmplt = StringUtils.replaceAll(tmplt, TAG_BEGIN + TIME_UNIX_NANO + TAG_END, timeStamp + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_POR_ID, obj.getPorIdentifier());
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_POR_MONTH_YEAR, deriveMonthYear(obj.getPorMonth(), obj.getPorYear()));
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_POR_MONTH, obj.getPorMonth());
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_POR_YEAR, obj.getPorYear());
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_POR_COUNT, obj.getPorTotalCount() + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_POR_SALES, obj.getPorTotalSales() + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_SO_COUNT, obj.getSoTotalCount() + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_ATTR_SO_SALES, obj.getSoTotalSales() + "");
		
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_METRIC_POR_COUNT, obj.getPorTotalCount() + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_METRIC_POR_SALES, obj.getPorTotalSales() + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_METRIC_SO_COUNT, obj.getSoTotalCount() + "");
		
		tmplt = StringUtils.replaceFirst(tmplt, OUT_METRIC_SO_SALES, obj.getSoTotalSales() + "");
		
		
		obj = null;
		
		String[] retArr = {tmplt};
		
		payloadMap.put("poreport", retArr);
		
		return payloadMap;
	}

	private static String deriveMonthYear(String month, String year) throws Throwable {
		int m = Integer.parseInt(month);
		int y = Integer.parseInt(year.substring(2));
		
		switch (m) {
		case 1:
			return "Jan-" + y;
		case 2:
			return "Feb-" + y;
		case 3:
			return "Mar-" + y;
		case 4:
			return "Apr-" + y;
		case 5:
			return "May-" + y;
		case 6:
			return "Jun-" + y;
		case 7:
			return "Jul-" + y;
		case 8:
			return "Aug-" + y;
		case 9:
			return "Sep-" + y;
		case 10:
			return "Oct-" + y;
		case 11:
			return "Nov-" + y;
		case 12:
			return "Dec-" + y;

		}
		
		return "Unknown";
	}
	
	public static PoReportMonthly xformInPoMonthlyReport(String json) throws Throwable {
		
		PoReportMonthly porep = new PoReportMonthly();
		
		JSONObject jobj = new JSONObject(json);
		
		JSONArray jarray = jobj.getJSONArray("resource_metrics");
		
		JSONObject both = jarray.getJSONObject(0);
		
		JSONArray resourceAttribs = both.getJSONObject("resource").getJSONArray("attributes");
		
		
		for (int jArrayPos = 0; jArrayPos < resourceAttribs.length(); jArrayPos++) {
			
			
			JSONObject attrib = resourceAttribs.getJSONObject(jArrayPos);
			
			String key = attrib.getString("key");
			
			if (key.equals(IN_ATTR_POR_ID)) {
				
				String por_id = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
				System.out.println("por_id = " + por_id);
				porep.setPorIdentifier(por_id);
				
			} else if (key.equals(IN_ATTR_POR_MONTH)) {
				
				String por_month = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
				System.out.println("por_month = " + por_month);
				porep.setPorMonth(por_month);
				
			} else if (key.equals(IN_ATTR_POR_YEAR)) {
				
				String por_year = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
				System.out.println("por_year = " + por_year);
				porep.setPorYear(por_year);

			} else if (key.equals(ATTR_TELEM_SDK_NAME)) {
				
				String telsdk = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
				System.out.println("telsdk = " + telsdk);
				porep.setTelemetrySdkName(telsdk);
				
			}
		
		}
		
		JSONArray scopeMetrics = both.getJSONArray("scope_metrics");
		
		JSONObject scopeo = scopeMetrics.getJSONObject(0).getJSONObject("scope");
		
		String scopeName = scopeo.getString("name");
		System.out.println("scopeName = " + scopeName);
		porep.setScopeName(scopeName);
		
		String scopeVer = scopeo.getString("version");
		System.out.println("scopeVer = " + scopeVer);
		porep.setScopeVersion(scopeVer);
		
		
		JSONArray metrics = scopeMetrics.getJSONObject(0).getJSONArray("metrics");
		
		JSONObject metrico = null;
		
		for (int jArrayPos = 0; jArrayPos < metrics.length(); jArrayPos++) {
			
			JSONObject metric = metrics.getJSONObject(jArrayPos);
			
			String mname = metric.getString("name");
			
			metrico = metric.getJSONObject("Data").getJSONObject("Gauge").getJSONArray("data_points").getJSONObject(0);
			
		
			
			if (mname.equals(IN_METRIC_POR_COUNT)) {
				
				Metric m1 = new Metric(true);
				m1.setMetricName(IN_METRIC_POR_COUNT);
				//m1.setIsLongVal(true);
				m1.setTimeUnixNano(ApplicationCtx.getTimeInNanos());
				m1.setLongValue(metrico.getJSONObject("Value").getInt("AsInt"));
				porep.setTotalPoReportCount(m1);
				porep.setPorTotalCount(m1.getLongValue());
				
			} else if (mname.equals(IN_METRIC_POR_SALES)) { 
				
				Metric m1 = new Metric(true);
				m1.setMetricName(IN_METRIC_POR_SALES);
				//m1.setIsLongVal(true);
				m1.setTimeUnixNano(ApplicationCtx.getTimeInNanos());
				Double dbl1 = metrico.getJSONObject("Value").getDouble("AsInt");
				String str1 = dbl1.toString();
				long tmpL = Long.parseLong(StringUtils.split(str1, ".")[0]);
				m1.setLongValue(tmpL);
				porep.setTotalPoReportSales(m1);
				porep.setPorTotalSales(m1.getLongValue());
				
			} else if (mname.equals(IN_METRIC_SO_COUNT)) { 
				
				Metric m1 = new Metric(true);
				m1.setMetricName(IN_METRIC_SO_COUNT);
				//m1.setIsLongVal(true);
				m1.setTimeUnixNano(ApplicationCtx.getTimeInNanos());
				m1.setLongValue(metrico.getJSONObject("Value").getInt("AsInt"));
				porep.setTotalSoCount(m1);
				porep.setSoTotalCount(m1.getLongValue());
				
			} else if (mname.equals(IN_METRIC_SO_SALES)) { 
				
				Metric m1 = new Metric(true);
				m1.setMetricName(IN_METRIC_SO_SALES);
				//m1.setIsLongVal(true);
				m1.setTimeUnixNano(ApplicationCtx.getTimeInNanos());
				Double dbl2 = metrico.getJSONObject("Value").getDouble("AsInt");
				String str2 = dbl2.toString();
				long tmpL2 = Long.parseLong(StringUtils.split(str2, ".")[0]);
				m1.setLongValue(tmpL2);
				porep.setTotalSoSales(m1);
				porep.setSoTotalSales(m1.getLongValue());				
				
			}
			
		
		}
		
		return porep;
		
	}

}
