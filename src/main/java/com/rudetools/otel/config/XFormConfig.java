/**
 * 
 */
package com.rudetools.otel.config;

import java.util.List;

/**
 * @author james101
 *
 */
public class XFormConfig {

	private String xformName;
	private String xformClassName;
	private List<TemplateConfig> templates;
	
	
	/**
	 * 
	 */
	public XFormConfig() {
		
	}


	public String getXformName() {
		return xformName;
	}


	public void setXformName(String xformName) {
		this.xformName = xformName;
	}


	public String getXformClassName() {
		return xformClassName;
	}


	public void setXformClassName(String xformClassName) {
		this.xformClassName = xformClassName;
	}


	public List<TemplateConfig> getTemplates() {
		return templates;
	}


	public void setTemplates(List<TemplateConfig> templates) {
		this.templates = templates;
	}

}
