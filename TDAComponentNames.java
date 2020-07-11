package com.tda.common.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMMode;

@Model(adaptables =  { SlingHttpServletRequest.class, Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TDAComponentNames {
	/**
	 * The getter wraps configuration message of the FAQ Component for the authors in edit mode. Defaulted to empty.
	 * 
	 * @return configure message if edit mode, otherwise empty.
	 */
	 private static final Logger LOGGER = LoggerFactory.getLogger(TDAComponentNames.class);
	String editMessage = "";
	protected WCMMode mode;
	@Inject
	protected SlingHttpServletRequest request;
	
	@SlingObject
	protected ResourceResolver resourceResolver;
	
	@ValueMapValue(name = "urlnewwindow", injectionStrategy = InjectionStrategy.DEFAULT)
	private String isPageNewWin = "";
	
	
	@PostConstruct
	public void initialize(){
		try {
			mode = WCMMode.fromRequest(request);
		}catch(Exception e){
			LOGGER.info("Exception :"+e);
		}
	}
	
	public String getEditMessage() {
		try {
		if (TDACommonUtil.isEditMode(mode)) {
			String componentPath=request.getRequestPathInfo().getResourcePath();
				editMessage=TDACommonUtil.getComponentTitle(componentPath, resourceResolver);
				editMessage = TDACommonUtil.wrapEditMessage("Configure "+editMessage);
				LOGGER.info("editMessage: "+editMessage);	
		}
		}catch(Exception e){
			LOGGER.info("Exception :"+e);
			return editMessage;
		}
		return editMessage;
	}	
	/**
     * The getter method retrieves the page path target window based on the checkbox associated with the resource. Defaulted to empty.
     * 
     * @return Title target window. 
     */
	public String getPagePathTarget() {	
		LOGGER.info("isPageNewWin#############: "+isPageNewWin);	
		return "false".equals(isPageNewWin) ? "_self" : "_blank";
	}
}
