package com.tda.apac.core.models;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = { SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LeftNavModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(LeftNavModel.class);

	@ScriptVariable
	Page currentPage;

	@ScriptVariable
	private Style currentStyle;  
	private Page rootPage;
	private int structureDepth;
	/**
	 * The post construction will be executed on model instantiation. It is used to
	 * initialize the values.
	 */
	@PostConstruct
	protected void init() {
		try {
			structureDepth=currentStyle.get("navigationStructureDepth",Integer.class);
			rootPage = currentPage.getAbsoluteParent(structureDepth);
		   } catch (Exception e) {
			LOGGER.error("Error in Navigation class:" + e);
		}

	}

	public Page getRootPage() {
		return rootPage;
	}

	public void setRootPage(Page rootPage) {
		this.rootPage = rootPage;
	}
	}
