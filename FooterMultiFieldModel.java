package com.tda.apac.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables=Resource.class , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterMultiFieldModel {
	
	final static Logger logger = LoggerFactory.getLogger(FooterMultiFieldModel.class);
    
    @Inject
    @Optional
    @Named("column1Links")
    private Resource column1LinksList;
    
    @Inject
    @Optional
    @Named("column2Links")
    private Resource column2LinksList;
    
    @Inject
    @Optional
    @Named("column3Links")
    private Resource column3LinksList;
    
    @Inject
    @Optional
    @Named("column4Links")
    private Resource column4LinksList;
    
    @Inject
    @Optional
    @Named("languages")
    private Resource languagesList;
    
    @Inject
    @Optional
    @Named("secondaryLinks")
    private Resource secondaryLinks;    
    
    @ValueMapValue(name = "accoladesIconImageSource", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accoladesIconImageSource = "";
    
    @ValueMapValue(name = "accoladesIconAltText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accoladesIconAltText = "";
    
    @ValueMapValue(name = "accoladesHeadlineText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accoladesHeadlineText = "";
    
    @ValueMapValue(name = "accoladesHeadlineAlttext", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accoladesHeadlineAlttext = "";
    
    @ValueMapValue(name = "accoladesLogoImageSource", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accoladesLogoImageSource = "";
    
    @ValueMapValue(name = "accoladesLogoAltText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String accoladesLogoAltText = "";
    
    @ValueMapValue(name = "column1Title", injectionStrategy = InjectionStrategy.DEFAULT)
	private String column1Title = "";
    
    @ValueMapValue(name = "column2Title", injectionStrategy = InjectionStrategy.DEFAULT)
	private String column2Title = "";
    
    @ValueMapValue(name = "column3Title", injectionStrategy = InjectionStrategy.DEFAULT)
	private String column3Title = "";
    
    @ValueMapValue(name = "column4Title", injectionStrategy = InjectionStrategy.DEFAULT)
	private String column4Title = "";
    
    @ValueMapValue(name = "languageTitle", injectionStrategy = InjectionStrategy.DEFAULT)
	private String languageTitle = "";
    
    @ValueMapValue(name = "taglineText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String taglineText = "";
    
    @PostConstruct
	protected void init() {		
    	logger.info("FooterMultiFieldModel init method");
	}
    
    public Resource getColumn1LinksList() {
		return column1LinksList;
	}

	public Resource getColumn2LinksList() {
		return column2LinksList;
	}

	public Resource getColumn3LinksList() {
		return column3LinksList;
	}

	public Resource getColumn4LinksList() {
		return column4LinksList;
	}

	public Resource getLanguagesList() {
		return languagesList;
	}

	public Resource getSecondaryLinks() {
		return secondaryLinks;
	}

    public String getTaglineText() {
		return taglineText;
	}

	public String getLanguageTitle() {
		return languageTitle;
	}
    
    public String getAccoladesIconImageSource() {
		return accoladesIconImageSource;
	}
        
    public String getAccoladesIconAltText() {
		return accoladesIconAltText;
	}

	public String getAccoladesHeadlineText() {
		return accoladesHeadlineText;
	}

	public String getAccoladesHeadlineAlttext() {
		return accoladesHeadlineAlttext;
	}

	public String getAccoladesLogoImageSource() {
		return accoladesLogoImageSource;
	}

	public String getAccoladesLogoAltText() {
		return accoladesLogoAltText;
	}
	
	public String getColumn1Title() {
		return column1Title;
	}

	public String getColumn2Title() {
		return column2Title;
	}

	public String getColumn3Title() {
		return column3Title;
	}

	public String getColumn4Title() {
		return column4Title;
	}
		
}
