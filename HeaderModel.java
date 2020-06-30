package com.tda.apac.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.tda.apac.core.beans.HeaderNavigationBean;
@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

	final static Logger logger = LoggerFactory.getLogger(HeaderModel.class);

	@Inject
	private SlingHttpServletRequest request;
	
	Resource primayNavList = null;
	String primayNavListString = "";
	TDACommonUtil util=new TDACommonUtil();

	public String getResourcePath() {
		String requestResourcePath = request.getResource().getPath();
		logger.info("requestResourcePath is::::::" + requestResourcePath);
		requestResourcePath = requestResourcePath.substring(requestResourcePath.lastIndexOf("/") + 1);
		return requestResourcePath;
	}

	@ValueMapValue(name = "openNewAccounText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String openNewAccounText;

	@ValueMapValue(name = "openNewAccountUrl", injectionStrategy = InjectionStrategy.DEFAULT)
	private String openNewAccountUrl;
	
	@ValueMapValue(name = "accountTarget", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accountTarget;

	@ValueMapValue(name = "logoHref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoHref;

	@ValueMapValue(name = "fileReference", injectionStrategy = InjectionStrategy.DEFAULT)
	private String fileReference;

	@ValueMapValue(name = "loginText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String loginText;

	@ValueMapValue(name = "loginHref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String loginHref;
	
	@ValueMapValue(name = "logoLink", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoLink;
	
	
	@ValueMapValue(name = "loginTarget", injectionStrategy = InjectionStrategy.DEFAULT)
	private String loginTarget;

	@ValueMapValue(name = "logoAltText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoAltText;
	
	@ValueMapValue(name = "logoTarget", injectionStrategy = InjectionStrategy.DEFAULT)
	private String logoTarget;
	
	@ValueMapValue(name = "buttonText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String buttonText;

	@ValueMapValue(name = "buttonhref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String buttonhref;
	
	@ValueMapValue(name = "buttonTarget", injectionStrategy = InjectionStrategy.DEFAULT)
	private String buttonTarget;


	@Optional
	private List<HeaderNavigationBean> primaryNavigationList;

	public String getLoginText() {
		return loginText;
	}
	
	public String getLoginTarget() {
		return util.getRedirectWindow(loginTarget);
	}
	
	public String getLoginHref() {
		return loginHref;
	}

	public String getLogoAltText() {
		return logoAltText;
	}
	
	public String getLogoTarget() {
		return util.getRedirectWindow(logoTarget);
	}

	public String getButtonText() {
		return buttonText;
	}

	public String getButtonhref() {
		return buttonhref;
	}
	
	public String getButtonTarget() {
		TDACommonUtil util=new TDACommonUtil();
		return util.getRedirectWindow(buttonTarget);
	}
	
	public String getFileReference() {
		return fileReference;
	}

	public String getLogoHref() {
		return TDACommonUtil.getValidPath(logoHref);
	}

	public String getOpenNewAccounText() {
		return openNewAccounText;
	}

	public String getOpenNewAccountUrl() {
		return TDACommonUtil.getValidPath(openNewAccountUrl);
	}
	
	public String getAccountTarget() {
		return TDACommonUtil.getValidPath(accountTarget);
	}

	
	public String getLogoLink() {
		return TDACommonUtil.getValidPath(logoLink);
	}

	@Inject
	private PageManager pageManager;

	

	@PostConstruct
	protected void init() throws RepositoryException {
		logger.info("request.getResource().getPath():");
		primayNavListString = request.getResource().getPath() + "/primayNavSections";
		primayNavList = request.getResourceResolver().getResource(primayNavListString);	
		logger.info("primayNavList:"+primayNavList);
		loginHref=TDACommonUtil.getValidPath(loginHref);
	}
	public List<HeaderNavigationBean> getPrimaryNavigationList() throws PathNotFoundException, RepositoryException {
		primaryNavigationList = new ArrayList<HeaderNavigationBean>();

		if (primayNavList != null) {
			Iterator<Resource> primaryLinkResource = primayNavList.listChildren();
			while (primaryLinkResource.hasNext()) {
				HeaderNavigationBean navModel = new HeaderNavigationBean();
				Node node = primaryLinkResource.next().adaptTo(Node.class);
				String linkText = null;
				String linkUrl = null;
				String linkTarget = null;
				if (node != null) {
					try {
						if (node.hasProperty("linkText")) {
							linkText = node.getProperty("linkText").getString();
						}
						if (node.hasProperty("primaryTarget")) {
							linkTarget = node.getProperty("primaryTarget").getString();
							linkTarget = util.getRedirectWindow(linkTarget);
						}
						if (node.hasProperty("linkUrl")) {
							linkUrl = node.getProperty("linkUrl").getString();
							Page page = pageManager.getPage(linkUrl);
							logger.info("Page......." + page);
							if (page != null) {
								navModel.setPage(page);
							}							
							linkUrl=TDACommonUtil.getValidPath(linkUrl);
						} else {
							linkUrl = "#";
						}
						if (linkUrl != null && linkText != null) {
							navModel.setLinkText(linkText);
							navModel.setLinkUrl(linkUrl);
							navModel.setLinkTarget(linkTarget);
							primaryNavigationList.add(navModel);
						}
					} catch (ValueFormatException e) {
						e.printStackTrace();
					} catch (PathNotFoundException e) {
						e.printStackTrace();
					} catch (RepositoryException e) {
						e.printStackTrace();
					}

				}

			}
		}
		return primaryNavigationList;
	}
	
}
