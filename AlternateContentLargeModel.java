package com.tda.apac.core.models;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tda.apac.core.beans.AccordionBean;
import com.tda.apac.core.models.TDACommonUtil;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AlternateContentLargeModel {

	final static Logger logger = LoggerFactory.getLogger(AlternateContentLargeModel.class);

	private String requestResourcePath;
	private String TEXT = "text";
	private String HREF = "href";
	private ArrayList<AccordionBean> listArray;
	public String link="";

	@ValueMapValue(name = "alignment", injectionStrategy = InjectionStrategy.DEFAULT)
	private String alignment = "";

	@ValueMapValue(name = "alternateContentType", injectionStrategy = InjectionStrategy.DEFAULT)
	private String alternateContentType = "";

	@ValueMapValue(name = "acColorThemes", injectionStrategy = InjectionStrategy.DEFAULT)
	private String acColorThemes = "";

	@ValueMapValue(name = "imageAcLargeHref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageAcLargeHref = "";

	@ValueMapValue(name = "imageAcLargeImageAlignment", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageAcLargeImageAlignment = "";

	@ValueMapValue(name = "imageAcLargeImageSource", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageAcLargeImageSource = "";

	@ValueMapValue(name = "imageAcLargeImageType", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageAcLargeImageType = "";

	@ValueMapValue(name = "imageAcLargeAltText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageAcLargeAltText = "";

	@ValueMapValue(name = "imageAcLargeMobileImage", injectionStrategy = InjectionStrategy.DEFAULT)
	private String imageAcLargeMobileImage = "";

	@ValueMapValue(name = "headLineFontSize", injectionStrategy = InjectionStrategy.DEFAULT)
	private String headLineFontSize = "";

	@ValueMapValue(name = "headLineColor", injectionStrategy = InjectionStrategy.DEFAULT)
	private String headLineColor = "";

	@ValueMapValue(name = "headLineText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String headLineText = "";

	@ValueMapValue(name = "paragraphColor", injectionStrategy = InjectionStrategy.DEFAULT)
	private String paragraphColor = "";

	@ValueMapValue(name = "paragraphFontSize", injectionStrategy = InjectionStrategy.DEFAULT)
	private String paragraphFontSize = "";

	@ValueMapValue(name = "paragraphText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String paragraphText = "";

	@ValueMapValue(name = "ctaButtonHref", injectionStrategy = InjectionStrategy.DEFAULT)
	private String ctaButtonHref = "";

	@ValueMapValue(name = "ctaButtonPlaneText", injectionStrategy = InjectionStrategy.DEFAULT)
	private String ctaButtonPlaneText = "";

	@ValueMapValue(name = "withDivider", injectionStrategy = InjectionStrategy.DEFAULT)
	private String withDivider = "";

	@ValueMapValue(name = "dividerAlignment", injectionStrategy = InjectionStrategy.DEFAULT)
	private String dividerAlignment = "";

	@ValueMapValue(name = "ctaOpenWindow", injectionStrategy = InjectionStrategy.DEFAULT)
	private String ctaOpenWindow = "";

	@ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
	private Resource linklist;

	public String getCtaOpenWindow() {
		return ctaOpenWindow;
	}

	private String imageURL;

	private StringBuffer AcImageTag;

	private StringBuffer buttonTitleTag;

	private String ctaButtonType;

	public String getImageURL() {
		return imageURL;
	}

	public StringBuffer getAcImageTag() {
		return AcImageTag;
	}

	public static Logger getLogger() {
		return logger;
	}

	public String getAlignment() {
		return alignment;
	}

	public String getAlternateContentType() {
		return alternateContentType;
	}

	public String getAcColorThemes() {
		return acColorThemes;
	}

	public String getImageAcLargeHref() {
		return imageAcLargeHref;
	}

	public String getImageAcLargeImageAlignment() {
		return imageAcLargeImageAlignment;
	}

	public String getImageAcLargeImageSource() {
		return imageAcLargeImageSource;
	}

	public String getImageAcLargeImageType() {
		return imageAcLargeImageType;
	}

	public String getImageAcLargeAltText() {
		return imageAcLargeAltText;
	}

	public String getImageAcLargeMobileImage() {
		return imageAcLargeMobileImage;
	}

	public String getHeadLineFontSize() {
		return headLineFontSize;
	}

	public String getHeadLineColor() {
		return headLineColor;
	}

	public String getHeadLineText() {
		return headLineText;
	}

	public String getParagraphColor() {
		return paragraphColor;
	}

	public String getParagraphFontSize() {
		return paragraphFontSize;
	}

	public String getParagraphText() {
		return paragraphText;
	}

	public String getCtaButtonHref() {
		return ctaButtonHref;
	}

	public String getCtaButtonType() {
		return ctaButtonType;
	}

	public String getCtaButtonPlaneText() {
		return ctaButtonPlaneText;
	}

	public String getWithDivider() {
		return withDivider;
	}

	public String getDividerAlignment() {
		return dividerAlignment;
	}

	@PostConstruct
	protected void init() {
		logger.info("alternate Model init method::");
		requestResourcePath = request.getResource().getPath();
		logger.info("requestResourcePath is::::::" + requestResourcePath);

		try {

			Node currentNode = null;
			if (linklist != null) {
				currentNode = linklist.adaptTo(Node.class);
				logger.info("currentNode is :: " + currentNode);
			}
			if (currentNode != null) {
				if (currentNode.hasNodes()) {
					NodeIterator childNodes = currentNode.getNodes();
					listArray = new ArrayList<AccordionBean>();
					while (childNodes.hasNext()) {
						AccordionBean linkListBean = new AccordionBean();
						Node listitem = (Node) childNodes.next();
						logger.info("At Node   :: " + listitem.getName());
						linkListBean.setText(listitem.hasProperty(TEXT) ? listitem.getProperty(TEXT).getString() : "");
						if (listitem.hasProperty(HREF)) {
							link = listitem.getProperty(HREF).getString();
							link = TDACommonUtil.getValidPath(link);
						}
						linkListBean.setHref(link);

						listArray.add(linkListBean);
						logger.info(" listArray size is " + listArray.size());
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in alternate content class:" + e);
		}

	}

	@Inject
	private SlingHttpServletRequest request;

	public ArrayList<AccordionBean> getlistBeanArray() {
		return listArray;
	}

	public String getResourceName() {
		String requestResourceName = "";
		if (requestResourcePath != null) {
			requestResourceName = requestResourcePath.substring(requestResourcePath.lastIndexOf("/") + 1);
		}
		return requestResourceName;
	}

	public String getButtonTag() {
		TDACommonUtil util = new TDACommonUtil();
		buttonTitleTag = new StringBuffer();
		if (!ctaButtonHref.isEmpty() && !ctaButtonPlaneText.isEmpty()) {
			buttonTitleTag.append("<a title=" + "\"" + ctaButtonPlaneText + "\"");
			ctaButtonHref = TDACommonUtil.getValidPath(ctaButtonHref);
			buttonTitleTag.append(" " + "href=" + "\"" + ctaButtonHref + "\"");
			buttonTitleTag.append(" " + "target=" + "\"" + util.getRedirectWindow(ctaOpenWindow) + "\"");

			if (acColorThemes.equals("white")) {
				ctaButtonType = "btn-green-hollow";
			} else {
				ctaButtonType = "btn-white-hollow";

			}
			buttonTitleTag.append("class=\"alternating-content__cta btn " + ctaButtonType + "\" data-dl-link.name=\""
					+ ctaButtonPlaneText + "\" data-dl-product=\"" + ctaButtonPlaneText + "\">");

			buttonTitleTag.append(ctaButtonPlaneText + "</a>");

		}
		return buttonTitleTag.toString();

	}

	public String getAcColorImage() {
		AcImageTag = new StringBuffer();
		if (imageAcLargeImageType.equalsIgnoreCase("decorative")) {
			imageAcLargeAltText = "";
		}
		if (acColorThemes.equals("white")) {
			AcImageTag.append("<div class=\"alternating-content__columns alternating-content__image\"> ");
			AcImageTag.append("<picture>");
			AcImageTag.append("<source media=\"(min-width:767px)\" srcset=\"" + imageAcLargeImageSource + "\">");
			AcImageTag.append("<source media=\"(min-width:300px)\" srcset=\"" + imageAcLargeMobileImage + "\">");
			AcImageTag.append("<img class=\"alternating-content__img\"");
			AcImageTag.append(" " + "src=" + "\"" + imageAcLargeImageSource + "\"");

			AcImageTag.append(" " + "alt=" + "\"" + imageAcLargeAltText + "\">");
			AcImageTag.append("</picture>");
			AcImageTag.append("</div>");
			return AcImageTag.toString();

		} else {
			AcImageTag.append("<div class=\"alternating-content__columns alternating-content__image\"");
			if (!imageAcLargeImageType.equals("decorative")) {
				AcImageTag.append("" + " role=\"img\"");
			}
			AcImageTag.append(" " + "aria-label=" + "\"" + imageAcLargeAltText + "\"");
			imageURL = " " + "style=\"background-image:url(" + imageAcLargeImageSource + ");\"> </div>";
			AcImageTag.append(imageURL);

			if (!imageAcLargeMobileImage.isEmpty()) {
				AcImageTag.append(
						"<div class=\"alternating-content__columns alternating-content__image alternating-content__image--mobile\"");
				if (!imageAcLargeImageType.equals("decorative")) {
					AcImageTag.append("" + " role=\"img\"");
				}
				AcImageTag.append(" " + "aria-label=" + "\"" + imageAcLargeAltText + "\"");
				imageURL = " " + "style=\"background-image:url(" + imageAcLargeMobileImage + ");\"> </div>";
				AcImageTag.append(imageURL);
			}

			return AcImageTag.toString();
		}

	}

}
