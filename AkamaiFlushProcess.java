package com.tda.apac.core.workflows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import org.apache.felix.scr.annotations.Component;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;
import com.tda.apac.core.utils.WorkflowUtils;
@Component(name = "APAC Akamai Flush Process-Component", service = WorkflowProcess.class,immediate = true, property = {
                                "process.label=APAC Akamai Flush Process"},configurationPid = "com.tda.apac.core.workflows.AkamaiFlushProcess")

public class AkamaiFlushProcess implements WorkflowProcess {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Service used to get Sling Osgi configurations
	 */
	@Reference
	private ConfigurationAdmin configAdmin;
	
	@Reference
    private ResourceResolverFactory resourceResolverFactory;
	
	private String hostName;
	
	private String akamaiUrl;
	
	private static final String matchAndSubst = "/content/(.[^/]*)/en_US/index(.html$|/)(.*)?::/$1/$3";
	
	@Override
	public void execute(WorkItem workItem, WorkflowSession wfsession, MetaDataMap metaData) throws WorkflowException {
		int result = 1;  /*Default to failed (0-success, 1-failed)*/
		List<String> files = new ArrayList<String>();
		try {
			 log.info("Start of APAC Akamia Flush Process Workflow");
			
			 String qaTestFlag = null;
		     String qaAkamaiTestUrl = null;
		     String akamaiStageUrl = null;
		     String akamaiProductionUrl = null;
		     String stageHostName =  null;
		     String productionHostName = null;
		     String[] regExps = null;
			
			 String[] args = WorkflowUtils.buildArguments(metaData);
			 /* process arguments */
		     String akamaiEnv = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.AKAMAIENV.getArgumentName(), args);
		     log.info("Akmai Environment:::::::"+akamaiEnv);
		     /* Get the site which the current workflow belongs to.*/
		     String projectKey = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.PROJECT.getArgumentName(), args);
		     log.info("Workflow Site:::::::"+projectKey);
		     
		     if(akamaiEnv == null) {
		    	 akamaiEnv = "stage";
		     }
		    
		    
		     /* Get Stage and Live akamai configuration using Sling OSGI Configurations */
	         try {
	        	Configuration configOsgi = configAdmin.getConfiguration("com.tda.apac.core.workflows.AkamaiFlushProcess");
	        	if(configOsgi != null && configOsgi.getProperties() != null) {
	        		akamaiStageUrl = (String)configOsgi.getProperties().get("stageAkamaiURL");
	        		akamaiProductionUrl = (String)configOsgi.getProperties().get("productionAkamaiURL");
	        		/* Get site specific configurations if exists, otherwise default configurations.*/
	        		if(projectKey != null && projectKey.length() > 0) {
		        		stageHostName = (String)configOsgi.getProperties().get(projectKey+"-stageHostName");
		        		productionHostName = (String)configOsgi.getProperties().get(projectKey+"-productionHostName");   
	        		} else {
	        			stageHostName = (String)configOsgi.getProperties().get("stageHostName");
		        		productionHostName = (String)configOsgi.getProperties().get("productionHostName"); 
	        		}
	        		qaTestFlag = (String)configOsgi.getProperties().get("qaTestFlag");
	        		qaAkamaiTestUrl = (String)configOsgi.getProperties().get("qaTestAkamaiUrl");
	        		regExps = (String[])configOsgi.getProperties().get("regularExpression");
	        	}	
	         } catch(IOException ex) {
	        	log.error("Exception while getting APAC sling osgi configuration:::"+ex);
	         }
	         
	        /* get current page path with html extension*/
	        String pagePath = workItem.getWorkflowData().getPayload().toString() + ".html";
	        log.info("pagePath::::"+pagePath);
	        
	        /* Add the file to be purged based on akamai environment (stage or production) */
	        if("production".equals(akamaiEnv)) {
	        	hostName = productionHostName;
	        	akamaiUrl = akamaiProductionUrl;
	        } else {
	        	hostName = stageHostName;
	        	akamaiUrl = akamaiStageUrl;
	        }
	       
	        log.info("akamaiUrl::::::::"+akamaiUrl);
	        log.info("hostName:::::::::"+hostName);
	        log.info("qaTestFlag:::::"+qaTestFlag);
	        
	        if(hostName == null) {
	        	hostName = "";
	        }
	        
	        if(akamaiUrl == null) {
	        	akamaiUrl = "";
	        }
	      
