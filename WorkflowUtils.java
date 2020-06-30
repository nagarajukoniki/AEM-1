package com.tda.apac.core.utils;

import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;

import com.day.cq.commons.Externalizer;


import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WorkflowUtils {

    private static final Logger log = LoggerFactory.getLogger(WorkflowUtils.class);

    private static final String PN_USER_EMAIL = "profile/email";
    private static final String PN_USER_FIRST_NAME = "profile/givenName";
    private static final String PN_USER_LAST_NAME = "profile/familyName";  

    /***
     * Tests whether the payload is a DAM asset or a cq:Page for DAM asset
     * returns all properties at the metadata node for DAM assets for cq:Page
     * returns all properties at the jcr:content node The Map<String, String>
     * that is returned contains string representations of each of the
     * respective properties
     *
     * @param payloadRes the payload as a resource
     * @param sdf        used by the method to transform Date properties into Strings
     *
     * @return Map<String, String> String representation of jcr properties
     */
    public final static Map<String, String> getPayloadProperties(Resource payloadRes, SimpleDateFormat sdf) {
        Map<String, String> emailParams = new HashMap<>();
        if (payloadRes == null) {
            return emailParams;
        }

        // Check if the payload is an asset
        if (DamUtil.isAsset(payloadRes)) {
            // get metadata resource
            Resource mdRes = payloadRes.getChild(JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER);
            return getJcrKeyValuePairs(mdRes, sdf);
        } else {
            // check if the payload is a page
            Page payloadPage = payloadRes.adaptTo(Page.class);

            if (payloadPage != null) {
                return getJcrKeyValuePairs(payloadPage.getContentResource(), sdf);
            }
        }

        return emailParams;
    }

    /**
     * Gets email(s) based on the initiator If it is a user it
     * returns an array with a single email if the path is a group returns an
     * array emails for each individual in the group
     *
     * @param resourceResolver
     * @param principleName    CQ user or group
     *
     * @return String[] of email(s) associated with account
     */
    public final static String[] getEmailAddrsFromUserPath(ResourceResolver resourceResolver,
			String principleName) {
		Set<String> emailList = new HashSet<String>();
		String[] groupNames = principleName.split(";");
		try {
			for (String group : groupNames) {
				log.info("group Name :" + group);
				if (group != null) {
					Authorizable authorizable = getAuthorizable(resourceResolver, group);
					if (authorizable != null) {
						// check if it is a group
						if (authorizable.isGroup()) {
							Resource authRes = resourceResolver.getResource(authorizable.getPath());
							Group authGroup = authRes.adaptTo(Group.class);
							// iterate over members of the group and add emails
							Iterator<Authorizable> memberIt = authGroup.getMembers();
							while (memberIt.hasNext()) {
								Authorizable authnew = memberIt.next();
								String currEmail = getAuthorizableEmail(authnew);
								if (currEmail != null) {
									log.info("membebr email :" + currEmail);
									emailList.add(currEmail);
								}
							}
						} else {
							log.info("Initiator email fetching:");
							// otherwise is an individual user
							String authEmail = getAuthorizableEmail(authorizable);
							if (authEmail != null) {
								log.info("Individual user email :" + authEmail);
								emailList.add(authEmail);
							}
						}
					}
				}
				log.info("group Name:" + group + " group members size:" + emailList.size());
			}
			// }
		} catch (RepositoryException e) {
			log.warn("Could not get list of email(s) for users. {}", e);
		}
		log.info("emailList:" + emailList);
		String[] emailReturn = new String[emailList.size()];
		log.info("emailReturn:" + emailReturn + " emailReturn size:" + emailList.size());
		return emailList.toArray(emailReturn);
	}

    /***
     * Method to add all properties of a resource to Key/Value map of strings
     * only converts dates to string format based on simple date format
     * concatenates String[] into a string of comma separated items all other
     * values uses toString
     *
     * @param resource
     *
     * @return a string map where the key is the jcr property and the value is
     */
    private static Map<String, String> getJcrKeyValuePairs(Resource resource, SimpleDateFormat sdf) {
        Map<String, String> returnMap = new HashMap<>();
        if (resource == null) {
            return returnMap;
        }

        ValueMap resMap = resource.getValueMap();

        for (Map.Entry<String, Object> entry : resMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Calendar) {
                // Date property
                String fmtDate = formatDate((Calendar) value, sdf);
                returnMap.put(entry.getKey(), fmtDate);
            } else if (value instanceof String[]) {
                // concatenate string array
                String strValue = StringUtils.join((String[]) value, ", ");
                returnMap.put(entry.getKey(), strValue);

            } else {
                // all other properties just use default to string
                returnMap.put(entry.getKey(), value.toString());
            }
        }

        return returnMap;
    }

   

   public static String getFirstName(String userId, ResourceResolver resourceResolver){
         String firstname = userId;
         	try {
             Authorizable authorizable =  getAuthorizable(resourceResolver, userId);
             String email = getAuthorizableEmail(authorizable);
             if(email!=null && email.contains("@"))
             {
            	 email = email.substring(0,email.indexOf('@'));
        		if(email.contains("."))
        		{
        			firstname = email.substring(0,email.indexOf('.'));
                }
             }
         	}catch(Exception e){
         		log.info(" Exception while gettign User name "+e.getMessage());
         	}
    	return firstname;
    }
    private static String getAuthorizableEmail(Authorizable authorizable) throws RepositoryException {
        if (authorizable.hasProperty(PN_USER_EMAIL)) {
            Value[] emailVal = authorizable.getProperty(PN_USER_EMAIL);
            return emailVal[0].getString();
        }

        return null;
    }

    /***
     * Format date as a string using global variable sdf
     *
     * @param calendar
     *
     * @return
     */
    public static String formatDate(Calendar calendar, SimpleDateFormat sdf) {

        return sdf.format(calendar.getTime());
    }
    public final static String getFullNameFromUserPath(ResourceResolver resourceResolver,
            String principleName) {
         	String fullName="";
         	try {	
         		if (principleName != null) {
         		Authorizable authorizable =	getAuthorizable(resourceResolver, principleName);
         		if (authorizable != null) {			
         			fullName = getAuthorizablefullName(authorizable);			
         			}
         		}
         	} catch (RepositoryException e) {
         		log.warn("Could not get list of fullName for users. {}", e);
         	}
         	return fullName;
    	}
    private static String getAuthorizablefullName(Authorizable authorizable) throws RepositoryException {
    	String fullName="";
        if (authorizable.hasProperty(PN_USER_FIRST_NAME)) {
            Value[] firstName = authorizable.getProperty(PN_USER_FIRST_NAME);
            fullName= firstName[0].getString();
        }
        if (authorizable.hasProperty(PN_USER_LAST_NAME)) {
            Value[] lastName = authorizable.getProperty(PN_USER_LAST_NAME);
            fullName= fullName+" "+lastName[0].getString();
        }
        log.debug("User fullName:"+fullName);
        return fullName;
    }
    
   
    public static Authorizable getAuthorizable(final ResourceResolver resourceResolver, final String userId) throws RepositoryException {
        UserManager userManager = resourceResolver.adaptTo(UserManager.class);
        return userManager.getAuthorizable(userId);
    }
    /**
     * The available arguments to this process implementation.
     */
    public enum Arguments {
        PROCESS_ARGS("PROCESS_ARGS"),
        /**
         * emailTemplate - process argument
         */
        TEMPLATE("emailTemplate"),

        /**
         * sendTo - process argument
         */
        SEND_TO("sendTo"),
        
        /**
         * Replication Type - to verify workflfow process is identifying  Stage/Publish to Activate Content
         */
        REPLICATION_TYPE("replicationType"),
        
        /**
         * Dispatcher Flush ID  - to verify workflow process is identifying Stage/Publish to Invalidate Cache
         */
        DISPATCHER_FLUSH_AGENT_ID("dispatcherFlushAgentId"),
        
        /**
         * ECMS Flush ID  - to verify workflow process is identifying Stage/Publish to Invalidate Cache
         */
        ECMS_FLUSH_AGENT_ID("ecmsFlushAgentId"),       


        /**
         * dateFormat - process argument
         */
        DATE_FORMAT("dateFormat"),

        /**
         * approver - GroupName
         */
        APPROVER_GROUP_NAME("approverGroup"),
    	
        
        /**
         * project key
         */
        PROJECT("project"),
    	

    	/**
         * akamai environent key
         */
        AKAMAIENV("akamaiEnv");
        
       

        private String argumentName;

        Arguments(String argumentName) {
            this.argumentName = argumentName;
        }

        public String getArgumentName() {
            return this.argumentName;
        }

    }
    /***
     * @param metaData metadata
     * @return the arguments string array
     */
    public static String[] buildArguments(MetaDataMap metaData) {	
    	try {
        String processArgs = metaData.get("PROCESS_ARGS", String.class);
        if (StringUtils.isNotBlank(processArgs)) {  
            return processArgs.split(",");
        } else {
            return new String[0];
        }
    	}catch(Exception e) {
    		log.error("Exception:"+e);
    		
    	}
		return null;
    }
    /***
     * Gets value from workflow process arguments
     *
     * @param key       parameters key value from the process args
     * @param arguments arguments array
     * @return String of the argument value or null if not found
     */
    public static String getValueFromArgs(String key, String arguments[]) {
        final String prefix = key + ":";
        for (String str : arguments) {
            String trimmedStr = str.trim();  
            if (trimmedStr.startsWith(prefix)) {        	
                return trimmedStr.substring((prefix).length());
            }      
        }
        return null;
    }
    

	/***
     * Gets the respective Value by key argument 
     *
     * @param key
     * @param values
     * @return Associated Value by matches key from the values array.
     */
    public static String getValueByKey(String key, String[] values) {
    	log.info("key:"+key);
    	String prefix = "";
    	if(key != null && key.trim().length() > 0) {
    		prefix = key.trim(); 
    		prefix = prefix+"=";
    	} else {
    		log.info("key passed is null (or) empty");
    		return "";
    	}
    	if(values != null && values.length > 0) {
    		for (String str : values) {
                String trimmedStr = str.trim(); 
                if (trimmedStr.startsWith(prefix)) {        	
                    return trimmedStr.substring((prefix).length());
                }      
            }
    	}
        return "";
    }
    
    /***
     * Gets Akamaized url based on http|https rules defined for the site
     *
     * @param project
     * @param path
     * @return String applies regular expression 
     */

	public static String getAkamizedUrl(String site, String path, String match, String subst) {

		if(path != null) {
			return path.replaceAll(match, subst);
		}		
		return path;
	}
	public static String buildExternalLink(String runmodeKey,ResourceResolver resourceResolver) {
		String externalLink = StringUtils.EMPTY;
		try {
		if(null != runmodeKey) {
			Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
			externalLink = externalizer.externalLink(resourceResolver, runmodeKey, "") ;
			log.debug("externalLink is :::::" + externalLink);
		}
		}catch(Exception e) {
			return externalLink;
		}
		return externalLink;
	}

    
}
