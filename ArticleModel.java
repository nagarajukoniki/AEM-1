package com.tda.apac.core.models;


import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)

public class ArticleModel {
	
	@Inject
	@Optional
	public Resource disclosureids;
	
	@Inject @Default(values="")
	@Optional
	public String head;
	
	@Inject @Default(values="")
	@Optional
	public String teaser;
	
	
	@Inject @Default(values="")
	@Optional
	public String renderedHtml;
	
	@Inject @Default(values="")
	@Optional
	public String heroSmUrl;	
	
	@Inject @Default(values="")
	@Optional
	public String category;
	
	
	public String getCategory() {
		return category;
	}

	public String getHead() {
		return head;
	}

	public String getRenderedHtml() {
		return renderedHtml;
	}

	public String getHeroSmUrl() {
		return heroSmUrl;
	}

	public Resource getDisclosureids() {
		return disclosureids;
	}

	public String getTeaser() {
		return teaser;
	}
	
	}
