/**
 * 
 */
package com.rudetools.otel.xform;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rudetools.otel.AppConstants;
import com.rudetools.otel.ApplicationCtx;
import com.rudetools.otel.beans.Metric;
import com.rudetools.otel.beans.Predictability;
import com.rudetools.otel.beans.PredictabilitySummary;
import com.rudetools.otel.config.ServiceConfig;
import com.rudetools.otel.config.TemplateConfig;
import com.rudetools.otel.config.XFormConfig;
import com.rudetools.otel.utils.DateUtils;
import com.rudetools.otel.utils.StringUtils;

/**
 * @author james101
 *
 */
public class PredictabilityXform implements XForm2OtlpJsonProto, AppConstants {
	
	private final static Logger logger = LoggerFactory.getLogger(PredictabilityXform.class);

	private static final String STATE_NEW = "New";
	private static final String STATE_CLOSED = "Closed";
	private static final String STATE_ON_HOLD = "On Hold";
	private static final String STATE_IN_PROGRESS = "In Progress";
	
	private static final String PRIORITY_1 = "1 - Critical";
	private static final String PRIORITY_2 = "2 - High";
	private static final String PRIORITY_3 = "3 - Moderate";
	private static final String PRIORITY_4 = "4 - Low";
	private static final String PRIORITY_5 = "5 - Planning";

	private static final String ATTR_PRIORITY_1 = "P1-Critical";
	private static final String ATTR_PRIORITY_2 = "P2-High";
	private static final String ATTR_PRIORITY_3 = "P3-Moderate";
	private static final String ATTR_PRIORITY_4 = "P4-Low";
	private static final String ATTR_PRIORITY_5 = "P5-Planning";
	
	private static final String ENTITY_PRED = "predictability";
	private static final String ENTITY_PRED_SUMM = "predict_summary";
	
	private static final String ATTR_INC_NUMBER = "number";
	private static final String ATTR_INC_OPENED_DATE = "opened_at";
	private static final String ATTR_INC_CLOSED_DATE = "closed_at";
	private static final String ATTR_INC_SHORT_DESC = "short_description";
	private static final String ATTR_INC_CALLER_ID = "caller_id";
	private static final String ATTR_INC_PRIORITY = "priority";
	private static final String ATTR_INC_URGENCY = "urgency";
	private static final String ATTR_INC_IMPACT = "impact";
	private static final String ATTR_INC_STATE = "state";
	private static final String ATTR_INC_CATEGORY = "category";
	private static final String ATTR_INC_ASGN_GROUP = "assignment_group";
	private static final String ATTR_INC_ASGN_TO = "assigned_to";
	private static final String ATTR_INC_SYS_UPDT_ON = "sys_updated_on";
	private static final String ATTR_INC_SYS_UPDT_BY = "sys_updated_by";
	private static final String ATTR_INC_SLA_DUE = "sla_due";
	private static final String ATTR_INC_SLA_MADE = "made_sla";
	private static final String ATTR_INC_SYS_CREATED_ON = "sys_created_on";
	private static final String ATTR_INC_DUE_DATE = "due_date";
	private static final String ATTR_INC_TIME_WRKD = "time_worked";
	private static final String ATTR_INC_CAL_STC = "calendar_stc";
	private static final String ATTR_INC_RESOLVE_DATE = "resolved_at";
	
	private static final String ATTR_INC_TT_RESOLVE = "time_to_resolve_hours";
	private static final String ATTR_INC_TT_REACT = "time_to_react_hours";
	private static final String ATTR_INC_YEAR = "pred_year";
	private static final String ATTR_INC_MONTH = "pred_month";
	private static final String ATTR_INC_QUARTER = "pred_quarter";
	
	private static final String ATTR_SUM_QTRYR_TXT = "quarter_year_text";
	private static final String ATTR_SUM_YRTYPE_TXT = "year_type_text";
	private static final String ATTR_SUM_TOTAL_INCS = "total_incidents";
	
	private static final String MET_MTT_REACT = "pred_mtt_react_hrs";
	private static final String MET_MTT_RESOLVE = "pred_mtt_resolve_hrs";
	private static final String MET_CT_ASSGN_GRP = "pred_count_by_assign_group";
	private static final String MET_CT_CATEGORY = "pred_count_by_category";
	private static final String MET_CT_PRIORITY_CATEGORY = "pred_count_by_priority_category";
	private static final String MET_CT_PRIORITY = "pred_count_by_priority";
	private static final String MET_CT_STATE = "pred_count_by_state";

	
	// String month, String quarter
	private static Map<String, String> QTRS_MAP = null;
	
