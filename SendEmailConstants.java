package com.tda.apac.core.utils;
/**
* Class to store SendEmail constants.
*
* @author Murali Komatineni
*/
public class SendEmailConstants {
    
   
     /*-----------------------------------------------------
       TDA WORKFLOW CONSTANTS.
       -----------------------------------------------------*/
    /**
     * the title of the current workflow title To be used in the template as:
     * <code>${wfTitle}</code>
     */
    public static final String WF_TITLE = "wfTitle";

    /**
     * the title of the current workflow model To be used in the template as:
     * <code>${wfModelTitle}</code>
     */
    public static final String WF_MODEL_TITLE = "wfModelTitle";

    /**
     * the title of the current step in the workflow To be used in the template
     * as: <code>${wfStepTitle}</code>
     */
    public static final String WF_STEP_TITLE = "wfStepTitle";

    /**
     * the email subject of the current step in the workflow To be used in the template
     * as: <code>${wfEmailSubject}</code>
     */
    public static final String WF_EMAIL_SUBJECT = "wfEmailSubject";

    /**
     * name of the workflow initiator To be used in the template
     * as: <code>${wfInitiator}</code>
     */
    public static final String WF_INITIATOR = "wfInitiator";
    
    /**
     * absolute URL string to the payload on the author environment includes the
     * editor extension, i.e 'cf#' or 'editor.html' for pages 'damadmin#' or
     * 'assetdetails.html' for assets To be used in the template as:
     * <code>${authorLink}</code>
     */
    public static final String AUTHOR_LINK = "authorLink";
    
    /** name of author Inbox Link to be used in the template */

    public static final String AUTHOR_INBOX_LINK = "authorInBoxLink";

    
    /**
     * name of the workflow initiator fullName To be used in the template
     * as: <code>${fullName}</code>
     */
    public static final String WF_USER_FULL_NAME = "wfInitiatorFullName";
   
    public static final String PUBLISH_LINK = "publishLink";
    
    public static final String STAGE_LINK = "stageLink";
    
    public static final String ASSET_PAGE = "AssetOrPage";

}
