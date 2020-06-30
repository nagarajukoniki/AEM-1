package com.tda.apac.core.models;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.tda.apac.core.utils.WorkflowUtils;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class BreadCrumbModel {
	private static final Logger logger = LoggerFactory.getLogger(BreadCrumbModel.class);
	@Inject
	private Page currentPage;
	@Inject
	private ResourceResolverFactory resolverFactory;
	String templateName = "hong-kong-template";
	String externalizerLinkKey = "tdameritrade-hk";
	public String externalLink;

	@PostConstruct
	public void init() {
		ResourceResolver resourceResolver = null;
		try {
			logger.info("Inside BreadCrumbModel Class");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "tdaRead");
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
			logger.info("currentPage template name:" + currentPage.getTemplate().getName());
			templateName = currentPage.getTemplate().getName();
			if (!templateName.isEmpty()) {
				if (templateName.equals("hong-kong-template")) {
					externalizerLinkKey = "tdameritrade-hk";
				} else if (templateName.equals("singapore-template")) {
					externalizerLinkKey = "tdameritrade-sg";
				}
			}
			externalLink = WorkflowUtils.buildExternalLink(externalizerLinkKey, resourceResolver);
			if (externalLink.endsWith("/")) {
				externalLink = StringUtils.substring(externalLink, 0, externalLink.length() - 1);
			}
		} catch (Exception e) {
			logger.error("Error in Template head Sling Models Class:" + e);
		} finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
	}

	public String getExternalLink() {
		return externalLink;
	}
}
