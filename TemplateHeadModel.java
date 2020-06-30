package com.tda.apac.core.models;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.*;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TemplateHeadModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateHeadModel.class);

	@Inject
	SlingHttpServletRequest request;

	@Inject
	private Page currentPage;

	@Inject
	@Optional
	private Style currentStyle;

	@Inject
	@Optional
	public Resource metatags;

	@Inject
	private ResourceResolverFactory resolverFactory;

	/**
	 * Gets the current page associated with the resource.
	 */
	public Page getCurrentPage() {
		return currentPage;
	}

	public String canonicalTag = "";
	Locale currentLocale;

	/**
	 * The post construction will be executed on model instantiation. It is used to
	 * initialize the values.
	 */
	@PostConstruct
	public void init() {
		ResourceResolver resourceResolver = null;
		try {

			LOGGER.info("Template head Sling Model Class");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "tdaRead");
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
			canonicalTag = getCurrentPage().getProperties().get("canonicalLink", String.class);

			if (StringUtils.isNotBlank(canonicalTag)) {
				canonicalTag = resourceResolver.map(canonicalTag);
			} else {
				canonicalTag = StringUtils.EMPTY;
			}

			LOGGER.info("Template head canonicalTag:" + canonicalTag);
		} catch (Exception e) {
			LOGGER.error("Error in Template head Sling Models Class:" + e);
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
	public String getCanonicalTag() {
		return canonicalTag;
	}

	/**
	 * This method retrieves the site hierarchy.
	 *
	 * @return list of page names for site hierarchy
	 */
	public String getSiteHierarchy() {
		StringBuffer siteHierarchy = new StringBuffer();
		try {
			currentLocale = currentPage.getLanguage(true);
			ResourceBundle bundle = request.getResourceBundle(currentLocale);
			I18n i18n = new I18n(bundle);
			int startlevel = 5;
			int currentPageDepth = currentPage.getDepth();
			for (int i = startlevel; i < currentPageDepth; i++) {
				if (currentPage.getAbsoluteParent(i) != null && currentPage.getProperties().get("breadcrumb") != null) {
					Page absoluteParent = currentPage.getAbsoluteParent(i);
					if (absoluteParent.getProperties().get("breadcrumb") != null) {
						if (i == currentPageDepth - 1) {
							siteHierarchy.append((i == startlevel) ? i18n.get("Home") : absoluteParent.getTitle());
						} else {
							siteHierarchy.append(i == startlevel ? i18n.get("Home") : absoluteParent.getTitle());
							siteHierarchy.append(",");
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error:" + e.getMessage());
		}
		return siteHierarchy.toString();

	}

}
