core/beans:
package com.tda.apac.core.beans;

import java.util.List;

import com.tda.apac.core.models.TDACommonUtil;

public class AccordionBean {
	private String itemheader;
	private String itemcontent;
	private String contentid;
	private String ctabuttontext;
	private String ctabuttonstyle;
	private String ctalink;
	private String text;
	private String href;
	private boolean hasLinkLists;
	private List<AccordionBean> accordionBean;

	public boolean isHasLinkLists() {
		return hasLinkLists;
	}

	public void setHasLinkLists(boolean hasLinkLists) {
		this.hasLinkLists = hasLinkLists;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getContentid() {
		return contentid;
	}

	public void setContentid(String contentid) {
		this.contentid = contentid;
	}

	public String getCtabuttontext() {
		return ctabuttontext;
	}

	public void setCtabuttontext(String ctabuttontext) {
		this.ctabuttontext = ctabuttontext;
	}

	public String getCtabuttonstyle() {
		return ctabuttonstyle;
	}

	public void setCtabuttonstyle(String ctabuttonstyle) {
		this.ctabuttonstyle = ctabuttonstyle;
	}

	public String getCtalink() {
		return TDACommonUtil.getValidPath(ctalink);
	}

	public void setCtalink(String ctalink) {
		this.ctalink = ctalink;
	}

	public String getItemHeader() {
		return itemheader;
	}

	public void setItemHeader(String itemheader) {
		this.itemheader = itemheader;
	}

	public String getItemContent() {
		return itemcontent;
	}

	public void setItemContent(String itemcontent) {
		this.itemcontent = itemcontent;
	}

	public List<AccordionBean> getAccordionBean() {
		return accordionBean;
	}

	public void setAccordionBean(List<AccordionBean> accordionBean) {
		this.accordionBean = accordionBean;
	}
}
HeadernavBean.java:
package com.tda.apac.core.beans;

import com.day.cq.wcm.api.Page;

public class HeaderNavigationBean {
	private String languageCode;
	private String languageLabel;
	private String linkText;	    
    private String linkUrl;
    private String linkTarget;
	private Page page;	
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public String getLanguageLabel() {
		return languageLabel;
	}
	public void setLanguageLabel(String languageLabel) {
		this.languageLabel = languageLabel;
	}
	public String getLinkText() {
		return linkText;
	}
	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getLinkTarget() {
		return linkTarget;
	}
	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}

}
HrefLanguageBean.java:
package com.tda.apac.core.beans;

public class HrefLanguageBean {
private String href;
private String hrefLang;

public String getHref() {
	return href;
}
public void setHref(String href) {
	this.href = href;
}
public String getHrefLang() {
	return hrefLang;
}
public void setHrefLang(String hrefLang) {
	this.hrefLang = hrefLang;
}
	
}
TextStyleBean.java
package com.tda.apac.core.beans;

public class TextStyleBean {
	private String bold;
	private String italic;
	private String underline;
	
	public String getBoldStyle() {
		return bold;
	}
	public void setBoldStyle(String bold) {
		this.bold = bold;
	}
	public String getItalicStyle() {
		return italic;
	}
	public void setItalicStyle(String italic) {
		this.italic = italic;
	}
	public String getUnderlineStyle() {
		return underline;
	}
	public void setUnderlineStyle(String underline) {
		this.underline = underline;
	}		
}
