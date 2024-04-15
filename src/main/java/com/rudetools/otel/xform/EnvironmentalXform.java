/**
 * 
 */
package com.rudetools.otel.xform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.AppConstants;
import com.rudetools.otel.ApplicationCtx;
import com.rudetools.otel.beans.Environmental;
import com.rudetools.otel.beans.EnvironmentalSummary;
import com.rudetools.otel.beans.Metric;
import com.rudetools.otel.config.ServiceConfig;
import com.rudetools.otel.config.TemplateConfig;
import com.rudetools.otel.config.XFormConfig;
import com.rudetools.otel.utils.StringUtils;

/**
 * @author james101
 *
 */
public class EnvironmentalXform implements XForm2OtlpJsonProto, AppConstants {

	private final static Logger logger = LoggerFactory.getLogger(EnvironmentalXform.class);
	
	private static final String ENTITY_ENV = "environmental";
	private static final String ENTITY_ENV_SUMM_QTR = "environ_summary_qtr";
	private static final String ENTITY_ENV_SUMM_YR = "environ_summary_yr";
	
	// 15 attributes, some have different names on input versus output
	private static final String ATTR_TIK_OUT_NUMBER = "ticket_number";
	private static final String ATTR_TIK_OUT_TRVL_QTR = "traveled_quarter";
	private static final String ATTR_TIK_OUT_TRVL_YEAR = "traveled_year";
	private static final String ATTR_TIK_OUT_EMISSION_TYPE = "emission_type";
	private static final String ATTR_TIK_OUT_AIR_CARRIER = "air_carrier";
	private static final String ATTR_TIK_OUT_AIR_CABIN_CLS = "air_cabin_class";
	private static final String ATTR_TIK_OUT_AIR_ADVCD_BKNG = "air_advanced_booking";
	private static final String ATTR_TIK_OUT_AIR_TRIP_TYPE = "trip_type";
	private static final String ATTR_TIK_OUT_DEST_COUNTRY = "destination_country";
	private static final String ATTR_TIK_OUT_DEST_CITY = "destination_city";
	private static final String ATTR_TIK_OUT_ORGIN_COUNTRY = "origin_country";
	private static final String ATTR_TIK_OUT_ORGIN_CITY = "orgin_city";
	private static final String ATTR_TIK_OUT_ROUTG_TYPE = "routing_type";
	private static final String ATTR_TIK_OUT_MARKET = "market";
	private static final String ATTR_TIK_OUT_TOTAL_EMISSIONS = "total_emissions";
	private static final String ATTR_TIK_OUT_TOTAL_CRBN_FEE = "total_carbon_fee";
	private static final String ATTR_TIK_OUT_CURRENCY_SYMBOL = "currency_symbol";
	
	private static final String ATTR_TIK_IN_NUMBER = "Ticket_Number";
	private static final String ATTR_TIK_IN_TRVL_QTR = "Traveled_Quarter";
	private static final String ATTR_TIK_IN_TRVL_YEAR = "Traveled__Year";
	private static final String ATTR_TIK_IN_EMISSION_TYPE = "Emission_Type";
	private static final String ATTR_TIK_IN_AIR_CARRIER = "Air-Carrier";
	private static final String ATTR_TIK_IN_AIR_CABIN_CLS = "Air-Cabin_Class";
	private static final String ATTR_TIK_IN_AIR_ADVCD_BKNG = "Air-Advanced_Booking";
	private static final String ATTR_TIK_IN_AIR_TRIP_TYPE = "Air-Trip_Type";
	private static final String ATTR_TIK_IN_DEST_COUNTRY = "Destination_Country";
	private static final String ATTR_TIK_IN_DEST_CITY = "Destination_City";
	private static final String ATTR_TIK_IN_ORGIN_COUNTRY = "Origin_Country";
	private static final String ATTR_TIK_IN_ORGIN_CITY = "Orgin_City";
	private static final String ATTR_TIK_IN_ROUTG_TYPE = "Routing_Type";
	private static final String ATTR_TIK_IN_MARKET = "WBS_Market";
	private static final String ATTR_TIK_IN_TOTAL_EMISSIONS = "Total_Emissions";
	private static final String ATTR_TIK_IN_TOTAL_CRBN_FEE = "Total_Carbon_Fee";
	
