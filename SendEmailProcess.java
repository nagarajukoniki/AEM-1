package com.tda.apac.core.workflows;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.acs.commons.wcm.AuthorUIHelper;
import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.HistoryItem;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.tda.apac.core.utils.SendEmailConstants;
import com.tda.apac.core.utils.WorkflowUtils;
@Component(name = "APAC Send Email Process", service = WorkflowProcess.class,immediate = true, property = {
"process.label=APAC Send Email Process" },configurationPid = "com.tda.apac.core.workflows.SendEmailProcess")

@Designate(ocd = SendEmailProcess.URLConfiguration.class)
public class SendEmailProcess implements WorkflowProcess {
	
    private static final Logger log = LoggerFactory.getLogger(SendEmailProcess.class);
    public static String StagePreviewURLKey ;
    public static String LivePreviewURLKey ;
    private String defaultStageExternalLink="http://h0001863.associatesys.local:4503";
    private String defaultLivePreviewURLKey="http://h0001863.associatesys.local:4503";
    /* OSGI Configurations */
    @ObjectClassDefinition(name="APAC Publishing Workflow Configurations")
    public @interface URLConfiguration {
    	@AttributeDefinition(
			    name = "StagePreviewURL",
			    description = "Add Runmode key, ex: ecms-StageURL",
			    type = AttributeType.STRING
		    )
		  String ExternalStagePreviewURLRunmodeKey() default "apac-StageURL";
	    @AttributeDefinition(
			    name = "LivePreviewURL",
			    description = "Add Runmode key, ex: ecms-LiveURL",
			    type = AttributeType.STRING
		    )
		  String ExternalLivePreviewURLRunmodeKey() default "apac-LiveURL";
    }
    
    @Activate
	public void activate(URLConfiguration config) {
    	StagePreviewURLKey = config.ExternalStagePreviewURLRunmodeKey();
    	LivePreviewURLKey = config.ExternalLivePreviewURLRunmodeKey();
		log.debug("Key for StagePreviewURL:" + StagePreviewURLKey);
		log.debug("Key for LivePreviewURL:" +  LivePreviewURLKey);
	}

    private String[] urlArray;
    private String[] urlOsgiArray;
    
    /**
     * Service used to get Sling Osgi configurations
     */
    @Reference
    private ConfigurationAdmin configAdmin;

    /**
     * Service used to send the email
     */
    @Reference
    private EmailService emailService;

    /**
     * Service used to generate a link to the payload on author environment
     */
    @Reference
    private AuthorUIHelper authorUIHelper;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * used to generate a link to the payload on publish environment
     */
    @Reference
    private Externalizer externalizer;
    
