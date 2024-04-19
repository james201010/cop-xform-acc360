/**
 * 
 */
package com.rudetools.otel.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.rudetools.otel.AppConstants;
import com.rudetools.otel.ApplicationCtx;
import com.rudetools.otel.config.XFormConfig;
import com.rudetools.otel.utils.HttpUtils;
import com.rudetools.otel.xform.XForm;



/**
 * @author James Schneider
 *
 */

@Controller
public class WebController implements AppConstants {

	
	public static final Logger logger = LoggerFactory.getLogger(WebController.class);
	
	/**
	 * 
	 */
	public WebController() {
		
		
	}

	@RequestMapping(value="/environmental", method = RequestMethod.POST)
	public String processEnvironmental(HttpServletRequest request, ModelMap model, @RequestBody String json, @RequestParam String clientId) {
		
		try {
			
			logger.info("Client IP = " + request.getRemoteAddr());
			
			if (clientId != null && json != null) {
				
				if (clientId.equals(ApplicationCtx.SRVC_CONF.getClientId())) {

					logger.info("ClientId = " + clientId);
					//logger.info("Incoming json = " + json);
					
					String forward = System.getProperty("receiver.forward", "false");
					
					List<XFormConfig> xconfs = ApplicationCtx.SRVC_CONF.getXforms();
					
					XFormConfig xconf = null;
					
					for (XFormConfig xc : xconfs) {
						if (xc.getXformName().equals("environmental")) {
							xconf = xc;
							break;
						}
					}
					
	    			Class<?> clazz = Class.forName(xconf.getXformClassName());
	    			Object object = clazz.newInstance();
	    			XForm xform = (XForm)object;						
					
	    			Map<String, String[]> payloadMap = xform.xform(ApplicationCtx.SRVC_CONF, xconf, json);
	    			
	    			
	    			String[] summsYrPayload = payloadMap.get("summaries_yr");			
		    		if (forward != null && forward.toLowerCase().equals("true")) {
		    			for (int i = 0; i < summsYrPayload.length; i++) {
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("-----------------------------------------------------------------------------------------------------");
			    			//logger.info("###### Outbound summaries by year json = ");
		    				//logger.info(summsYrPayload[i]);
			    			HttpUtils.sendOtlpJsonProto(ApplicationCtx.SRVC_CONF.getOtelCollectorHttpProtoMetricsEndpoint(), summsYrPayload[i]);
						}
					}

		    		Thread.currentThread().sleep(10000);
	    			
	    			String[] summsQtrPayload = payloadMap.get("summaries_qtr");			
		    		if (forward != null && forward.toLowerCase().equals("true")) {
		    			for (int i = 0; i < summsQtrPayload.length; i++) {
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("-----------------------------------------------------------------------------------------------------");
			    			//logger.info("###### Outbound summaries by quarter json = ");
		    				//logger.info(summsQtrPayload[i]);
			    			HttpUtils.sendOtlpJsonProto(ApplicationCtx.SRVC_CONF.getOtelCollectorHttpProtoMetricsEndpoint(), summsQtrPayload[i]);
						}
					}
	    			
	    			
	    			
	    			String[] envsPayload = payloadMap.get("environ");			
		    		if (forward != null && forward.toLowerCase().equals("true")) {
		    			for (int i = 0; i < envsPayload.length; i++) {
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("-----------------------------------------------------------------------------------------------------");
			    			//logger.info("###### Outbound environmental json = ");
		    				//logger.info(envsPayload[i]);
			    			HttpUtils.sendOtlpJsonProto(ApplicationCtx.SRVC_CONF.getOtelCollectorHttpProtoMetricsEndpoint(), envsPayload[i]);
						}
					}		    		

					return "result";
					
					
				}

			}
				
			
			return "error";
			
		} catch (Throwable ex) {
			ex.printStackTrace();
			return "error";
		}
		
	}	

	
	@RequestMapping(value="/predictability", method = RequestMethod.POST)
	public String processPredictability(HttpServletRequest request, ModelMap model, @RequestBody String json, @RequestParam String clientId) {
		
		try {
			
			logger.info("Client IP = " + request.getRemoteAddr());
			
			if (clientId != null && json != null) {
				
				if (clientId.equals(ApplicationCtx.SRVC_CONF.getClientId())) {

					logger.info("ClientId = " + clientId);
					//logger.info("Incoming json = " + json);
					
					String forward = System.getProperty("receiver.forward", "false");
					
					List<XFormConfig> xconfs = ApplicationCtx.SRVC_CONF.getXforms();
					
					XFormConfig xconf = null;
					
					for (XFormConfig xc : xconfs) {
						if (xc.getXformName().equals("predictability")) {
							xconf = xc;
							break;
						}
					}
					
	    			Class<?> clazz = Class.forName(xconf.getXformClassName());
	    			Object object = clazz.newInstance();
	    			XForm xform = (XForm)object;						
					
	    			Map<String, String[]> payloadMap = xform.xform(ApplicationCtx.SRVC_CONF, xconf, json);
	    			
	    			
	    			String[] summsPayload = payloadMap.get("summaries");			
		    		if (forward != null && forward.toLowerCase().equals("true")) {
		    			for (int i = 0; i < summsPayload.length; i++) {
			    			//logger.info("");
			    			//logger.info("-----------------------------------------------------------------------------------------------------");
			    			//logger.info("###### Outbound summaries json = ");
		    				//logger.info(summsPayload[i]);
			    			HttpUtils.sendOtlpJsonProto(ApplicationCtx.SRVC_CONF.getOtelCollectorHttpProtoMetricsEndpoint(), summsPayload[i]);
						}
					}
		    		
	    			String[] incsPayload = payloadMap.get("incidents");			
		    		if (forward != null && forward.toLowerCase().equals("true")) {
		    			for (int i = 0; i < incsPayload.length; i++) {
			    			//logger.info("");
			    			//logger.info("-----------------------------------------------------------------------------------------------------");
			    			//logger.info("###### Outbound incidents json = ");
		    				//logger.info(incsPayload[i]);
			    			HttpUtils.sendOtlpJsonProto(ApplicationCtx.SRVC_CONF.getOtelCollectorHttpProtoMetricsEndpoint(), incsPayload[i]);
						}
					}		    		

					return "result";
					
					
				}

			}
				
			
			return "error";
			
		} catch (Throwable ex) {
			ex.printStackTrace();
			return "error";
		}
		
	}	
	
	
	