	private static final String MET_EMSNS_MARKET = "emissions_by_market";
	private static final String MET_EMSNS_TRIP_TYPE = "emissions_by_trip_type";
	private static final String MET_EMSNS_TYPE = "emissions_by_type";
	private static final String MET_EMSNS_TOTAL = "emissions_total";
	
	
	
	/**
	 * 
	 */
	public EnvironmentalXform() {
		
	}

	@Override
	public Map<String, String[]> xform(ServiceConfig srvcConf, XFormConfig xformConf, String data2Xform) throws Throwable {
		
		Map<String, String[]> payloadMap = new HashMap<String, String[]>();
		
		List<Environmental> envs = xformInEnvironmental(data2Xform);
		
		
		List<EnvironmentalSummary> summsYr = createSummariesforYear(envs);
		
		String[] outSummsYr = xformOutSummaries(srvcConf, xformConf, summsYr, false);
	
		payloadMap.put("summaries_yr", outSummsYr);
		
		
		List<EnvironmentalSummary> summsQtr = createSummariesforQuarter(envs);
		
		String[] outSummsQtr = xformOutSummaries(srvcConf, xformConf, summsQtr, true);
	
		payloadMap.put("summaries_qtr", outSummsQtr);
		
		
		Thread.currentThread().sleep(10000);
		
		
		String[] outEnvs = xformOutEnvironmental(srvcConf, xformConf, envs);
		
		payloadMap.put("environ", outEnvs);
		
		return payloadMap;

	}

