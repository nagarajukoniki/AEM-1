package com.tda.apac.core.models;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tda.apac.core.beans.AccordionBean;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class AccordionModel {
	private ArrayList<AccordionBean> accordionArray = new ArrayList<AccordionBean>();
	private ArrayList<AccordionBean> accordionListArray;
	public static final String ITEM_HEADER_TEXT = "itemheader";
	public static final String ITEM_CONTENT_TEXT = "itemcontent";
	public static final String CONTENT_ID = "contentid";
	public static final String CTA_BUTTON_TEXT = "ctabuttontext";
	public static final String CTA_BUTTON_STYLE = "ctabuttonstyle";
	public static final String CTA_LINK = "ctalink";
	public static final String TEXT = "text";
	public static final String HREF = "href";
	public String link = "";

	private static final Logger LOGGER = LoggerFactory.getLogger(AccordionModel.class);

	@ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
	private Resource faqlist;

	@ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
	private Resource linklist;

	@PostConstruct
	public void init() {
		try {

			Node currentNode = null;
			if (faqlist != null) {
				currentNode = faqlist.adaptTo(Node.class);
				LOGGER.info("currentNode is :: " + currentNode);
			}
			if (currentNode != null) {
				if (currentNode.hasNodes()) {
					NodeIterator childNodes = currentNode.getNodes();
					int contentId = 0;
					while (childNodes.hasNext()) {
						contentId = contentId + 1;
						AccordionBean accordionBean = new AccordionBean();
						Node faq = (Node) childNodes.next();
						LOGGER.debug("At node   :: " + faq.getName());
						accordionBean.setItemHeader(
								faq.hasProperty(ITEM_HEADER_TEXT) ? faq.getProperty(ITEM_HEADER_TEXT).getString() : "");
						accordionBean.setItemContent(
								faq.hasProperty(ITEM_CONTENT_TEXT) ? faq.getProperty(ITEM_CONTENT_TEXT).getString()
										: "");
						accordionBean.setContentid(faq.hasProperty(ITEM_HEADER_TEXT)
								? "content-" + contentId
								: "");
						accordionBean.setCtabuttontext(
								faq.hasProperty(CTA_BUTTON_TEXT) ? faq.getProperty(CTA_BUTTON_TEXT).getString() : "");
						accordionBean.setCtabuttonstyle(
								faq.hasProperty(CTA_BUTTON_STYLE) ? faq.getProperty(CTA_BUTTON_STYLE).getString() : "");
						accordionBean
								.setCtalink(faq.hasProperty(CTA_LINK) ? faq.getProperty(CTA_LINK).getString() : "");
						accordionBean.setHasLinkLists(faq.hasNodes() ? true : false);

						if (faq.hasNodes()) {

							getAccordionList(faq);
							LOGGER.info(
									"accordionListArray size is ::::::::::::::::::::::::" + accordionListArray.size());
							accordionBean.setAccordionBean(accordionListArray);
						}
						accordionArray.add(accordionBean);

					}

				}
			}

		} catch (Exception e) {
			LOGGER.error("Error in Accordion class:" + e);
		}

	}

	public void getAccordionList(Node faq) {
		try {
			NodeIterator listchildNodes = faq.getNodes();
			if (listchildNodes.hasNext()) {

				Node list = (Node) listchildNodes.next();
				LOGGER.debug("At Node   :: " + list.getName());
				if (list.hasNodes()) {
					NodeIterator listNodes = list.getNodes();
					accordionListArray = new ArrayList<AccordionBean>();
					while (listNodes.hasNext()) {

						AccordionBean accordionLinkListBean = new AccordionBean();
						Node listitem = (Node) listNodes.next();
						LOGGER.debug("At Node   :: " + listitem.getName());
						accordionLinkListBean
								.setText(listitem.hasProperty(TEXT) ? listitem.getProperty(TEXT).getString() : "");
						if (listitem.hasProperty(HREF)) {
							link = listitem.getProperty(HREF).getString();
							link = TDACommonUtil.getValidPath(link);
						}
						accordionLinkListBean.setHref(link);
						accordionListArray.add(accordionLinkListBean);

					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in Accordion class:" + e);
		}
	}

	/**
	 * The getter method retrieves the list of faq as bean objects evaluated in the
	 * model initialization.
	 *
	 * @return list of faq
	 */
	public ArrayList<AccordionBean> getaccordionBeanArray() {
		return accordionArray;
	}

	/**
	 * The getter method retrieves the list of faq as bean objects evaluated in the
	 * model initialization.
	 *
	 * @return list of faq
	 */
	public ArrayList<AccordionBean> getaccordionlistBeanArray() {
		return accordionListArray;
	}
}
