package com.rudetools.otel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author James Schneider
 *
 */

@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		//SpringApplication.run(WebApplication.class, args);
	    SpringApplication springApplication = new SpringApplication(WebApplication.class);
	    springApplication.addListeners(new WebAppListener());
	    springApplication.run(args);		
	}

}