	/**
	 * 
	 */
	public PredictabilityXform() {
		
		// Here we construct a map of each month in the year to the appropriate quarter of the year 
		// based on the 'first month of the first quarter' in the year, defined in the config.yaml file,
		// so that we can support both a calendar year VS a fiscal year per COP tenant
		if (QTRS_MAP == null) {
			
			int qtrMonth = ApplicationCtx.SRVC_CONF.getFirstMonthOfFirstQuarter();
			
			QTRS_MAP = new HashMap<String, String>();
			
			for (int i = 1; i < 5; i++) {
				
				QTRS_MAP.put(qtrMonth + "", i + "");
				if (qtrMonth == 12) { qtrMonth = 1; } else { qtrMonth++; }
				
				QTRS_MAP.put(qtrMonth + "", i + "");
				if (qtrMonth == 12) { qtrMonth = 1; } else { qtrMonth++; }
				
				QTRS_MAP.put(qtrMonth + "", i + "");
				if (qtrMonth == 12) { qtrMonth = 1; } else { qtrMonth++; }
				
				
			}
		}
		
		//Set<String> keys = QTRS_MAP.keySet();
		//for(String key : keys) {
			//logger.info("Month = " + key + " | Qtr = " + QTRS_MAP.get(key));
		//}
		
		
	}

	@Override
	public Map<String, String[]> xform(ServiceConfig srvcConf, XFormConfig xformConf, String data2Xform) throws Throwable {
		
		Map<String, String[]> payloadMap = new HashMap<String, String[]>();
		
		List<Predictability> incs = xformInIncidents(data2Xform);
		
		List<PredictabilitySummary> summs = createSummaries(incs);
		
		String[] outSumms = xformOutSummaries(srvcConf, xformConf, summs);
	
		payloadMap.put("summaries", outSumms);
		
		Thread.currentThread().sleep(10000);
		
		String[] outIncs = xformOutIncidents(srvcConf, xformConf, incs);
		
		payloadMap.put("incidents", outIncs);
		
		return payloadMap;
	}

