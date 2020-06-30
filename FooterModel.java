package com.tda.apac.core.models;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMMode;
@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(FooterModel.class);
	
	@ValueMapValue(name = "fileReference", injectionStrategy = InjectionStrategy.DEFAULT)
	private String fileReference = "";
	
	@ValueMapValue(name = "logoUrl", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoUrl = "";

	@ValueMapValue(name = "logoTitle", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoTitle  = "";
	
	@ValueMapValue(name = "logoAltText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoAltText  = "";
	
	@ValueMapValue(name = "callUsLabel", injectionStrategy = InjectionStrategy.DEFAULT)
	private String callUsLabel  = "";

	@ValueMapValue(name = "mobileNum", injectionStrategy = InjectionStrategy.DEFAULT)
	private String mobileNum  = "";
	
	@ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
	private Resource socialicons;
	
	@ValueMapValue(name = "logoLinkOptions", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoLinkOptions  = "";

	private WCMMode mode;
	
	@Inject
	private SlingHttpServletRequest request;
	
	@Inject
	private Resource resource;

    public String getResourcePath() {
    	 String requestResourcePath = request.getResource().getPath();
    	 LOGGER.info("requestResourcePath is::::::"+requestResourcePath);
    	 requestResourcePath = requestResourcePath.substring(requestResourcePath.lastIndexOf("/") + 1);
    	return requestResourcePath;
    }


	/**
	 * The post construct initiates the modal class. 
	 * 
	 * @return
	 */
	@PostConstruct
	protected void init() {
		LOGGER.debug("In init method");
		mode = WCMMode.fromRequest(request);
	}

        
    public String getLogoAnchorTag() { 	
    	String imgTag = TDACommonUtil.getImageTag(fileReference, logoAltText, "main-footer-logo-image svg", fileReference);
    	TDACommonUtil util = new TDACommonUtil();
    	String anchorTag = TDACommonUtil.getAnchorTag(imgTag, logoUrl, logoTitle, util.getRedirectWindow(logoLinkOptions), null, null, null, null);
    	return anchorTag;
    }
    
    public String getCallUsTag() { 
    	
    	if(TDACommonUtil.isEmpty(callUsLabel)) {
    		callUsLabel="";
    	}
    	if(TDACommonUtil.isEmpty(mobileNum)) {
    		mobileNum="";
    	} 
    	StringBuffer callUsTag = new StringBuffer();
    	callUsTag.append(callUsLabel);
    	callUsTag.append(" ");
    	callUsTag.append("<strong>"+mobileNum+"</strong>");
    	
    	return callUsTag.toString();
    }

    public String getSocialIconTag(String linkText,  String linkUrl, String linkTitle, String dataNetwork, String target) {
    	StringBuffer sbSocialTag = new StringBuffer();
    	TDACommonUtil util = new TDACommonUtil();
    	sbSocialTag.append("<li class=\"main-footer-social-item\">");
    	sbSocialTag.append(TDACommonUtil.getAnchorTag(linkText, linkUrl, linkTitle, util.getRedirectWindow(target), "main-footer-social-link", dataNetwork, "social_follow", "social_follow"));
    	sbSocialTag.append("</li>");
    	return sbSocialTag.toString();
    }
    
    public String getSocialIcons() {
		StringBuffer sbSocialIcons = new StringBuffer();
		try {
			Node currentNode = null;
			if(socialicons != null) {
				currentNode = socialicons.adaptTo(Node.class);
			}
			
			if(currentNode!=null) {
				if(currentNode.hasNodes()) {
					NodeIterator childNodes =currentNode.getNodes();
					while (childNodes.hasNext()) {
						Node socialicon = (Node) childNodes.next();
						sbSocialIcons.append(getSocialIconTag(getSvgData(getNodeValue(socialicon, "iconImage")),getNodeValue(socialicon, "socialLinkUrl"), getNodeValue(socialicon, "socialLinkTitle"), getNodeValue(socialicon, "socialNetworkName"), getNodeValue(socialicon, "logoLinkOptions")));					
					}
					
				}
			}
		} catch(Exception ex) {
			LOGGER.error("Exception while retrieving social icon::::");
		}
		return sbSocialIcons.toString(); 
    }
    
    public String getNodeValue(Node node, String property) {
    	String value = "";
    	try {
			if(node != null && node.getProperty(property) != null) {
				value = node.getProperty(property).getString();		
			}
		} catch (RepositoryException re) {
			LOGGER.error("Exception while getting the node value" + re);
		}
    	return value;
    }
  
    
    
    public String getSvgData(String svgImage)  {
		String svgData = "";
		try {		
			if(!TDACommonUtil.isEmpty(svgImage)) {
				String nodePath = svgImage + "/jcr:content/renditions/original";
				if(request.getResourceResolver() != null) {
					resource = request.getResourceResolver().getResource(nodePath);
					LOGGER.info("init Header model resource..."+resource);
					Node currentNode = null;
					if (resource != null && resource.adaptTo(Node.class).hasNode("jcr:content")) {
						currentNode = resource.adaptTo(Node.class).getNode("jcr:content");
						LOGGER.info("init Header model currentNode...."+currentNode);
						if (currentNode.hasProperty("jcr:data")) {
							svgData = currentNode.getProperty("jcr:data").getString();
						}
					}
				}
			}
		} catch(Exception ex) {
			LOGGER.error("Exception while getting svg data for the file:::"+svgImage);
		}
		return svgData;
	}
    
    
}
