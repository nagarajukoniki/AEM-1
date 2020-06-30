package com.tda.apac.core.models;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tda.apac.core.models.TDACommonUtil;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CrossSellModel {
	private static final Logger logger = LoggerFactory.getLogger(CrossSellModel.class);
	
	@ValueMapValue(name = "crossSellColorTheme", injectionStrategy = InjectionStrategy.DEFAULT)
	private String crossSellColorTheme = "";
	@ValueMapValue(name = "crossSellType", injectionStrategy = InjectionStrategy.DEFAULT)
	private String crossSellType = "";

	@ValueMapValue(name = "headLineColor", injectionStrategy = InjectionStrategy.DEFAULT)
	private String headLineColor = "";

	@ValueMapValue(name = "headLinePlainText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String headLinePlainText = "";

	@ValueMapValue(name = "subHeadLineColor", injectionStrategy = InjectionStrategy.DEFAULT)
	private String subHeadLineColor = "";

	@ValueMapValue(name = "subHeadLinePlainText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String subHeadLinePlainText = "";

	@ValueMapValue(name = "paragraphText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String paragraphText = "";

	@ValueMapValue(name = "ctaButtonPlainText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String ctaButtonPlainText = "";

	@ValueMapValue(name = "ctaButtonHref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String ctaButtonHref = "";
	
	@ValueMapValue(name = "ctaButtonAltText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String ctaButtonAltText = "";
	
	@ValueMapValue(name = "ctaUrlNewWindow", injectionStrategy = InjectionStrategy.DEFAULT)
	private String 	ctaUrlNewWindow = "";
	
	@ValueMapValue(name = "disclosureLinkPlainText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String disclosureLinkPlainText = "";

	@ValueMapValue(name = "disclosureLinkHref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String disclosureLinkHref = "";
	@ValueMapValue(name = "disclosureUrlNewWindow", injectionStrategy = InjectionStrategy.DEFAULT)
	private String 	disclosureUrlNewWindow = "";
	@ValueMapValue(name = "imageSource", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageSource = "";
	
	TDACommonUtil util=new TDACommonUtil();
	
	
	@PostConstruct
	protected void init() {
		logger.info("Cross-sell Model init method::");
		
          }
	public String getCrossSellColorTheme() {
		return crossSellColorTheme;
	}

	public String getCrossSellType() {
		return crossSellType;
	}

	public String getHeadLineColor() {
		return headLineColor;
	}

	public String getHeadLinePlainText() {
		return headLinePlainText;
	}

	public String getSubHeadLineColor() {
		return subHeadLineColor;
	}

	public String getSubHeadLinePlainText() {
		return subHeadLinePlainText;
	}
	public String getParagraphText() {
		return paragraphText;
	}
	public String getCtaButtonPlainText() {
		return ctaButtonPlainText;
	}

	public String getCtaButtonHref() {
		return TDACommonUtil.getValidPath(ctaButtonHref);
	}

	public String getCtaButtonAltText() {
		return ctaButtonAltText;
	}

	public String getDisclosureLinkPlainText() {
		return disclosureLinkPlainText;
	}

	public String getDisclosureLinkHref() {
		return TDACommonUtil.getValidPath(disclosureLinkHref);
	}
	public String getCtaUrlNewWindow() {
		return util.getRedirectWindow(ctaUrlNewWindow);
	}

	public String getDisclosureUrlNewWindow() {
		return  util.getRedirectWindow(disclosureUrlNewWindow);
	}
	public String getImageSource() {
		return imageSource;
	}
					
	}
