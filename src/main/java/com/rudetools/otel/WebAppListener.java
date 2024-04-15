/**
 * 
 */
package com.rudetools.otel;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.rudetools.otel.config.ServiceConfig;
import com.rudetools.otel.utils.StringUtils;




/**
 * @author james101
 *
 */
public class WebAppListener implements AppConstants, ApplicationListener<ApplicationEvent> {

	public static final Logger lgr = LoggerFactory.getLogger(WebAppListener.class);
	
	/**
	 * 
	 */
	public WebAppListener() {
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		if (event instanceof AvailabilityChangeEvent) {
			
			AvailabilityChangeEvent<?> ace = (AvailabilityChangeEvent<?>)event;
			
			if (ace.getState().equals(ReadinessState.ACCEPTING_TRAFFIC)) {
				
				try {
					
					lgr.info("");
					lgr.info("");
					lgr.info("######################################    STARTING COP XFORM    ######################################");
					lgr.info("");
					
					String confPath = System.getProperty(SERVICE_CONF_KEY);
					
					if (confPath == null || confPath.equals("")) {
						lgr.error("Missing JVM startup property -D" + SERVICE_CONF_KEY);
						lgr.error("Please set this property -D" + SERVICE_CONF_KEY + " with the full or relative path to the configuration Yaml file like this | -D" + SERVICE_CONF_KEY + "=/opt/cisco/cop-xform/config.yaml");
						lgr.info("");
						lgr.info("");
						System.exit(1);
					}
					
					
					Yaml yaml = new Yaml(new Constructor(ServiceConfig.class));
					InputStream inputStream = StringUtils.getFileAsStream(confPath);
					
					ApplicationCtx.SRVC_CONF = yaml.load(inputStream);
					
					
					init();
					
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				
				
			}
			
		}
		
		
	}
	
	private static void init() {
		
		try {
			
			ApplicationCtx.SOLUTION_NAME = ApplicationCtx.SRVC_CONF.getCopSolutionName();
			
			
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
	}	
}
