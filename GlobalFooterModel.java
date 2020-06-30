package com.tda.apac.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional; 

@Model(adaptables = Resource.class)
public class GlobalFooterModel {
	
	@Inject
	@Optional
	public String logo;
	
	@Inject
	@Optional
	public String logoURL;
	
	@Inject
	@Optional
	public String logoAltText;

	@Inject
	@Optional
	public String bannerMsg;
	
	@Inject
	@Optional
	public String bannerLinkText;
	
	@Inject
	@Optional
	public String bannerLinkURL;
	
	@Inject
	@Optional
	public String mobileNumber;
	
	@Inject
	@Optional
	public String termText;
	
	@Inject
	@Optional
	public Resource footerLinks;
	
	@Inject
	@Optional
	public Resource footerMainContent1Links;
	
	@Inject
	@Optional
	public Resource footerMainContent2Links;
	
	@Inject
	@Optional
	public Resource footerMainContent3Links;
	
	@Inject
	@Optional
	public Resource footerMainContent4Links;
	
	@Inject
	@Optional
	public Resource footerMainContent5Links;
	
	@Inject
	@Optional
	public Resource socialLinks;
	@Inject
	@Optional
	public Resource loginLinks;
	
	@Inject
	@Optional
	public Resource legalInfoLinks;
	
	@Inject
	@Optional
	public Resource legalTermsLinks;
	
	public Resource getFooterLinks() {
	return footerLinks;
	}

	public String getLogoAltText() {
		return logoAltText;
	}
	
	public String getLogo() {
		return logo;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public String getBannerMsg() {
		return bannerMsg;
	}

	public String getBannerLinkText() {
		return bannerLinkText;
	}

	public String getBannerLinkURL() {
		return bannerLinkURL;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public Resource getSocialLinks() {
		return socialLinks;
	}
	public Resource getLoginLinks() {
		return loginLinks;
	}

	public Resource getLegalInfoLinks() {
		return legalInfoLinks;
	}

	public Resource getLegalTermsLinks() {
		return legalTermsLinks;
	}

	public Resource getFooterMainContent1Links() {
		return footerMainContent1Links;
	}

	public Resource getFooterMainContent2Links() {
		return footerMainContent2Links;
	}

	public Resource getFooterMainContent3Links() {
		return footerMainContent3Links;
	}

	public Resource getFooterMainContent4Links() {
		return footerMainContent4Links;
	}
	public Resource getFooterMainContent5Links() {
		return footerMainContent5Links;
	}
	public String getImageTag() {
		return (logo == null ||"".equals(logo.trim())) ? "" : "<img class=\"main-footer-logo-image svg\" alt=\""+getLogoAltText()+"\" src-defer=\""+getLogo()+"\"  src=\""+getLogo()+"\">";
	}
	public String getTermText() {
		return (termText == null ||"".equals(termText.trim())) ? "" : "<div class=\"main-footer-columns main-footer-copy\">"+termText+"</div>";
	}
		
}
