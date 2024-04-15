/**
 * 
 */
package com.rudetools.otel;

import java.util.Calendar;

import com.rudetools.otel.config.ServiceConfig;

/**
 * @author james101
 *
 */
public class ApplicationCtx {

	public static ServiceConfig SRVC_CONF = null;
	public static String SOLUTION_NAME = "";
	
	
	
	/**
	 * 
	 */
	public ApplicationCtx() {
		
	}
	
	public static long getTimeInNanos() {
		
		Calendar cal = Calendar.getInstance();
		String millis = cal.getTimeInMillis() + "";
		String nanos = "000000";
		
		return Long.parseLong(millis + nanos);		
		
	}	

}