	@RequestMapping(value="/poreportmonthly", method = RequestMethod.POST)
	public String processPoReportMonthly(HttpServletRequest request, ModelMap model, @RequestBody String json, @RequestParam String clientId) {
		
		try {
			
			logger.info("Client IP = " + request.getRemoteAddr());
			
			if (clientId != null && json != null) {
				
				if (clientId.equals(ApplicationCtx.SRVC_CONF.getClientId())) {

					logger.info("ClientId = " + clientId);
					//logger.info("Incoming json = " + json);
					
					String forward = System.getProperty("receiver.forward", "false");
					
					if (forward != null && forward.toLowerCase().equals("true")) {
						
						List<XFormConfig> xconfs = ApplicationCtx.SRVC_CONF.getXforms();
						
						XFormConfig xconf = null;
						
						for (XFormConfig xc : xconfs) {
							if (xc.getXformName().equals("poreport")) {
								xconf = xc;
								break;
							}
						}
						
		    			Class<?> clazz = Class.forName(xconf.getXformClassName());
		    			Object object = clazz.newInstance();
		    			XForm xform = (XForm)object;						
						
		    			Map<String, String[]> payloadMap = xform.xform(ApplicationCtx.SRVC_CONF, xconf, json);
		    			
		    			String[] outPayload = payloadMap.get("poreport");
		    		
		    			for (int i = 0; i < outPayload.length; i++) {
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("");
			    			//logger.info("-----------------------------------------------------------------------------------------------------");
			    			//logger.info("Outbound json = " + outPayload[i]);		    				
		    				HttpUtils.sendOtlpJsonProto(ApplicationCtx.SRVC_CONF.getOtelCollectorHttpProtoMetricsEndpoint(), outPayload[i]);
						}
						
					}
					

					return "result";
					
					
				}

			}
				
			
			return "error";
			
		} catch (Throwable ex) {
			ex.printStackTrace();
			return "error";
		}
		
	}	
	
	
	
	
}
