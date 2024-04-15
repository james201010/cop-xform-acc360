/**
 * 
 */
package com.rudetools.otel.xform;

import java.util.Map;

import com.rudetools.otel.config.ServiceConfig;
import com.rudetools.otel.config.XFormConfig;

/**
 * 
 * @author james101
 *
 */
public interface XForm {
	
	public abstract Map<String, String[]> xform(ServiceConfig srvcConf, XFormConfig xformConf, String data2Xform) throws Throwable;
	
	
	
}
