/**
 * 
 */
package com.rudetools.otel.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author james101
 *
 */
public class HttpUtils {

	public final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
	 * 
	 */
	public HttpUtils() {
		
	}

	// send to OTEL Collector 
	public static int sendOtlpJsonProto(String endpoint, String payload) throws Throwable {
		
		int retCode = 404;
	    final HttpPost httpPost = new HttpPost(endpoint);

	    
	    final StringEntity entity = new StringEntity(payload);
	    httpPost.setEntity(entity);
	    httpPost.setHeader("Accept", "*/*");
	    httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
	    httpPost.setHeader("User-Agent", "COPXForm/0.0.1");
	    httpPost.setHeader("Content-Type", "application/json");

	    CloseableHttpClient client = HttpClients.createDefault();
	    CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost);
	    
	    retCode = response.getStatusLine().getStatusCode();
	    logger.info("HTTP Response Code = " + retCode);
	    client.close();
		
		return retCode;
	}
	
}