	private static String[] xformOutSummaries(ServiceConfig srvcConf, XFormConfig xformConf, List<EnvironmentalSummary> summs, boolean byQtr) throws Throwable {
		
		List<TemplateConfig> tmplts = xformConf.getTemplates();
		TemplateConfig tmpltSum = null;
		TemplateConfig tmpltMetric = null;
		TemplateConfig tmpltDatapoint = null;
		TemplateConfig tmpltDpAttr = null;
		
		for (TemplateConfig xConfig : tmplts) {
			if (xConfig.getTemplateName().equals("environ_summary_qtr")) {
				if (byQtr) {
					tmpltSum = xConfig;
				}
			} else if (xConfig.getTemplateName().equals("environ_summary_yr")) {
				if (!byQtr) {
					tmpltSum = xConfig;
				}		
			} else if (xConfig.getTemplateName().equals("metric")) {
				tmpltMetric = xConfig;
				
			} else if (xConfig.getTemplateName().equals("datapoint")) {
				tmpltDatapoint = xConfig;		
				
			} else if (xConfig.getTemplateName().equals("dp_attribute")) {
				tmpltDpAttr = xConfig;								
			}
		}
		
		String[] result = new String[summs.size()];
		
		String entityName;
		if (byQtr) {
			entityName = new String(ENTITY_ENV_SUMM_QTR);
		} else {
			entityName = new String(ENTITY_ENV_SUMM_YR);
		}
		
		for (int i = 0; i < result.length; i++) {
			
			// get the top level template file for one summary payload
			String sumTmpltStr = StringUtils.getFileAsString(tmpltSum.getTemplateFilePath());
			EnvironmentalSummary obj = summs.get(i);
			
			// fill in the attributes for one summary
			sumTmpltStr = StringUtils.replaceAll(sumTmpltStr, TAG_BEGIN + TAG_SOLUTION_NAME + TAG_END, ApplicationCtx.SOLUTION_NAME);
			//logger.info(TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_TRVL_YEAR + TAG_END);
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_TRVL_YEAR + TAG_END, obj.getTravelYear());
			//logger.info(TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_TRVL_YEAR + TAG_END);
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_EMISSION_TYPE + TAG_END, obj.getEmissionType());
			//logger.info(TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_EMISSION_TYPE + TAG_END);
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_TRVL_QTR + TAG_END, obj.getTravelQuarter());
			//logger.info(TAG_BEGIN + entityName + "." + ATTR_TIK_OUT_TRVL_QTR + TAG_END);
		
			// get the list of metric definitions for this pred_summary
			Map<String, List<Metric>> metMap = obj.getMetricsMap();
			if (metMap != null && metMap.size() > 0) {
				
				StringBuffer metBuff = new StringBuffer();
				
				// process the list of metric definitions
				for (String metName : metMap.keySet()) {
					
					// get the template file for a metric definition
					String metTmpltStr = StringUtils.getFileAsString(tmpltMetric.getTemplateFilePath());
					
					List<Metric> mets = metMap.get(metName);
					
					// fill in details for a metric definition
					metTmpltStr = StringUtils.replaceAll(metTmpltStr, TAG_BEGIN + TAG_SOLUTION_NAME + TAG_END, ApplicationCtx.SOLUTION_NAME);
					metTmpltStr = StringUtils.replaceAll(metTmpltStr, TAG_BEGIN + TAG_METRIC_NAME + TAG_END, mets.get(0).getMetricName());
					metTmpltStr = StringUtils.replaceAll(metTmpltStr, TAG_BEGIN + TAG_METRIC_TYPE + TAG_END, mets.get(0).getMetricType());
					
					StringBuffer dpBuff = new StringBuffer();
					
					for (Metric met : mets) {
						// get the template file for a metric data point
						String dpTmpltStr = StringUtils.getFileAsString(tmpltDatapoint.getTemplateFilePath());
						
						// fill in details for a metric data point
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TAG_METRIC_NUMERIC_TYPE + TAG_END, TAG_METRIC_TYPE_AS_DBL);
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TAG_METRIC_VALUE + TAG_END, met.getDoubleValue() + "");
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TIME_UNIX_NANO + TAG_END, met.getTimeUnixNano() + "");
						
						StringBuffer dpAttrsBuff = new StringBuffer();
						
						if (met.getAttributes() != null && met.getAttributes().size() > 0) {
							for (String attrName : met.getAttributes().keySet()) {
								
								// get the template file for the data point attribute
								String dpAttrTmpltStr = StringUtils.getFileAsString(tmpltDpAttr.getTemplateFilePath());
								
								// fill in details for the metric data point attribute
								dpAttrTmpltStr = StringUtils.replaceAll(dpAttrTmpltStr, TAG_BEGIN + TAG_METRIC_ATTR_NAME + TAG_END, attrName);
								dpAttrTmpltStr = StringUtils.replaceAll(dpAttrTmpltStr, TAG_BEGIN + TAG_METRIC_ATTR_VAL + TAG_END, met.getAttributes().get(attrName));
								dpAttrsBuff.append(dpAttrTmpltStr);
							}
						}

						
						String dpAttrs = "";
						
						if (met.getAttributes() != null && met.getAttributes().size() > 0) {
							// take off the last comma here
							dpAttrs = dpAttrsBuff.toString().substring(0, dpAttrsBuff.length()-1);
							//logger.info("Datapoint Attributes = " + dpAttrs);	
						}
						
						// put attributes in the data point
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TAG_METRIC_DP_ATTRS + TAG_END, dpAttrs);
						dpBuff.append(dpTmpltStr);
						
					}
					
					// take off the last comma here
					String dataPoints = dpBuff.toString().substring(0, dpBuff.length()-1);	
					//logger.info("Datapoints = " + dataPoints);
					
					// put data points in the metric definition
					metTmpltStr = StringUtils.replaceAll(metTmpltStr, TAG_BEGIN + TAG_METRIC_DATAPOINTS + TAG_END, dataPoints);
					metBuff.append(metTmpltStr);
					
				}
				
				// take off the last comma here
				String metricDefs = metBuff.toString().substring(0, metBuff.length()-1);
				//logger.info("Metric Defs = " + metricDefs);
								
				// put the metric definitions in the pred_summary payload
				sumTmpltStr = StringUtils.replaceAll(sumTmpltStr, TAG_BEGIN + TAG_METRICS + TAG_END, metricDefs);
				
				result[i] = new String(sumTmpltStr);
				
			}
			

		}
		// if some summaries had no metrics then get the count of the ones that did have metrics
		int cntr = 0;
		for (int j = 0; j < result.length; j++) {
			if (result[j] != null && !result[j].equals("")) {
				cntr++;
			}
		}
		
		// create a new array that may be smaller than the original array if some summaries had no metrics
		String[] res = new String[cntr];
		int outCntr = 0;
		for (int k = 0; k < result.length; k++) {
			if (result[k] != null && !result[k].equals("")) {
				res[outCntr] = result[k];				
				//logger.info("");
				//logger.info("");
				//logger.info("Out Summary Payload #" + outCntr + " = ");
				//logger.info(res[outCntr]);
				//logger.info("");
				//logger.info("");
				outCntr++;
			}
			
		}
		
		return res;
		
	}

	
	// Here we create a new summary object for each quarter+year from the list of envs
	// and add the appropriate envs to the appropriate summary
	private static List<EnvironmentalSummary> createSummariesforQuarter(List<Environmental> envs) throws Throwable {
		
		Map<String, EnvironmentalSummary> summs = new HashMap<String, EnvironmentalSummary>();
		EnvironmentalSummary summ = null;
		
		for (Environmental obj : envs) {
			
			if (!summs.containsKey(obj.getTravelYear() + ":" + obj.getTravelQuarter())) {
				//logger.info(obj.getTravelYear() + " : Adding Year");
				summ = new EnvironmentalSummary();
				summ.setTravelYear(obj.getTravelYear());
				summ.setEmissionType(obj.getEmissionType());
				summ.setTravelQuarter(obj.getTravelQuarter());
				summ.setIsByQuarter(true);
				summs.put(obj.getTravelYear() + ":" + obj.getTravelQuarter(), summ);
			} else {
				//logger.info(obj.getTravelYear() + " : Found Year");
				summ = summs.get(obj.getTravelYear() + ":" + obj.getTravelQuarter());
			}
			summ.addEnvironmental(obj);
		}
		
		long timeStamp = ApplicationCtx.getTimeInNanos();
		for(String key : summs.keySet()) {
			logger.info(summs.get(key).getTravelQuarter() + " : Environs = " + summs.get(key).getEnvironmentals().size());
			createEmissionMetricsForSummary(summs.get(key), timeStamp);
			//logger.info("Summary by Quarter = " + key);
			//logger.info("");
		}
		
		logger.info("createSummariesforQuarter count = " + summs.size());
		return new ArrayList<EnvironmentalSummary>(summs.values());
		
	}

	
	// Here we create a new summary object for each year from the list of envs
	// and add the appropriate envs to the appropriate summary
	private static List<EnvironmentalSummary> createSummariesforYear(List<Environmental> envs) throws Throwable {
		
		Map<String, EnvironmentalSummary> summs = new HashMap<String, EnvironmentalSummary>();
		EnvironmentalSummary summ = null;
		
		for (Environmental obj : envs) {
			
			if (!summs.containsKey(obj.getTravelYear())) {
				//logger.info(obj.getTravelYear() + " : Adding Year");
				summ = new EnvironmentalSummary();
				summ.setTravelYear(obj.getTravelYear());
				summ.setEmissionType(obj.getEmissionType());
				summ.setIsByQuarter(false);
				summs.put(obj.getTravelYear(), summ);
			} else {
				//logger.info(obj.getTravelYear() + " : Found Year");
				summ = summs.get(obj.getTravelYear());
			}
			summ.addEnvironmental(obj);
		}
		
		long timeStamp = ApplicationCtx.getTimeInNanos();
		for(String key : summs.keySet()) {
			logger.info(summs.get(key).getTravelYear() + " : Environs = " + summs.get(key).getEnvironmentals().size());
			createEmissionMetricsForSummary(summs.get(key), timeStamp);
			//logger.info("Summary by Year = " + key);
			//logger.info("");
		}
		
		logger.info("createSummariesforYear count = " + summs.size());
		
		return new ArrayList<EnvironmentalSummary>(summs.values());
		
	}
	
	
	private static void createEmissionMetricsForSummary(EnvironmentalSummary summ, long timeStamp) throws Throwable {

		List<String> groups = new ArrayList<String>();
		
		// create emission metrics for market
		for (Environmental obj : summ.getEnvironmentals()) {
			if (!groups.contains(obj.getMarket())) {
				groups.add(obj.getMarket());
			}
		}
		for (String key : groups) {
			//logger.info("Creating emission metric for : " + key);
			deriveEmissionByMarketMetric(summ, timeStamp, MET_EMSNS_MARKET, MET_EMSNS_MARKET, TAG_METRIC_TYPE_GAUGE, key, key);
		}
		
		
		// create emission metrics for trip type
		groups = new ArrayList<String>();		
		for (Environmental obj : summ.getEnvironmentals()) {
			if (!groups.contains(obj.getAirTripType())) {
				groups.add(obj.getAirTripType());
			}
		}
		for (String key : groups) {
			//logger.info("Creating emission metric for : " + key);
			deriveEmissionByTripTypeMetric(summ, timeStamp, MET_EMSNS_TRIP_TYPE, MET_EMSNS_TRIP_TYPE, TAG_METRIC_TYPE_GAUGE, key, key);
		}
		
		
		// create emission metrics for emission type
		groups = new ArrayList<String>();		
		for (Environmental obj : summ.getEnvironmentals()) {
			if (!groups.contains(obj.getEmissionType()  )) {
				groups.add(obj.getEmissionType());
			}
		}
		for (String key : groups) {
			//logger.info("Creating emission metric for : " + key);
			deriveEmissionByTypeMetric(summ, timeStamp, MET_EMSNS_TYPE, MET_EMSNS_TYPE, TAG_METRIC_TYPE_GAUGE, key, key);
		}

		
		// create emission metrics for total of all envs
		deriveEmissionByTotalMetric(summ, timeStamp, MET_EMSNS_TOTAL, MET_EMSNS_TOTAL, TAG_METRIC_TYPE_GAUGE);
		
		
	}
	
	private static void deriveEmissionByTotalMetric(EnvironmentalSummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType) throws Throwable {
		
		Metric met = null;
		double metVal;
		
		metVal = calcEmissionByTotalForSummary(summ);
		//logger.info("By Total:            " + summ.getTravelYear() + " : Emission = " + metVal);
		//if (metVal > 0) {
			met = new Metric(false);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setDoubleValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			summ.addMetric(met);			
		//}
	}	
	
	private static double calcEmissionByTotalForSummary(final EnvironmentalSummary summ) throws Throwable {
		double cntr = 0;
		for (Environmental obj : summ.getEnvironmentals()) {
			cntr = cntr + obj.getTotalEmissions();	
		}
		return cntr;
	}	
	
	private static void deriveEmissionByTypeMetric(EnvironmentalSummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String objType, String attrType) throws Throwable {
		
		Metric met = null;
		double metVal;
		
		metVal = calcEmissionByTypeForSummary(summ, objType);
		//logger.info("By Emission Type:            " + summ.getTravelYear() + " : " + attrType + " : Emission = " + metVal);
		//if (metVal > 0) {
			met = new Metric(false);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setDoubleValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("emission_type", attrType);
			summ.addMetric(met);			
		//}
	}	
	
	private static double calcEmissionByTypeForSummary(final EnvironmentalSummary summ, final String type) throws Throwable {
		double cntr = 0;
		for (Environmental obj : summ.getEnvironmentals()) {
			if (obj.getEmissionType().equals(type)) {
				cntr = cntr + obj.getTotalEmissions();				
			}
		}
		return cntr;
	}	
	
	private static void deriveEmissionByTripTypeMetric(EnvironmentalSummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String objTripType, String attrTripType) throws Throwable {
		
		Metric met = null;
		double metVal;
		
		metVal = calcEmissionByTripTypeForSummary(summ, objTripType);
		//logger.info("By Trip Type:            " + summ.getTravelYear() + " : " + attrTripType + " : Emission = " + metVal);
		//if (metVal > 0) {
			met = new Metric(false);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setDoubleValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("trip_type", attrTripType);
			summ.addMetric(met);			
		//}
	}	
	
	private static double calcEmissionByTripTypeForSummary(final EnvironmentalSummary summ, final String tripType) throws Throwable {
		double cntr = 0;
		for (Environmental obj : summ.getEnvironmentals()) {
			if (obj.getAirTripType().equals(tripType)) {
				cntr = cntr + obj.getTotalEmissions();			
			}
		}
		return cntr;
	}	

	
	private static void deriveEmissionByMarketMetric(EnvironmentalSummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String objMarket, String attrMarket) throws Throwable {
		
		Metric met = null;
		double metVal;
		
		metVal = calcEmissionByMarketForSummary(summ, objMarket);
		//logger.info("By Market:            " + summ.getTravelYear() + " : " + attrMarket + " : Emission = " + metVal);
		//if (metVal > 0) {
			met = new Metric(false);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setDoubleValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("market", attrMarket);
			summ.addMetric(met);			
		//}
	}	
	
	private static double calcEmissionByMarketForSummary(final EnvironmentalSummary summ, final String market) throws Throwable {
		double cntr = 0;
		for (Environmental obj : summ.getEnvironmentals()) {
			if (obj.getMarket().equals(market)) {
				cntr = cntr + obj.getTotalEmissions();			
			}
		}
		return cntr;
	}	
	
	private static String[] xformOutEnvironmental(ServiceConfig srvcConf, XFormConfig xformConf, List<Environmental> theList) throws Throwable {
		
		List<TemplateConfig> tmplts = xformConf.getTemplates();
		TemplateConfig tmpltPred = null;
		
		for (TemplateConfig xConfig : tmplts) {
			if (xConfig.getTemplateName().equals("environ")) {
				tmpltPred = xConfig;
				break;
			}
		}
		
		long timeStamp = ApplicationCtx.getTimeInNanos();
		
		String[] result = new String[theList.size()];
		
		for (int i = 0; i < result.length; i++) {
			
			// get the top level template file for one environmental  payload
			String envTmpltStr = StringUtils.getFileAsString(tmpltPred.getTemplateFilePath());
			Environmental obj = theList.get(i);
			
			// fill in the attributes for one pred_summary
			envTmpltStr = StringUtils.replaceAll(envTmpltStr, TAG_BEGIN + TAG_SOLUTION_NAME + TAG_END, ApplicationCtx.SOLUTION_NAME);
						
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_NUMBER + TAG_END, obj.getTicketNumber());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_TRVL_QTR + TAG_END, obj.getTravelQuarter());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_TRVL_YEAR + TAG_END, obj.getTravelYear());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_EMISSION_TYPE + TAG_END, obj.getEmissionType());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_AIR_CARRIER + TAG_END, obj.getAirCarrier());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_AIR_CABIN_CLS + TAG_END, obj.getAirCabinClass());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_AIR_ADVCD_BKNG + TAG_END, obj.getAirAdvancedBooking());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_AIR_TRIP_TYPE + TAG_END, obj.getAirTripType());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_DEST_COUNTRY + TAG_END, obj.getDestCountry());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_DEST_CITY + TAG_END, obj.getDestCity());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_ORGIN_COUNTRY + TAG_END, obj.getOriginCountry());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_ORGIN_CITY + TAG_END, obj.getOriginCity());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_ROUTG_TYPE + TAG_END, obj.getRoutingType());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_MARKET + TAG_END, obj.getMarket());
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_TOTAL_EMISSIONS + TAG_END, obj.getTotalEmissions() + "");
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_TOTAL_CRBN_FEE + TAG_END, obj.getTotalCarbonFee() + "");
			envTmpltStr = StringUtils.replaceFirst(envTmpltStr, TAG_BEGIN + ENTITY_ENV + "." + ATTR_TIK_OUT_CURRENCY_SYMBOL + TAG_END, obj.getCurrencySymbol());
			
			envTmpltStr = StringUtils.replaceAll(envTmpltStr, TAG_BEGIN + TIME_UNIX_NANO + TAG_END, timeStamp + "");
			
			
			result[i] = new String(envTmpltStr);
		}		
		
		
		return result;

	}
	
	
	private static List<Environmental> xformInEnvironmental(String json) throws Throwable {
		
		List<Environmental> theList = new ArrayList<Environmental>();
		
		
		JSONObject jobj = new JSONObject(json);
		
		JSONObject jroot = jobj.getJSONObject("root");
	
		JSONArray jarray = jroot.getJSONArray("row");
		
		for(int arrCntr = 0; arrCntr < jarray.length(); arrCntr++){
			JSONObject jsonObj = jarray.getJSONObject(arrCntr);
			
			Environmental theObj = new Environmental();
			theObj.setTicketNumber(jsonObj.getString(ATTR_TIK_IN_NUMBER).trim());
			theObj.setTravelQuarter(jsonObj.getString(ATTR_TIK_IN_TRVL_QTR).trim());
			theObj.setTravelYear(jsonObj.getString(ATTR_TIK_IN_TRVL_YEAR).trim());
			theObj.setEmissionType(jsonObj.getString(ATTR_TIK_IN_EMISSION_TYPE).trim());
			theObj.setAirCarrier(jsonObj.getString(ATTR_TIK_IN_AIR_CARRIER).trim());
			theObj.setAirCabinClass(jsonObj.getString(ATTR_TIK_IN_AIR_CABIN_CLS).trim());
			theObj.setAirAdvancedBooking(jsonObj.getString(ATTR_TIK_IN_AIR_ADVCD_BKNG).trim());
			theObj.setAirTripType(jsonObj.getString(ATTR_TIK_IN_AIR_TRIP_TYPE).trim());
			theObj.setDestCountry(jsonObj.getString(ATTR_TIK_IN_DEST_COUNTRY).trim());
			theObj.setDestCity(jsonObj.getString(ATTR_TIK_IN_DEST_CITY).trim());
			theObj.setOriginCountry(jsonObj.getString(ATTR_TIK_IN_ORGIN_COUNTRY).trim());
			theObj.setOriginCity(jsonObj.getString(ATTR_TIK_IN_ORGIN_CITY).trim());
			theObj.setRoutingType(jsonObj.getString(ATTR_TIK_IN_ROUTG_TYPE).trim());
			theObj.setMarket(jsonObj.getString(ATTR_TIK_IN_MARKET).trim());
			theObj.setCurrencySymbol(StringUtils.getCurrencySymbol(jsonObj.getString(ATTR_TIK_IN_TOTAL_CRBN_FEE).trim()));
			theObj.setTotalEmissions(StringUtils.fixDouble(jsonObj.getString(ATTR_TIK_IN_TOTAL_EMISSIONS).trim()));
			theObj.setTotalCarbonFee(StringUtils.fixDouble(jsonObj.getString(ATTR_TIK_IN_TOTAL_CRBN_FEE).trim()));
			
			theList.add(theObj);
			
			//if (!attrs.contains(inc.getCategory())) {
				//attrs.add(inc.getCategory());
			//}
			
		}		
		
		logger.info("Number of environs = " + theList.size());
		
		//logger.info("");
		//logger.info("");
		//for(String attrName : attrs) {
			//logger.info("Name = " + attrName);
		//}
		//logger.info("");
		
		logger.info("");
		
		return theList;
	}	
	
}
