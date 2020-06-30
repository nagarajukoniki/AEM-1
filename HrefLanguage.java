package com.tda.apac.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.tda.apac.core.beans.HrefLanguageBean;

@Model(adaptables = SlingHttpServletRequest.class)

public class HrefLanguage  {
	private static final Logger logger = LoggerFactory.getLogger(HrefLanguage.class);

    @ScriptVariable
    private Page currentPage;

    @Inject
	private ResourceResolverFactory resolverFactory;
    private int structureDepth=1;
    private Page rootPage;
    private List items;
    private int startLevel;
    ResourceResolver resourceResolver ;    
    protected Locale locale;
  public ArrayList<HrefLanguageBean> HrefLanguageBeanArray=new ArrayList<HrefLanguageBean>();

    @PostConstruct
    private void initModel() {
    	try {
        Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "tdaRead");
		resourceResolver = resolverFactory.getServiceResourceResolver(param);
        getItems();
    	} catch (Exception e) {
			logger.error("Error in Template head Sling Models Class:" + e);
		}finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
		}
    	
    }

 
	public List getItems() {
		if (items == null) {
			PageManager pageManager = currentPage.getPageManager();
			rootPage = pageManager.getPage(HrefConfiguration.navigationRoot);
			
			if (rootPage != null) {
				int rootPageLevel = rootPage.getDepth();
				//logger.debug("navigationRoot(rootPage) depth:" + navigationRoot);
				startLevel = rootPageLevel + 1;
				structureDepth += rootPageLevel;
				logger.debug("structureDepth:" + structureDepth);
				items = getItems(rootPage);
			} else {
				items = Collections.emptyList();
			}
		}

		return Collections.unmodifiableList(items);
	}


    private List getItems(Page root) {
        List pages = new ArrayList<>();
        String locale=null;
        String countryCode=null;
        String hrefLang=null;
        try {
        if (root.getDepth() < structureDepth) {
            Iterator<Page> it = root.listChildren(new PageFilter());
            int relativePath=currentPage.getPath().lastIndexOf("/");
            String partialurl=currentPage.getPath().substring(relativePath).replace("/", "");
            logger.info("requested page name:"+partialurl);
            while (it.hasNext()) {
            	HrefLanguageBean hrefLanguageBean=new HrefLanguageBean();
                Page page = it.next();              
                Page localizedPage = getLocalizedPage(currentPage, page);
                if (localizedPage != null) {
                    page = localizedPage;
                }
                locale=getLocale(page).toString();
                hrefLang=HrefConfiguration.hrefLanguageMap.get(getLocale(page).toString()); 
                countryCode=locale!=null?locale.replace("_", "-").toLowerCase():null;
                logger.debug("countryCode:"+countryCode);
                if( countryCode!=null && !countryCode.equals(partialurl) && page.getPath().endsWith(partialurl)&&hrefLang!=null && !hrefLang.equals(StringUtils.EMPTY) ) {
                	logger.debug("page Included:"+page.getPath());
                	hrefLanguageBean.setHref(resourceResolver.map(page.getPath()));
                	hrefLanguageBean.setHrefLang(hrefLang);
                	HrefLanguageBeanArray.add(hrefLanguageBean);
                }else {
                	logger.debug("Requested Page NOT Included since page is not existed in this locale:"+page.getPath());	
                }
                
            }
        }
		} catch (Exception e) {
			logger.error("Exception in getItems method" + e);
			return pages;
		}

        return pages;
    }

	private Page getLocalizedPage(Page page, Page languageRoot) {
		Page localizedPage = null;
		String path = languageRoot.getPath();
		String relativePath = page.getPath();
		if (relativePath.startsWith(path)) {
			localizedPage = page;
		} else {
			String separator = "/";
			int i = relativePath.indexOf(separator);
			int occurrence = StringUtils.countMatches(path, separator) + 1;
			while (--occurrence > 0 && i != -1) {
				i = relativePath.indexOf(separator, i + 1);
			}
			relativePath = (i > 0) ? relativePath.substring(i) : "";
			path = path.concat(relativePath);
			PageManager pageManager = page.getPageManager();
			localizedPage = pageManager.getPage(path);
		}
		return localizedPage;
	}

	public Locale getLocale(Page page) {
		Locale locale = null;
		locale = page.getLanguage(false);
		return locale;
	}


	public ArrayList<HrefLanguageBean> getHrefLanguageBeanArray() {
		return HrefLanguageBeanArray;
	}
    
}
