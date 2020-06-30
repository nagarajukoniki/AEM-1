package com.tda.apac.core.models;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.*;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.tda.apac.core.beans.HeaderNavigationBean;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderNavigationModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeaderNavigationModel.class);
	public static final String HOME_PAGE_PATH = "homepagepath";
	public static final String LANGUAGE_CODE = "langcode";
	public static final String LANGUAGE_LABEL = "langlabel";
	public static final String LANGUAGE_SELECTION = "languageselection";
	private String homePagePath = null;
	@Inject
	SlingHttpServletRequest request;

	@Inject
	private Page currentPage;

	@Inject
	@Optional
	private Style currentStyle;

	private String logoUrl;
	private String contactUsLink;
	private String tryVirtualTradingLink;
	private String loginLink;
	private String headerCtaButton;
	private String contactUsUrlTarget = "_self";
	private String tryVirtualTradingUrlTarget = "_self";
	private String loginUrlTarget = "_self";
	private String ctaButtonTarget = "_self";
	private ArrayList<HeaderNavigationBean> languagesBean;
	ResourceResolver resourceResolver = null;
	Session session = null;

	/**
	 * Gets the current page associated with the resource.
	 */
	public Page getCurrentPage() {
		return currentPage;
	}

	private Page rootPage;

	@Inject
	private ResourceResolverFactory resolverFactory;

	@Inject
	private Resource resource;

	/**
	 * The post construction will be executed on model instantiation. It is used to
	 * initialize the values.
	 */
	@PostConstruct
	public void init() {
		try {
			LOGGER.info("APAC Header Navigation Sling Models Init Post Construct Method**");
			resourceResolver = request.getResourceResolver();
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			session = null;
			ContentPolicyManager policyManager = (ContentPolicyManager) resourceResolver
					.adaptTo(ContentPolicyManager.class);
			if (policyManager != null) {
				ContentPolicy contentPolicy = policyManager.getPolicy(resource);
				if (contentPolicy != null) {
					homePagePath = currentStyle.get(HOME_PAGE_PATH).toString();
					LOGGER.info("apac root page path homePagePath: " + homePagePath);
					rootPage = pageManager.getPage(homePagePath);
					contactUsLink = currentStyle.get("contactuslink").toString();
					tryVirtualTradingLink = currentStyle.get("tryvirtualtradinglink").toString();
					loginLink = currentStyle.get("loginlink").toString();
					headerCtaButton = currentStyle.get("headerctabutton").toString();
					contactUsUrlTarget = currentStyle.get("contactusurlnewwindow") == null
							|| "".equals(currentStyle.get("contactusurlnewwindow")) ? "_self" : "_blank";
					tryVirtualTradingUrlTarget = currentStyle.get("tryvirtualtradingurlnewwindow") == null
							|| "".equals(currentStyle.get("tryvirtualtradingurlnewwindow")) ? "_self" : "_blank";
					loginUrlTarget = currentStyle.get("loginurlnewwindow") == null
							|| "".equals(currentStyle.get("loginurlnewwindow")) ? "_self" : "_blank";
					ctaButtonTarget = currentStyle.get("headerctatarget") == null
							|| "".equals(currentStyle.get("headerctatarget")) ? "_self" : "_blank";
					logoUrl = currentStyle.get("logourl").toString();
					Map<String, Object> param = new HashMap<String, Object>();
					param.put(ResourceResolverFactory.SUBSERVICE, "tdaRead");
					resourceResolver = resolverFactory.getServiceResourceResolver(param);
					session = resourceResolver.adaptTo(Session.class);
					Node languages = session.getNode(currentStyle.getPath());
					LOGGER.info("languages............:" + languages);
					if (languages.hasNode(LANGUAGE_SELECTION)) {
						languages = languages.getNode(LANGUAGE_SELECTION);
						NodeIterator childNodes = languages.getNodes();
						LOGGER.info("Parent Node:" + languages.getPath());
						HeaderNavigationBean headerNavigationBean = null;
						languagesBean = new ArrayList<HeaderNavigationBean>();
						while (childNodes.hasNext()) {
							headerNavigationBean = new HeaderNavigationBean();
							Node childNode = (Node) childNodes.next();
							String langCode = childNode.hasProperty(LANGUAGE_CODE)
									? childNode.getProperty(LANGUAGE_CODE).getString()
									: "";
							headerNavigationBean.setLanguageCode(langCode);
							LOGGER.info("langCode:" + langCode);
							String langLabel = childNode.hasProperty(LANGUAGE_LABEL)
									? childNode.getProperty(LANGUAGE_LABEL).getString()
									: "";
							LOGGER.info("langLabel:" + langLabel);
							headerNavigationBean.setLanguageLabel(langLabel);
							languagesBean.add(headerNavigationBean);
						}
						headerNavigationBean = null;
					}
				}

			}
		}

		catch (Exception e) {
			LOGGER.error("Error in Navigation class:" + e);
		} finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
	}

	/**
	 * The getter method retrieves the list of navigation links as bean objects
	 * evaluated in the model initialization.
	 *
	 * @return list of navigation links
	 */
	public Page getRootPage() {
		return rootPage;
	}

	public String getContactUsLink() {
		return TDACommonUtil.getValidPath(contactUsLink);
	}

	public String getTryVirtualTradingLink() {
		return TDACommonUtil.getValidPath(tryVirtualTradingLink);
	}

	public String getLoginLink() {
		return TDACommonUtil.getValidPath(loginLink);
	}

	public String geHheaderCtaButton() {
		return TDACommonUtil.getValidPath(headerCtaButton);
	}

	/**
	 * The getter method retrieves the page path target window based on the checkbox
	 * associated with the resource. Defaulted to empty.
	 *
	 *
	 * @return Target window.
	 */
	public String getContactUsUrlTarget() {
		return contactUsUrlTarget;
	}

	/**
	 * The getter method retrieves the page path target window based on the checkbox
	 * associated with the resource. Defaulted to empty.
	 *
	 * @return Target window.
	 */
	public String getTrVirtualTradingUrlTarget() {
		return tryVirtualTradingUrlTarget;
	}

	/**
	 * The getter method retrieves the page path target window based on the checkbox
	 * associated with the resource. Defaulted to empty.
	 *
	 * @return Target window.
	 */
	public String getLoginUrlTarget() {
		return loginUrlTarget;
	}

	/**
	 * The getter method retrieves the page path target window based on the checkbox
	 * associated with the resource. Defaulted to empty.
	 *
	 * @return Target window.
	 */
	public String getCtaButtonTarget() {
		return ctaButtonTarget;
	}

	/**
	 * The getter method retrieves the list of navigation links as bean objects
	 * evaluated in the model languageselection.
	 *
	 * @return list of languageselections in dropdown
	 */
	public ArrayList<HeaderNavigationBean> getLanguageSelection() {
		return languagesBean;
	}

	public String getLogoUrl() {
		return TDACommonUtil.getValidPath(logoUrl);
	}

	public boolean isLanguageSelectionEmpty() {
		if (languagesBean != null && languagesBean.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
