/**
 * 
 */
package com.rudetools.otel.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

import com.rudetools.otel.AppConstants;


/**
 * @author James Schneider
 *
 */
public class JsonHelper implements AppConstants {

	/**
	 * 
	 */
	public JsonHelper() {
		
	}

	public static String getJsonFromHttpResponse(HttpResponse response) throws Throwable {
		String resp = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }			
		
        resp = out.toString();
		reader.close();
		return resp;
	}
	
//	public static PoReportMonthly xformPoMonthlyReport(String json) throws Throwable {
//		
//		PoReportMonthly porep = new PoReportMonthly();
//		
//		JSONObject jobj = new JSONObject(json);
//		
//		JSONArray jarray = jobj.getJSONArray("resource_metrics");
//		
//		JSONObject both = jarray.getJSONObject(0);
//		
//		JSONArray resourceAttribs = both.getJSONObject("resource").getJSONArray("attributes");
//		
//		
//		for (int jArrayPos = 0; jArrayPos < resourceAttribs.length(); jArrayPos++) {
//			
//			
//			JSONObject attrib = resourceAttribs.getJSONObject(jArrayPos);
//			
//			String key = attrib.getString("key");
//			
//			if (key.equals(ATTR_POREPORT_POR_ID)) {
//				
//				String por_id = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
//				System.out.println("por_id = " + por_id);
//				porep.setPorIdentifier(por_id);
//				
//			} else if (key.equals(ATTR_POREPORT_POR_MONTH)) {
//				
//				String por_month = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
//				System.out.println("por_month = " + por_month);
//				porep.setPorMonth(por_month);
//				
//			} else if (key.equals(ATTR_POREPORT_POR_YEAR)) {
//				
//				String por_year = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
//				System.out.println("por_year = " + por_year);
//				porep.setPorYear(por_year);
//
//			} else if (key.equals(ATTR_POREPORT_TELEM_SDK_NAME)) {
//				
//				String telsdk = attrib.getJSONObject("value").getJSONObject("Value").getString("StringValue");
//				System.out.println("telsdk = " + telsdk);
//				porep.setTelemetrySdkName(telsdk);
//				
//			}
//		
//		}
//		
//		JSONArray scopeMetrics = both.getJSONArray("scope_metrics");
//		
//		JSONObject scopeo = scopeMetrics.getJSONObject(0).getJSONObject("scope");
//		
//		String scopeName = scopeo.getString("name");
//		System.out.println("scopeName = " + scopeName);
//		porep.setScopeName(scopeName);
//		
//		String scopeVer = scopeo.getString("version");
//		System.out.println("scopeVer = " + scopeVer);
//		porep.setScopeVersion(scopeVer);
//		
//		
//		JSONArray metrics = scopeMetrics.getJSONObject(0).getJSONArray("metrics");
//		
//		JSONObject metrico = null;
//		
//		for (int jArrayPos = 0; jArrayPos < metrics.length(); jArrayPos++) {
//			
//			JSONObject metric = metrics.getJSONObject(jArrayPos);
//			
//			String mname = metric.getString("name");
//			
//			metrico = metric.getJSONObject("Data").getJSONObject("Gauge").getJSONArray("data_points").getJSONObject(0);
//			
//			if (mname.equals(METRIC_POREPORT_POR_COUNT)) {
//				
//				Metric m1 = new Metric();
//				m1.setMetric_name(METRIC_POREPORT_POR_COUNT);
//				m1.setTime_unix_nano(metrico.getLong("time_unix_nano"));
//				m1.setLong_value(metrico.getJSONObject("Value").getInt("AsInt"));
//				porep.setTotalPoReportCount(m1);
//				porep.setPorTotalCount(m1.getLong_value());
//				System.out.println("Metric Name = " + m1.getMetric_name());
//				System.out.println("Metric Time = " + m1.getTime_unix_nano());
//				System.out.println("Metric Value = " + m1.getLong_value());
//				
//			} else if (mname.equals(METRIC_POREPORT_POR_SALES)) { 
//				
//				Metric m1 = new Metric();
//				m1.setMetric_name(METRIC_POREPORT_POR_SALES);
//				m1.setTime_unix_nano(metrico.getLong("time_unix_nano"));
//				Double dbl1 = metrico.getJSONObject("Value").getDouble("AsInt");
//				String str1 = dbl1.toString();
//				long tmpL = Long.parseLong(StringUtils.split(str1, ".")[0]);
//				//long tmpL = Double.doubleToLongBits(metrico.getJSONObject("Value").getDouble("AsInt"));
//				m1.setLong_value(tmpL);
//				porep.setTotalPoReportSales(m1);
//				porep.setPorTotalSales(m1.getLong_value());
//				System.out.println("Metric Name = " + m1.getMetric_name());
//				System.out.println("Metric Time = " + m1.getTime_unix_nano());
//				System.out.println("Metric Value = " + m1.getLong_value());
//				
//			} else if (mname.equals(METRIC_POREPORT_SO_COUNT)) { 
//				
//				Metric m1 = new Metric();
//				m1.setMetric_name(METRIC_POREPORT_SO_COUNT);
//				m1.setTime_unix_nano(metrico.getLong("time_unix_nano"));
//				m1.setLong_value(metrico.getJSONObject("Value").getInt("AsInt"));
//				porep.setTotalSoCount(m1);
//				porep.setSoTotalCount(m1.getLong_value());
//				System.out.println("Total SO Count = " + porep.getSoTotalCount());
//				System.out.println("Metric Name = " + m1.getMetric_name());
//				System.out.println("Metric Time = " + m1.getTime_unix_nano());
//				System.out.println("Metric Value = " + m1.getLong_value());
//				
//			} else if (mname.equals(METRIC_POREPORT_SO_SALES)) { 
//				
//				Metric m1 = new Metric();
//				m1.setMetric_name(METRIC_POREPORT_SO_SALES);
//				m1.setTime_unix_nano(metrico.getLong("time_unix_nano"));
//				Double dbl2 = metrico.getJSONObject("Value").getDouble("AsInt");
//				String str2 = dbl2.toString();
//				long tmpL2 = Long.parseLong(StringUtils.split(str2, ".")[0]);
//
//				//long tmpL2 = Long.parseLong(dbl2.toString());
//				//long tmpL2 = Double.doubleToLongBits(metrico.getJSONObject("Value").getDouble("AsInt"));
//				m1.setLong_value(tmpL2);
//				porep.setTotalSoSales(m1);
//				porep.setSoTotalSales(m1.getLong_value());
//				System.out.println("Total SO Sales = " + porep.getSoTotalSales());
//				System.out.println("Metric Name = " + m1.getMetric_name());
//				System.out.println("Metric Time = " + m1.getTime_unix_nano());
//				System.out.println("Metric Value = " + m1.getLong_value());				
//				
//			}
//			
//		
//		}
//		
//		return porep;
//		
//	}
	
}
