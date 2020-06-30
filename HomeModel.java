package com.tda.apac.core.models;

import com.day.cq.wcm.api.Page;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomeModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeModel.class);

	@Inject
	private Page currentPage;

	private String leftrail = "false";

	private String rightrail = "false";
	private String home = "false";

	/**
	 * Gets the current page associated with the resource.
	 */
	public Page getCurrentPage() {
		return currentPage;
	}

	@PostConstruct
	public void init() {

		LOGGER.info("leftrail " + currentPage.getProperties().get("leftrail"));
		LOGGER.info("rightrail " + currentPage.getProperties().get("rightrail"));

		if (currentPage.getProperties().get("leftrail") != null) {
			leftrail = currentPage.getProperties().get("leftrail").toString();
		}
		if (currentPage.getProperties().get("rightrail") != null) {
			rightrail = currentPage.getProperties().get("rightrail").toString();
		}
		if (leftrail.equalsIgnoreCase("false") && rightrail.equalsIgnoreCase("false")) {
			home = "true";
		}

	}

	public String getLeftrail() {
		return leftrail;
	}

	public String getRightrail() {

		return rightrail;
	}

	public String getHome() {
		return home;
	}
  
	public String getRailMain() {
		String pageClass ="page-main-content";
		if(rightrail.equalsIgnoreCase("true")) {
			pageClass= "hs-page-main-content-right-rail";
		}
		return pageClass;
	}
	
	public String getMainBody() {
		String pageClass ="page-main-content-body__wrapper";
		if (rightrail.equalsIgnoreCase("true")) {
			pageClass="hs-page-main-content-body__wrapper";
		}
		return pageClass;
	}
}