	private static String[] xformOutIncidents(ServiceConfig srvcConf, XFormConfig xformConf, List<Predictability> theList) throws Throwable {
		
		List<TemplateConfig> tmplts = xformConf.getTemplates();
		TemplateConfig tmpltPred = null;
		
		for (TemplateConfig xConfig : tmplts) {
			if (xConfig.getTemplateName().equals("predict")) {
				tmpltPred = xConfig;
				break;
			}
		}
		
		long timeStamp = ApplicationCtx.getTimeInNanos();
		
		String[] result = new String[theList.size()];
		
		for (int i = 0; i < result.length; i++) {
			
			// get the top level template file for one predictibility/incident  payload
			String incsTmpltStr = StringUtils.getFileAsString(tmpltPred.getTemplateFilePath());
			Predictability obj = theList.get(i);
			
			// fill in the attributes for one pred_summary
			incsTmpltStr = StringUtils.replaceAll(incsTmpltStr, TAG_BEGIN + TAG_SOLUTION_NAME + TAG_END, ApplicationCtx.SOLUTION_NAME);
			

			
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_NUMBER + TAG_END, obj.getIncNumber());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_OPENED_DATE + TAG_END, obj.getOpenedAtDate());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_CLOSED_DATE + TAG_END, obj.getClosedAtDate());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_SHORT_DESC + TAG_END, obj.getShortDescr());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_CALLER_ID + TAG_END, obj.getCallerId());
			
			if (obj.getPriority().equals(PRIORITY_1)) {
				obj.setPriority(ATTR_PRIORITY_1);
			} else if (obj.getPriority().equals(PRIORITY_2)) {
				obj.setPriority(ATTR_PRIORITY_2);
			} else if (obj.getPriority().equals(PRIORITY_3)) {
				obj.setPriority(ATTR_PRIORITY_3);
			} else if (obj.getPriority().equals(PRIORITY_4)) {
				obj.setPriority(ATTR_PRIORITY_4);
			} else if (obj.getPriority().equals(PRIORITY_5)) {
				obj.setPriority(ATTR_PRIORITY_5);
			}
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_PRIORITY + TAG_END, obj.getPriority());
			
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_URGENCY + TAG_END, obj.getUrgency());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_IMPACT + TAG_END, obj.getImpact());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_STATE + TAG_END, obj.getState());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_CATEGORY + TAG_END, obj.getCategory());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_ASGN_GROUP + TAG_END, obj.getAssignmentGroup());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_ASGN_TO + TAG_END, obj.getAssignedTo());
						
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_SYS_UPDT_ON + TAG_END, obj.getSysUpdatedOn());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_SYS_UPDT_BY + TAG_END, obj.getSysUpdatedBy());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_SLA_DUE + TAG_END, obj.getSlaDue());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_SLA_MADE + TAG_END, obj.getSlaMade());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_SYS_CREATED_ON + TAG_END, obj.getSysCreatedOn());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_DUE_DATE + TAG_END, obj.getDueDate());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_TIME_WRKD + TAG_END, obj.getTimeWorked() + "");
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_CAL_STC + TAG_END, obj.getCalendarStc() + "");
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_RESOLVE_DATE + TAG_END, obj.getResolvedAtDate());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_TT_RESOLVE + TAG_END, obj.getTimeToResolveInHours() + "");
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_TT_REACT + TAG_END, obj.getTimeToReactInHours() + "");
			
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_YEAR + TAG_END, obj.getPredYear());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_QUARTER + TAG_END, obj.getPredQuarter());
			incsTmpltStr = StringUtils.replaceFirst(incsTmpltStr, TAG_BEGIN + ENTITY_PRED + "." + ATTR_INC_MONTH + TAG_END, obj.getPredMonth());
		
			
			
			incsTmpltStr = StringUtils.replaceAll(incsTmpltStr, TAG_BEGIN + TIME_UNIX_NANO + TAG_END, timeStamp + "");
			
			
			result[i] = new String(incsTmpltStr);
		}		
		
		
		return result;
		
	}	
	
	private static String[] xformOutSummaries(ServiceConfig srvcConf, XFormConfig xformConf, List<PredictabilitySummary> summs) throws Throwable {
		
		List<TemplateConfig> tmplts = xformConf.getTemplates();
		TemplateConfig tmpltSum = null;
		TemplateConfig tmpltMetric = null;
		TemplateConfig tmpltDatapoint = null;
		TemplateConfig tmpltDpAttr = null;
		
		for (TemplateConfig xConfig : tmplts) {
			if (xConfig.getTemplateName().equals("predict_summary")) {
				tmpltSum = xConfig;
							
			} else if (xConfig.getTemplateName().equals("metric")) {
				tmpltMetric = xConfig;
				
			} else if (xConfig.getTemplateName().equals("datapoint")) {
				tmpltDatapoint = xConfig;		
				
			} else if (xConfig.getTemplateName().equals("dp_attribute")) {
				tmpltDpAttr = xConfig;								
			}
		}
		
		String[] result = new String[summs.size()];
		
		for (int i = 0; i < result.length; i++) {
			
			// get the top level template file for one pred_summary payload
			String sumTmpltStr = StringUtils.getFileAsString(tmpltSum.getTemplateFilePath());
			PredictabilitySummary obj = summs.get(i);
			
			// fill in the attributes for one pred_summary
			sumTmpltStr = StringUtils.replaceAll(sumTmpltStr, TAG_BEGIN + TAG_SOLUTION_NAME + TAG_END, ApplicationCtx.SOLUTION_NAME);
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + ENTITY_PRED_SUMM + "." + ATTR_SUM_QTRYR_TXT + TAG_END, obj.getQuarterYearText());
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + ENTITY_PRED_SUMM + "." + ATTR_SUM_YRTYPE_TXT + TAG_END, obj.getYearTypeText());
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + ENTITY_PRED_SUMM + "." + ATTR_INC_YEAR + TAG_END, obj.getPredYear());
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + ENTITY_PRED_SUMM + "." + ATTR_INC_QUARTER + TAG_END, obj.getPredQuarter());
			sumTmpltStr = StringUtils.replaceFirst(sumTmpltStr, TAG_BEGIN + ENTITY_PRED_SUMM + "." + ATTR_SUM_TOTAL_INCS + TAG_END, obj.getTotalIncidents() + "");
			
			
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
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TAG_METRIC_NUMERIC_TYPE + TAG_END, TAG_METRIC_TYPE_AS_INT);
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TAG_METRIC_VALUE + TAG_END, met.getLongValue() + "");
						dpTmpltStr = StringUtils.replaceAll(dpTmpltStr, TAG_BEGIN + TIME_UNIX_NANO + TAG_END, met.getTimeUnixNano() + "");
						
						StringBuffer dpAttrsBuff = new StringBuffer();
						for (String attrName : met.getAttributes().keySet()) {
							
							// get the template file for the data point attribute
							String dpAttrTmpltStr = StringUtils.getFileAsString(tmpltDpAttr.getTemplateFilePath());
							
							// fill in details for the metric data point attribute
							dpAttrTmpltStr = StringUtils.replaceAll(dpAttrTmpltStr, TAG_BEGIN + TAG_METRIC_ATTR_NAME + TAG_END, attrName);
							dpAttrTmpltStr = StringUtils.replaceAll(dpAttrTmpltStr, TAG_BEGIN + TAG_METRIC_ATTR_VAL + TAG_END, met.getAttributes().get(attrName));
							dpAttrsBuff.append(dpAttrTmpltStr);
						}
						
						// take off the last comma here
						String dpAttrs = dpAttrsBuff.toString().substring(0, dpAttrsBuff.length()-1);
						//logger.info("Datapoint Attributes = " + dpAttrs);
						
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

	private static void deriveCountByPriorityByCategoryMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incCategory, String attrCategory, String incPriority, String attrPriority) throws Throwable {
		
		Metric met = null;
		long metVal;

		metVal = calcCountByPriorityByCategoryForSummary(summ, incCategory, incPriority);
		//logger.info("By Priority & Category:     " + summ.getQuarterYearText() + " : " + incPriority + " : " + incCategory + " : Count = " + metVal);
		//if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("priority", attrPriority);
			met.addAttribute("category", attrCategory);
			summ.addMetric(met);			
		//}
	}
	
	// calcCountByPriorityForSummary
	private static void deriveCountByPriorityMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incPriority, String attrPriority) throws Throwable {
		
		Metric met = null;
		long metVal;
		
		metVal = calcCountByPriorityForSummary(summ, incPriority);
		//logger.info("By Priority:                " + summ.getQuarterYearText() + " : " + incPriority + " : Count = " + metVal);
		//if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("priority", attrPriority);
			summ.addMetric(met);			
		//}
	}
	
	private static void deriveCountByStateMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incState, String attrState) throws Throwable {
		
		Metric met = null;
		long metVal;
		
		metVal = calcCountByStateForSummary(summ, incState);
		//logger.info("By State:                   " + summ.getQuarterYearText() + " : " + incState + " : Count = " + metVal);
		//if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("state", attrState);
			summ.addMetric(met);			
		//}
	}	
	
	private static void deriveCountByCategoryMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incCategory, String attrCategory) throws Throwable {
		
		Metric met = null;
		long metVal;
		
		metVal = calcCountByCategoryForSummary(summ, incCategory);
		//logger.info("By Category:                " + summ.getQuarterYearText() + " : " + incCategory + " : Count = " + metVal);
		//if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("category", attrCategory);
			summ.addMetric(met);			
		//}
	}
	
	private static void deriveCountByAssignGroupMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incAssignGroup, String attrAssignGroup) throws Throwable {
		
		Metric met = null;
		long metVal;
		
		metVal = calcCountByAssignGroupForSummary(summ, incAssignGroup);
		//logger.info("By Assign Group:            " + summ.getQuarterYearText() + " : " + incAssignGroup + " : Count = " + metVal);
		//if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("assignment_group", attrAssignGroup);
			summ.addMetric(met);			
		//}
	}
	
	
	private static void deriveMttReactMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incPriority, String attrPiority) throws Throwable {
		
		Metric met = null;
		long metVal;
		
		metVal = calcMttReactForSummary(summ, incPriority);
		//logger.info(summ.getQuarterYearText() + " : Priority = " + incPriority + " : MTT React = " + metVal);
		if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("priority", attrPiority);
			summ.addMetric(met);			
		}

	}

	
	private static void deriveMttResolveMetric(PredictabilitySummary summ, long timeStamp, String metricName, String metricTagName, 
			String metricType, String incPriority, String attrPiority) throws Throwable {
		
		Metric met = null;
		long metVal;
		
		metVal = calcMttResolveForSummary(summ, incPriority);
		//logger.info(summ.getQuarterYearText() + " : Priority = " + incPriority + " : MTT Resolve = " + metVal);
		
		if (metVal > 0) {
			met = new Metric(true);
			met.setMetricName(metricName);
			met.setMetricTagName(metricTagName);
			met.setMetricType(metricType);
			met.setLongValue(metVal);
			met.setTimeUnixNano(timeStamp);
			met.setStartTimeUnixNano(timeStamp);
			met.addAttribute("priority", attrPiority);
			summ.addMetric(met);			
		}

	}
	
	private static void createCountMetricsForSummary(PredictabilitySummary summ, long timeStamp) throws Throwable {

		List<String> groups = new ArrayList<String>();
		
		// create count metrics for assignment groups
		for (Predictability inc : summ.getIncidents()) {
			if (!groups.contains(inc.getAssignmentGroup())) {
				groups.add(inc.getAssignmentGroup());
			}
		}
		for (String key : groups) {
			//logger.info("Creating count metric for : " + key);
			deriveCountByAssignGroupMetric(summ, timeStamp, MET_CT_ASSGN_GRP, MET_CT_ASSGN_GRP, TAG_METRIC_TYPE_GAUGE, key, key);
		}
		
		
		// create count metrics for categories
		groups = new ArrayList<String>();
		for (Predictability inc : summ.getIncidents()) {
			if (!groups.contains(inc.getCategory())) {
				groups.add(inc.getCategory());
			}
		}
		for (String key : groups) {
			//logger.info("Creating count metric for : " + key);
			deriveCountByCategoryMetric(summ, timeStamp, MET_CT_CATEGORY, MET_CT_CATEGORY, TAG_METRIC_TYPE_GAUGE, key, key);
		}

		
		
		// create count metrics for categories + priorities
		groups = new ArrayList<String>();
		for (Predictability inc : summ.getIncidents()) {
			if (!groups.contains(inc.getCategory())) {
				groups.add(inc.getCategory());
			}
		}
		for (String key : groups) {
			//logger.info("Creating count metric for : " + key);
			deriveCountByPriorityByCategoryMetric(summ, timeStamp, MET_CT_PRIORITY_CATEGORY, MET_CT_PRIORITY_CATEGORY, TAG_METRIC_TYPE_GAUGE, key, key, PRIORITY_1, ATTR_PRIORITY_1);
			deriveCountByPriorityByCategoryMetric(summ, timeStamp, MET_CT_PRIORITY_CATEGORY, MET_CT_PRIORITY_CATEGORY, TAG_METRIC_TYPE_GAUGE, key, key, PRIORITY_2, ATTR_PRIORITY_2);
			deriveCountByPriorityByCategoryMetric(summ, timeStamp, MET_CT_PRIORITY_CATEGORY, MET_CT_PRIORITY_CATEGORY, TAG_METRIC_TYPE_GAUGE, key, key, PRIORITY_3, ATTR_PRIORITY_3);
			deriveCountByPriorityByCategoryMetric(summ, timeStamp, MET_CT_PRIORITY_CATEGORY, MET_CT_PRIORITY_CATEGORY, TAG_METRIC_TYPE_GAUGE, key, key, PRIORITY_4, ATTR_PRIORITY_4);
			deriveCountByPriorityByCategoryMetric(summ, timeStamp, MET_CT_PRIORITY_CATEGORY, MET_CT_PRIORITY_CATEGORY, TAG_METRIC_TYPE_GAUGE, key, key, PRIORITY_5, ATTR_PRIORITY_5);
		}
		
		
		// create count metrics for priorities
		deriveCountByPriorityMetric(summ, timeStamp, MET_CT_PRIORITY, MET_CT_PRIORITY, TAG_METRIC_TYPE_GAUGE, PRIORITY_1, ATTR_PRIORITY_1);
		deriveCountByPriorityMetric(summ, timeStamp, MET_CT_PRIORITY, MET_CT_PRIORITY, TAG_METRIC_TYPE_GAUGE, PRIORITY_2, ATTR_PRIORITY_2);
		deriveCountByPriorityMetric(summ, timeStamp, MET_CT_PRIORITY, MET_CT_PRIORITY, TAG_METRIC_TYPE_GAUGE, PRIORITY_3, ATTR_PRIORITY_3);
		deriveCountByPriorityMetric(summ, timeStamp, MET_CT_PRIORITY, MET_CT_PRIORITY, TAG_METRIC_TYPE_GAUGE, PRIORITY_4, ATTR_PRIORITY_4);
		deriveCountByPriorityMetric(summ, timeStamp, MET_CT_PRIORITY, MET_CT_PRIORITY, TAG_METRIC_TYPE_GAUGE, PRIORITY_5, ATTR_PRIORITY_5);
		
		
		// create count metrics for states
		deriveCountByStateMetric(summ, timeStamp, MET_CT_STATE, MET_CT_STATE, TAG_METRIC_TYPE_GAUGE, STATE_NEW, STATE_NEW);
		deriveCountByStateMetric(summ, timeStamp, MET_CT_STATE, MET_CT_STATE, TAG_METRIC_TYPE_GAUGE, STATE_CLOSED, STATE_CLOSED);
		deriveCountByStateMetric(summ, timeStamp, MET_CT_STATE, MET_CT_STATE, TAG_METRIC_TYPE_GAUGE, STATE_ON_HOLD, STATE_ON_HOLD);
		deriveCountByStateMetric(summ, timeStamp, MET_CT_STATE, MET_CT_STATE, TAG_METRIC_TYPE_GAUGE, STATE_IN_PROGRESS, STATE_IN_PROGRESS);

	}	
	
	
	private static void createMttrMetricsForSummary(PredictabilitySummary summ, long timeStamp) throws Throwable {

		deriveMttReactMetric(summ, timeStamp, MET_MTT_REACT, MET_MTT_REACT, TAG_METRIC_TYPE_GAUGE, PRIORITY_1, ATTR_PRIORITY_1);
		deriveMttReactMetric(summ, timeStamp, MET_MTT_REACT, MET_MTT_REACT, TAG_METRIC_TYPE_GAUGE, PRIORITY_2, ATTR_PRIORITY_2);
		deriveMttReactMetric(summ, timeStamp, MET_MTT_REACT, MET_MTT_REACT, TAG_METRIC_TYPE_GAUGE, PRIORITY_3, ATTR_PRIORITY_3);
		deriveMttReactMetric(summ, timeStamp, MET_MTT_REACT, MET_MTT_REACT, TAG_METRIC_TYPE_GAUGE, PRIORITY_4, ATTR_PRIORITY_4);
		deriveMttReactMetric(summ, timeStamp, MET_MTT_REACT, MET_MTT_REACT, TAG_METRIC_TYPE_GAUGE, PRIORITY_5, ATTR_PRIORITY_5);
		
		deriveMttResolveMetric(summ, timeStamp, MET_MTT_RESOLVE, MET_MTT_RESOLVE, TAG_METRIC_TYPE_GAUGE, PRIORITY_1, ATTR_PRIORITY_1);
		deriveMttResolveMetric(summ, timeStamp, MET_MTT_RESOLVE, MET_MTT_RESOLVE, TAG_METRIC_TYPE_GAUGE, PRIORITY_2, ATTR_PRIORITY_2);
		deriveMttResolveMetric(summ, timeStamp, MET_MTT_RESOLVE, MET_MTT_RESOLVE, TAG_METRIC_TYPE_GAUGE, PRIORITY_3, ATTR_PRIORITY_3);
		deriveMttResolveMetric(summ, timeStamp, MET_MTT_RESOLVE, MET_MTT_RESOLVE, TAG_METRIC_TYPE_GAUGE, PRIORITY_4, ATTR_PRIORITY_4);
		deriveMttResolveMetric(summ, timeStamp, MET_MTT_RESOLVE, MET_MTT_RESOLVE, TAG_METRIC_TYPE_GAUGE, PRIORITY_5, ATTR_PRIORITY_5);	
	
	}
	
	
	// Here we create a new summary object for each year + quarter from the list of incidents
	// and add the appropriate incidents to the appropriate summary
	private static List<PredictabilitySummary> createSummaries(List<Predictability> incs) throws Throwable {
		
		Map<String, PredictabilitySummary> summs = new HashMap<String, PredictabilitySummary>();
		
		PredictabilitySummary summ = null;
		
		for (Predictability inc : incs) {
			
			if (!summs.containsKey(inc.getPredYear() + ":" + inc.getPredQuarter())) {
				
				summ = new PredictabilitySummary();
				summ.setPredYear(inc.getPredYear());
				summ.setPredQuarter(inc.getPredQuarter());
				if (ApplicationCtx.SRVC_CONF.getFirstMonthOfFirstQuarter() == 1) {
					summ.setYearTypeText("CY");
				} else {
					summ.setYearTypeText("FY");
				}
				
				summ.setQuarterYearText(summ.getYearTypeText() + summ.getPredYear().substring(2, 4) + "-" + "Q" + summ.getPredQuarter());
				
				
				summs.put(inc.getPredYear() + ":" + inc.getPredQuarter(), summ);
				
			} else {
				summ = summs.get(inc.getPredYear() + ":" + inc.getPredQuarter());
				
			}
			
			summ.addIncident(inc);
			
			
			
		}
		
		
		
		long timeStamp = ApplicationCtx.getTimeInNanos();
		//long timeBetween = 360000000000l; // 6 minutes apart
		long timeBetween = 420000000000l; // 7 minutes apart
		long backDate = timeBetween * (summs.size() - 1);
		timeStamp = timeStamp - backDate;
		SortedSet<String> orderedKeys = new TreeSet<String>(summs.keySet());
		List<PredictabilitySummary> orderedSumms = new ArrayList<PredictabilitySummary>();
		
		for(String key : orderedKeys) {
			
			logger.info(summs.get(key).getQuarterYearText() + " : Incidents = " +summs.get(key).getIncidents().size());
			
			summs.get(key).setTotalIncidents(summs.get(key).getIncidents().size());
			
			
			createMttrMetricsForSummary(summs.get(key), timeStamp);
			
			createCountMetricsForSummary(summs.get(key), timeStamp);
			
			orderedSumms.add(summs.get(key));
			
			timeStamp = timeStamp + timeBetween;
			
			logger.info("Summary = " + key);
			
			//logger.info("");
		}
		
		return orderedSumms;
		
	}
	
	private static long calcCountByStateForSummary(final PredictabilitySummary summ, final String state) throws Throwable {
		long incCntr = 0;
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getState().equals(state)) {
				incCntr++;				
			}
		}
		return incCntr;
	}

	private static long calcCountByPriorityByCategoryForSummary(final PredictabilitySummary summ, final String category, final String priority) throws Throwable {
		long incCntr = 0;
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getCategory().equals(category) && obj.getPriority().equals(priority)) {
				incCntr++;				
			}
		}
		return incCntr;
	}

	private static long calcCountByPriorityForSummary(final PredictabilitySummary summ, final String priority) throws Throwable {
		long incCntr = 0;
		//logger.info("### " + summ.getQuarterYearText() + " : Incindents Size = " + summ.getIncidents().size());
		
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getPriority().equals(priority)) {
				//logger.info("### " + summ.getQuarterYearText() + " : Priority Passed = " + priority);
				//logger.info("### " + summ.getQuarterYearText() + " : Priority on Inc = " + obj.getPriority());
				incCntr++;
				
			}
		}
		return incCntr;
	}
	
	private static long calcCountByCategoryForSummary(final PredictabilitySummary summ, final String category) throws Throwable {
		long incCntr = 0;
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getCategory().equals(category)) {
				incCntr++;				
			}
		}
		return incCntr;
	}
	
	private static long calcCountByAssignGroupForSummary(final PredictabilitySummary summ, final String assignGroup) throws Throwable {
		long incCntr = 0;
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getAssignmentGroup().equals(assignGroup)) {
				incCntr++;				
			}
		}
		return incCntr;
	}
	
	
	private static long calcMttReactForSummary(final PredictabilitySummary summ, final String priority) throws Throwable {
		
		long incCntr = 0;
		long mttHrsTotal = 0;
		
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getState() != null && !obj.getState().equals(STATE_CLOSED)) {
				if (obj.getPriority().equals(priority)) {
					incCntr++;
					mttHrsTotal = mttHrsTotal + obj.getTimeToReactInHours();					
				}
			}
		}
		
		if (incCntr > 0 && mttHrsTotal > 0) {
			return mttHrsTotal / incCntr;
		} else {
			return 0;
		}
	}
	
	private static long calcMttResolveForSummary(final PredictabilitySummary summ, final String priority) throws Throwable {
		
		long incCntr = 0;
		long mttHrsTotal = 0;
		
		for (Predictability obj : summ.getIncidents()) {
			if (obj.getState() != null && obj.getState().equals(STATE_CLOSED)) {
				if (obj.getPriority().equals(priority)) {
					incCntr++;
					mttHrsTotal = mttHrsTotal + obj.getTimeToResolveInHours();					
				}
			}
		}
		
		if (incCntr > 0 && mttHrsTotal > 0) {
			return mttHrsTotal / incCntr;
		} else {
			return 0;
		}
	}
	
	private static List<Predictability> xformInIncidents(String json) throws Throwable {	
		
		List<Predictability> theList = new ArrayList<Predictability>();
		
		//List<String> attrs = new ArrayList<String>();
		
		
		JSONObject jobj = new JSONObject(json);
		
		JSONObject jroot = jobj.getJSONObject("root");
	
		JSONArray jarray = jroot.getJSONArray("row");
		
		for(int arrCntr = 0; arrCntr < jarray.length(); arrCntr++){
			JSONObject jinc = jarray.getJSONObject(arrCntr);
			
			Predictability theObj = new Predictability();
			theObj.setIncNumber(jinc.getString(ATTR_INC_NUMBER).trim());
			theObj.setOpenedAtDate(jinc.getString(ATTR_INC_OPENED_DATE).trim());
			theObj.setClosedAtDate(jinc.getString(ATTR_INC_CLOSED_DATE).trim());
			theObj.setShortDescr(jinc.getString(ATTR_INC_SHORT_DESC).trim());
			theObj.setCallerId(jinc.getString(ATTR_INC_CALLER_ID).trim());
			theObj.setPriority(jinc.getString(ATTR_INC_PRIORITY).trim());
			theObj.setUrgency(jinc.getString(ATTR_INC_URGENCY).trim());
			theObj.setImpact(jinc.getString(ATTR_INC_IMPACT).trim());
			theObj.setState(jinc.getString(ATTR_INC_STATE).trim());
			
			theObj.setCategory(jinc.getString(ATTR_INC_CATEGORY).trim());
			theObj.setCategory(StringUtils.replaceFirst(theObj.getCategory(), " / ", "-"));
			if (theObj.getCategory() == null || theObj.getCategory().equals("")) {
				theObj.setCategory("Unassigned");
			}
			
			theObj.setAssignmentGroup(jinc.getString(ATTR_INC_ASGN_GROUP).trim());
			if (theObj.getAssignmentGroup() == null || theObj.getAssignmentGroup().equals("")) {
				theObj.setAssignmentGroup("Unassigned");
			}
			
			theObj.setAssignedTo(jinc.getString(ATTR_INC_ASGN_TO).trim());
			theObj.setSysUpdatedOn(jinc.getString(ATTR_INC_SYS_UPDT_ON).trim());
			theObj.setSysUpdatedBy(jinc.getString(ATTR_INC_SYS_UPDT_BY).trim());
			theObj.setSlaDue(jinc.getString(ATTR_INC_SLA_DUE).trim());
			theObj.setSlaMade(jinc.getString(ATTR_INC_SLA_MADE).trim());
			theObj.setSysCreatedOn(jinc.getString(ATTR_INC_SYS_CREATED_ON).trim());
			theObj.setDueDate(jinc.getString(ATTR_INC_DUE_DATE).trim());
			theObj.setTimeWorked(StringUtils.fixLong(jinc.getString(ATTR_INC_TIME_WRKD).trim()));
			theObj.setCalendarStc(StringUtils.fixLong(jinc.getString(ATTR_INC_CAL_STC).trim()));
			theObj.setResolvedAtDate(jinc.getString(ATTR_INC_RESOLVE_DATE).trim());

			
			// order is important here
			processHoursToResolve(theObj);
			processHoursToReact(theObj);
			processMonthAndYear(theObj);
			processQuarter(theObj);
			
			theList.add(theObj);
			
			
			//if (!attrs.contains(inc.getCategory())) {
				//attrs.add(inc.getCategory());
			//}
			
		}		
		
		logger.info("Number of Incidents = " + theList.size());
		
		//logger.info("");
		//logger.info("");
		//for(String attrName : attrs) {
			//logger.info("Name = " + attrName);
		//}
		//logger.info("");
		
		logger.info("");
		
		return theList;
	}
	
	private static void processQuarter(Predictability obj) throws Throwable {
		
		obj.setPredQuarter(QTRS_MAP.get(obj.getPredMonth()));
		
		//logger.info("Qtr = " + obj.getPredQuarter() + " | Month = " + obj.getPredMonth());
	}
	
	private static void processMonthAndYear(Predictability obj) throws Throwable {
		
		String theDate = StringUtils.replaceAll(obj.getOpenedAtDate(), " ", "T");
		LocalDateTime ldt = LocalDateTime.parse(theDate);
		obj.setPredYear(ldt.getYear() + "");
		obj.setPredMonth(ldt.getMonthValue() + "");
		
		//logger.info("Year = " + obj.getIncYear() + " | Month = " + obj.getIncMonth());
		
	}

	private static void processHoursToReact(Predictability obj) throws Throwable {
		
		if (obj.getState() != null && !obj.getState().equals(STATE_CLOSED)) {
			long diff = DateUtils.differenceInHours(obj.getOpenedAtDate(), obj.getSysUpdatedOn());
			obj.setTimeToReactInHours(diff);
			
			//logger.info("Hours to React = " + diff);
			//if (diff <= 0) {
				//logger.info("Incident = " + obj.getIncNumber() + " | Hours = " + diff);
			//}
		}
		
	}
	
	private static void processHoursToResolve(Predictability obj) throws Throwable {
		
		if (obj.getState() != null && obj.getState().equals(STATE_CLOSED)) {
			long diff = DateUtils.differenceInHours(obj.getOpenedAtDate(), obj.getClosedAtDate());
			obj.setTimeToResolveInHours(diff);
			
			//logger.info("Hours to Resolve = " + diff);
			//if (diff == 0) {
				//logger.info("Incident = " + obj.getIncNumber() + " | Hours = " + diff);
			//}
		}
		
	}
	
}
