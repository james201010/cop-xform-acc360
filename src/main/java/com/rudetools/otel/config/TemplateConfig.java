/**
 * 
 */
package com.rudetools.otel.config;

/**
 * @author james101
 *
 */
public class TemplateConfig {

	private String templateName;
	private String templateFilePath;
	
	/**
	 * 
	 */
	public TemplateConfig() {
		
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateFilePath() {
		return templateFilePath;
	}

	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}

}