	        /* Get ResourceResolver */
	        final Map<String, Object> authInfo = new HashMap<>();
	        authInfo.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION,
	        		wfsession);
	        ResourceResolver resourceResolver = null;
	        resourceResolver = resourceResolverFactory.getResourceResolver(authInfo);
    		     
	        
	        String matchRegExp = WorkflowUtils.getValueByKey(projectKey+"-matchAndSubstitution", regExps);
	        matchRegExp =  (matchRegExp != null && matchRegExp.contains("::")) ? matchRegExp : WorkflowUtils.getValueByKey("matchAndSubstitution", regExps);
	        matchRegExp =  (matchRegExp != null && matchRegExp.contains("::")) ? matchRegExp : matchAndSubst;
	        
	        log.info("Match Regular Expression::"+matchRegExp);
	        String[] splitArray = matchRegExp.split("::");
	        /* As akamai is not configured for lower level environment (dev & qa), 
	         * Introduced a flag for QA people to test the desired akamai and a site url.
	         * If qa test flag is on configured test file will be added to the purge list. Other wise, the current page will be added to purge list.
	         * Defaulted to current page path.
	         */
	        if("true".equals(qaTestFlag)) {
	        	files.add(qaAkamaiTestUrl);
	        } else {

	        	/* Get mapped url based on out bound rules defined in the system*/
	        	String pagePathMappedUrl = WorkflowUtils.getAkamizedUrl(projectKey, pagePath, splitArray[0], splitArray[1]); 
	        	log.info("Akamized Url::::::::::::::::::::"+pagePathMappedUrl);
     	
	        	if(pagePathMappedUrl != null && pagePathMappedUrl.trim().length() > 0) {
	        		pagePathMappedUrl = hostName + pagePathMappedUrl;
	        	}
	        	
	        	if(pagePathMappedUrl == null) {
	        		pagePathMappedUrl = "";
	        	} 
	            files.add(pagePathMappedUrl);        	
	        }
		
			/* Invoke akamai purge*/
			boolean purgeSuccessful = purgeFiles(files);
			log.info("Is Purge Successful :::::" + purgeSuccessful); 
			
			/* assign result to 0 if success, otherwise 1 */
			result = purgeSuccessful ? 0 : 1;										
			log.info("final result is::::::"+ result);	
			
			log.info("End of Akamia Flush Process Workflow");
		}catch (Exception e) {
			 /*assign result to 1 if exception */
			result = 1;
			log.error("exception:" + e);
		}
	}
	
	
	/**
     * The method returns the mapped url based on rules
     * 
     * @param pagePath
     * @return mapped path.
     */
	/*private String mappedUrl(String pagePath) {
		
		return pagePath.replace("/content/workplace/en_US/index", "/workplace");
	}*/

	/**
     * The method purge the akamai cache for the list of files passed.
     * 
     * @param files
     * @return true if purge is successful, otherwise false.
     */
	private boolean purgeFiles(List<String> files) throws Exception{
		/* Build credentials for HTTP Client */
		ClientCredential credential = ClientCredential.builder()
	 	        .accessToken("akab-i7jgpatth3ttxdcx-mtjx4wasikxnhh2l")
	 	        .clientToken("akab-ssofoz7jsx6kllwl-g3gxfany7pi3bdiz")
	 	        .clientSecret("jLuChJcGdzyB9RHLBEjzgS4AI/CvTw3VBUkt2GsHqO0=")
	 	        .host("akab-rhsafdiweg5kjvvy-upq5dnlep3wvrz5t.purge.akamaiapis.net")
	 	        .build();
		
		/* Build HTTP Client */
		HttpClient client=HttpClientBuilder.create()
				.addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
				.setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
				.setSSLContext(trustAllCertificates())
				.setSSLHostnameVerifier(trustAllHosts())
				.build();

		
		 /*Add each file name into JSON array*/
	    JSONArray list = new JSONArray(); 
	    for (String file : files) {
			list.put(file);
		}
	    
		/* Create JSON object and add JSON array of filenames to object */
		JSONObject obj = new JSONObject();
	    obj.put("objects", list);
		String JsonData=obj.toString();
		
		/* log json data */
		log.info("json data is : "+JsonData);
		log.info("akamai url to post:::::"+akamaiUrl);
		
		/* Create HTTP Post and execute */
		 
	   // HttpPost request = new HttpPost("https://"+"akab-rhsafdiweg5kjvvy-upq5dnlep3wvrz5t.purge.akamaiapis.net"+"/ccu/v3/invalidate/url/staging");
	    HttpPost request = new HttpPost(akamaiUrl);
	    request.setHeader("Content-Type", "application/json");
	    request.setHeader("Accept", "application/json");
	    request.setEntity(new StringEntity(JsonData));
	    HttpResponse resp=client.execute(request);
	    
		 /*Log Post parameters message*/
	    log.info("Post Parameters: "+JsonData);
	    log.info("Response code: "+resp.getStatusLine().getStatusCode());
	    
		/* Print results */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
	    String line="";
	    StringBuffer result = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		log.info("Results from APAC akamai:"+result.toString());
	    
		/* Return true or false (for success) */
	    return resp.getStatusLine().getStatusCode() == 201;
	}
	
	/**
     * The method trusts all the hosts by implementing Hostname Verifier anonymously whose verify method true always irrespective of session.
     * 
     * @return 
     */
	private HostnameVerifier trustAllHosts() {
	    return new HostnameVerifier() {
	        public boolean verify(String s, SSLSession sslSession) {
	            return true;
	        }
	    };
	}
	
	/**
     * Set up a TrustManager that trusts everything
     * 
     * @return SSLContext
     */
	private SSLContext trustAllCertificates() {
		try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                }
            }}, new SecureRandom());
            return sslContext;
        } catch (KeyManagementException e1) {
            throw new RuntimeException(e1);
        } catch(NoSuchAlgorithmException e2){
        	throw new RuntimeException(e2);
        }
	}
}
