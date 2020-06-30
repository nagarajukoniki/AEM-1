package com.tda.apac.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tda.apac.core.models.TDACommonUtil;
import com.tda.apac.core.models.HomeBean;

@Model(adaptables =  { SlingHttpServletRequest.class, Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class NavigationModel {
	
	private ArrayList<HomeBean> homeBeanArray=new ArrayList<HomeBean>();
	private static final Logger LOGGER = LoggerFactory.getLogger(NavigationModel.class);
		@Inject
		SlingHttpServletRequest request;
		
		@Inject
		private Page currentPage;
		
		/**
		 * Gets the current page associated with the resource.
		 */
		public Page getCurrentPage(){
			return currentPage;
		}
		
		public static final String HOME_PAGE_PATH = "homepagepath";
		
		private Page rootPage;
		
		private String homePagePath=null;
		
		@Inject
		@Optional
		private Style currentStyle;
		/**
		 * The post construction will be executed on model instantiation. It is used to initialize the values.
		 */
		@PostConstruct
		public void init(){						
			try {
				LOGGER.info("Navigation Sling Models Class");
                LOGGER.info("In Navigation Sling Models Class");
				ResourceResolver resourceResolver = request.getResourceResolver();
				PageManager pageManager = resourceResolver.adaptTo(PageManager.class);				
				
				homePagePath = currentStyle.get(HOME_PAGE_PATH).toString();
				rootPage = pageManager.getPage(homePagePath);
				int rootpageLevel = rootPage.getDepth();
				Page currentPage = getCurrentPage();
				Page masterPage = currentPage;
				
				Iterator<Page> pages = currentPage.listChildren();
				if (!pages.hasNext()) {
					currentPage = currentPage.getParent();
				}
				int pageLevel = currentPage.getDepth();
				Iterator<Page> childPages = null;
				if (pageLevel == (rootpageLevel+1)) {
					childPages = currentPage.listChildren();
				} else if (pageLevel > (rootpageLevel+1)) {
					childPages = currentPage.getParent().listChildren();
				}

				while (childPages.hasNext()) {
					HomeBean homeBean = new HomeBean();
					List<HomeBean> hBeans = new ArrayList<HomeBean>();
					Page page = childPages.next().adaptTo(Page.class);
					if (page != null && !page.isHideInNav()) {
						homeBean.setTitle(page.getTitle());
						if (masterPage.getPath().equals(page.getPath())) {
							homeBean.setActive("selected");
						}
						if (currentPage.getPath().equals(page.getPath())) {
							Iterator<Page> secondLevelPages = page.listChildren();
							if (secondLevelPages != null) {
								while (secondLevelPages.hasNext()) {
									Page secondLevelPage = secondLevelPages.next();
									HomeBean secondLevelHomeBean = new HomeBean();
									if(secondLevelPage != null && !secondLevelPage.isHideInNav()) {
										secondLevelHomeBean.setTitle(secondLevelPage.getTitle());
										secondLevelHomeBean.setPagePath(TDACommonUtil.getValidPath(secondLevelPage.getPath()));
										if (masterPage.getPath().equals(secondLevelPage.getPath())) {
											secondLevelHomeBean.setActive("selected");
										}
										hBeans.add(secondLevelHomeBean);
									}
								}
							}
						}
						homeBean.setPagePath(TDACommonUtil.getValidPath(page.getPath()));
						LOGGER.info("child path:" + page.getPath());
						LOGGER.info("child page is active:" + homeBean.getActive());
						LOGGER.info("page level is :" + pageLevel);
						homeBean.setHomeBeans(hBeans);
						homeBeanArray.add(homeBean);
						homeBean = null;

					}
				}

			} catch (Exception e) {
				LOGGER.error("Error in Navigation class:" + e);
			}
		
	}
    

	/**
    * The getter method retrieves the list of navigation links as bean objects evaluated in the model initialization.
    *
    * @return list of navigation links
    */
	public ArrayList<HomeBean> getHomeBeanArray() {
			return homeBeanArray;
	}	
	
}