    public static final String WORKFLOW_MODEL_NAME = "workflowModel";

   
    @Override
    public final void execute(WorkItem workItem, WorkflowSession workflowSession,
            MetaDataMap metaData) throws WorkflowException {
    	
        final WorkflowData workflowData = workItem.getWorkflowData();
        final String type = workflowData.getPayloadType();
        if (!StringUtils.equals(type, "JCR_PATH")) {
            return;
        }
        
        /* Get Stage and Live Urls Sling OSGI Configurations */
        try {
        	Configuration configOsgi = configAdmin.getConfiguration("com.tda.apac.core.workflows.SendEmailProcess");
        	if(configOsgi != null && configOsgi.getProperties() != null) {
        		urlOsgiArray = (String[])configOsgi.getProperties().get("Stage_And_Live_URLMapper");
        		if(urlOsgiArray == null || urlOsgiArray.length == 0) {
        			/* Default to osgi configuration unless sling environment specific configurations presented*/
        			urlOsgiArray = urlArray;
        		}
        	}
        	
        } catch(IOException ex) {
        	/*Assign to osgi configuration if any exception*/
        	urlOsgiArray = urlArray;
        }
      
        String[] args = buildArguments(metaData);

        /* process arguments */
        String emailTemplate = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.TEMPLATE.getArgumentName(), args);
        String approverGroup = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.APPROVER_GROUP_NAME.getArgumentName(), args);
       
        if (emailTemplate == null) {            
            emailTemplate = "/etc/notification/email/apac/default-email-template.txt";
        }  
        
        /* set date format to be used in emails */
        String sdfParam = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.DATE_FORMAT.getArgumentName(), args);
        SimpleDateFormat sdf = getSimpleDateFormat(sdfParam);
        
        /* Get the path to the JCR resource from the payload */
        final String payloadPath = workflowData.getPayload().toString();
        
        /* Get ResourceResolver */
        final Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION,
                workflowSession.getSession());
        ResourceResolver resourceResolver = null;

        try {
           resourceResolver = resourceResolverFactory.getResourceResolver(authInfo);
            Resource payloadRes = resourceResolver.getResource(payloadPath);
            ResourceResolver resolverResolver = payloadRes.getResourceResolver();
            String StageExternalLink = WorkflowUtils.buildExternalLink(StagePreviewURLKey,resourceResolver);
        	String LiveExternalLink = WorkflowUtils.buildExternalLink(LivePreviewURLKey,resourceResolver);
        	 log.info("StageExternalLink:"+StageExternalLink);
             log.info("LiveExternalLink:"+LiveExternalLink);
             if (StringUtils.isBlank(StageExternalLink)) {
            	 StageExternalLink= defaultStageExternalLink;
             }
             if (StringUtils.isBlank(LiveExternalLink)) {
            	 LiveExternalLink= defaultLivePreviewURLKey;
             }            
            String fullName= WorkflowUtils.getFullNameFromUserPath(resolverResolver, workItem.getWorkflow().getInitiator()); 
            String workflowTitle=workItem.getWorkflowData().getMetaDataMap().get("workflowTitle")!=null?workItem.getWorkflowData().getMetaDataMap().get("workflowTitle").toString():"";
            Map<String, String> emailParams =
                    getEmailParams(workItem, workflowSession, sdf, payloadPath, payloadRes);
            emailParams.put(SendEmailConstants.WF_USER_FULL_NAME, fullName);  
            log.info("is Asset DAM:"+DamUtil.isAsset(payloadRes));
            if (DamUtil.isAsset(payloadRes)) {
            	 emailParams.put(SendEmailConstants.STAGE_LINK, StageExternalLink+payloadPath.substring(1)); 
                 emailParams.put(SendEmailConstants.PUBLISH_LINK, LiveExternalLink+payloadPath.substring(1));	           
            }else {
            	emailParams.put(SendEmailConstants.STAGE_LINK, StageExternalLink+payloadPath.substring(1)+".html"); 
 	            emailParams.put(SendEmailConstants.PUBLISH_LINK, LiveExternalLink+payloadPath.substring(1)+".html");
            }
            emailParams.put(SendEmailConstants.WF_TITLE,workflowTitle);
            if("initiator".equals(approverGroup))
            	approverGroup=workItem.getWorkflow().getInitiator();
           
            log.info("email params:"+ approverGroup);
            approverGroup=approverGroup.replaceAll("initiator",  workItem.getWorkflow().getInitiator());
            log.info("email params:"+ approverGroup);
            log.info("Email Template:"+emailTemplate);
            
            /* get email addresses based on CQ user or group */
            String[] emailTo = getEmailAddrs(approverGroup, payloadRes);
           if(emailTo.length>0) {
            List<String> failureList=  emailService.sendEmail(emailTemplate, emailParams, emailTo);
            if (failureList.isEmpty() && failureList != null) {
                log.info("Email sent successfully to {} recipients", emailTo.length);
            } else {
                StringBuilder emailBuilder = new StringBuilder();
                for (String emailToIndividualAddress : emailTo) {
                    emailBuilder.append(emailToIndividualAddress).append('|');
                }
                log.error("Email sent failed to {} ", emailBuilder.toString());
            } 
        }
        } catch (LoginException e) {
            log.error("Could not acquire a ResourceResolver object from the Workflow Session's JCR Session: {}", e);
        } finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    /**
     * This method returns the email Params that can be used in the email Template.
     *
     * @param workItem        workItem from the workflow.
     * @param workflowSession workflow session
     * @param sdf             simple date formatter
     * @param payloadPath     payload path as part of the workflow
     * @param payloadRes      payload resource
     * @return map of email params to be sent to the email template
     * @throws WorkflowException
     */
    private Map<String, String> getEmailParams(WorkItem workItem, WorkflowSession workflowSession,
            SimpleDateFormat sdf, String payloadPath, Resource payloadRes)
            throws WorkflowException {
    	
        /* Email Parameter map */
        Map<String, String> emailParams = new HashMap<>();

        /* Set jcr path */
        emailParams.put(org.apache.jackrabbit.JcrConstants.JCR_PATH, payloadPath);

        /* Get Payload params */
        Map<String, String> payloadProp = WorkflowUtils.getPayloadProperties(payloadRes, sdf);
        if (payloadProp != null) {
        	log.info("Map payloadProp:"+payloadProp);
            emailParams.putAll(payloadProp);
        }

        /* Get Url params */
        Map<String, String> urlParams = getURLs(payloadRes);
        if (urlParams != null) {
            emailParams.putAll(urlParams);
        }
        addCommentsToEmailParams(workItem, workflowSession, emailParams);


        /* Get Additional Parameters to add */
        Map<String, String> wfParams = getAdditionalParams(workItem);
        if (wfParams != null) {
            emailParams.putAll(wfParams);
        }

        /* Get the current date and add to email Params. */
        String fmtDate = WorkflowUtils.formatDate(Calendar.getInstance(), sdf);
        emailParams.put("emailTime", fmtDate);
        return emailParams;
    }

    /**
     * This method gets the last comment provided by the workflow user while completing that step
     * and place that in the emailParams to use it in the emailTemplate.
     *
     * @param workItem        workItem of the workflow
     * @param workflowSession workflow session
     * @param emailParams     email params in which comments needs to be added
     * @throws WorkflowException
     */
    private void addCommentsToEmailParams(WorkItem workItem, WorkflowSession workflowSession,
            Map<String, String> emailParams) throws WorkflowException {
    	
        /* Get the comments section from the last completed step and send it in an email.
           Get the current workflow. */
        List<HistoryItem> historyList = workflowSession.getHistory(workItem.getWorkflow());
        int listSize = historyList.size();
        HistoryItem lastItem = historyList.get(listSize - 1);
        String comment = lastItem.getComment();
        if (StringUtils.isNotBlank(comment)) {
            emailParams.put("comment", comment);
            log.info("Previous Workflow Comment = {}", comment);
        } else {
            emailParams.put("comment", "");
            log.info("Previous Workflow Comment = null or ''");
        }
    }

    /***
     * Gets a String[] of email addresses to send the email to. By default calls
     * {@link com.adobe.acs.commons.email.process.impl.SendTemplatedEmailUtils#getEmailAddrsFromUserPath(ResourceResolver, String)}
     * Protected so that it can be overridden by implementing classes to add
     * unique logic to where emails are routed to.
     *
     * @param workItem        the current WorkItem in the workflow
     * @param payloadResource the current payload as a Resource
     * @param args            process arguments configured by the workflow step
     * @return String[] of email addresses
     */
    protected String[] getEmailAddrs(String groupName, Resource payloadResource) {
        ResourceResolver resolver = payloadResource.getResourceResolver();
        return WorkflowUtils.getEmailAddrsFromUserPath(resolver, groupName);
    }

    /**
     * Returns a Map<String, String> of additional parameters that will be added
     * to the full list of email parameters that is sent to the EmailService. By
     * default adds the Workflow Title:
     * {@link com.adobe.acs.commons.email.process.impl.SendTemplatedEmailConstants#WF_MODEL_TITLE
     * WF_MODEL_TITLE} and adds the Workflow Step Title:
     * {@link com.adobe.acs.commons.email.process.impl.SendTemplatedEmailConstants#WF_STEP_TITLE
     * WF_STEP_TITLE}
     * {@link com.adobe.acs.commons.email.process.impl.SendTemplatedEmailConstants#WF_INITIATOR
     * WF_INITIATOR} Protected so that implementing classes can override and
     * add additional parameters.
     *
     * @param workItem workItem from the workflow
     * @return map of additional parameters to be added to email params
     */
    protected Map<String, String> getAdditionalParams(WorkItem workItem) throws WorkflowException {
        Map<String, String> wfParams = new HashMap<>();
        String workflowTitle = workItem.getNode().getTitle();
        String workflowEmailSubject = "Publish to Stage";
        wfParams.put(SendEmailConstants.WF_EMAIL_SUBJECT, workflowEmailSubject);
        wfParams.put(SendEmailConstants.WF_STEP_TITLE, workflowTitle);
        wfParams.put(SendEmailConstants.WF_MODEL_TITLE,
                workItem.getWorkflow().getWorkflowModel().getTitle());
        /* Set workflow initiator */
        wfParams.put(SendEmailConstants.WF_INITIATOR, workItem.getWorkflow().getInitiator());
        return wfParams;
    }

    /***
     * Uses the AuthorUIHelper to generate links to the payload on author Uses
     * Externalizer to generate links to the payload on publish
     *
     * @param payloadRes pay load resource
     * @return the url params with the resolved page path or asset path.
     */
    private Map<String, String> getURLs(Resource payloadRes) {
    	
        Page page;
        Asset asset;
        Map<String, String> urlParams = new HashMap<>();
        if (payloadRes == null) {
            return urlParams;
        }

        String payloadPath = payloadRes.getPath();
        ResourceResolver resolver = payloadRes.getResourceResolver();
        String authorInboxUrl =   externalizer.authorLink(resolver,"/aem/inbox");
                  urlParams.put(SendEmailConstants.AUTHOR_INBOX_LINK, authorInboxUrl);
                  log.info("authorUIHelper:"+authorInboxUrl);
                  log.info("payloadPath:"+payloadPath);

        page=resolver.getResource(payloadPath).adaptTo(Page.class);       
        if (DamUtil.isAsset(payloadRes)) {
        	asset=resolver.getResource(payloadPath).adaptTo(Asset.class);
            /* add publish url */
            String publishUrl = externalizer.publishLink(resolver, payloadPath);
            urlParams.put(SendEmailConstants.PUBLISH_LINK, publishUrl);
            if(asset.getName()!=null){
             urlParams.put(SendEmailConstants.ASSET_PAGE, asset.getName());
			}
            String assetDetailsUrl =
                    authorUIHelper.generateEditAssetLink(payloadPath, true, resolver);
            urlParams.put(SendEmailConstants.AUTHOR_LINK, assetDetailsUrl);


        } else {
            /* add publish url */
            String publishUrl = externalizer.publishLink(resolver, payloadPath + ".html");
            urlParams.put(SendEmailConstants.PUBLISH_LINK, publishUrl);
            urlParams.put(SendEmailConstants.ASSET_PAGE, page.getName());
            String pageDetailsUrl =
                    authorUIHelper.generateEditPageLink(payloadPath, true, resolver);
            urlParams.put(SendEmailConstants.AUTHOR_LINK, pageDetailsUrl);


        }

        return urlParams;
    }

    /***
     * @param metaData metadata
     * @return the arguments string array
     */
    private String[] buildArguments(MetaDataMap metaData) {
        /* the 'old' way, ensures backward compatibility */
        String processArgs = metaData.get(WorkflowUtils.Arguments.PROCESS_ARGS.getArgumentName(), String.class);
        if (StringUtils.isNotBlank(processArgs)) {
            return processArgs.split(",");
        } else {
            return new String[0];
        }
    }

    /***
     * Set the format to be used for displaying dates in the email Defaults to
     * format of 'yyyy-MM-dd hh:mm a'
     *
     * @param formatString workflow process argument to override default format
     * @return SimpleDateFormat that will be used to convert jcr Date properties
     * to Strings
     */
    private SimpleDateFormat getSimpleDateFormat(String formatString) {
        SimpleDateFormat defaultFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        if (StringUtils.isBlank(formatString)) {
            return defaultFormat;
        }

        try {
            return new SimpleDateFormat(formatString);
        } catch (IllegalArgumentException e) {
            /* invalid pattern */
            return defaultFormat;
        }
    }
    
    /***
     * Gets the respective URL by key argument 
     *
     * @param key
     * @return Associated URL by Key value passed if it exists in the configuration, otherwise empty
     */
    private String getURLByKey(String key) {
    	log.info("key:"+key);
    	String prefix = "";
    	if(key != null && key.trim().length() > 0) {
    		prefix = key.trim(); 
    		prefix = prefix+"=";
    	} else {
    		log.info("key passed is null (or) empty");
    		return "";
    	}
    	if(urlOsgiArray != null && urlOsgiArray.length > 0) {
    		for (String str : urlOsgiArray) {
                String trimmedStr = str.trim(); 
                if (trimmedStr.startsWith(prefix)) {        	
                    return trimmedStr.substring((prefix).length());
                }      
            }
    	}
        return "";
    }
}
