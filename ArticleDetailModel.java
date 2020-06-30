package com.tda.apac.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })

public class ArticleDetailModel {
	private static final Logger log = LoggerFactory.getLogger(ArticleDetailModel.class);
	@Inject
	SlingHttpServletRequest request;

	@Inject
	private ResourceResolverFactory resolverFactory;

	String mediaPath = null;
	String resourceType = null;
	public ArticleModel articleModel;
	boolean isValidMedia = false;

	@PostConstruct
	protected void init() {
		ResourceResolver resourceResolver = null;
		try {

			String resourcePath = request.getRequestPathInfo().getResourcePath();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "tdaRead");
			resourceResolver = resolverFactory.getServiceResourceResolver(param);

			checkResourcetypeValidity(resourceResolver, resourcePath);
		} catch (Exception e) {
			log.error("Exception in MediaDetailModel:" + e);
		} finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
	}

	public void checkResourcetypeValidity(ResourceResolver resourceResolver, String resourcePath)
			throws ValueFormatException, PathNotFoundException, RepositoryException {
		Node pagePath = null, mediaDetailNode = null;
		Resource resource, mediaDetailResource = null;
		resource = resourceResolver.getResource(resourcePath);
		pagePath = resource.adaptTo(Node.class);
		if (pagePath.hasProperty("pagepath")) {
			log.debug("media path:" + pagePath.getPath());
			mediaPath = pagePath.getProperty("pagepath").getString();
			if (mediaPath != null) {
				mediaDetailResource = resourceResolver.getResource(mediaPath + "/jcr:content/parsys");
				if (mediaDetailResource != null && !ResourceUtil.isNonExistingResource(mediaDetailResource)) {
					mediaDetailNode = mediaDetailResource.adaptTo(Node.class);
					resourceType = getResourceType(mediaDetailNode);
					if (resourceType != null && resourceType.equals("ecms-aem/components/content/article-component")) {
						if (mediaDetailResource != null && !ResourceUtil.isNonExistingResource(mediaDetailResource)) {
							log.debug("Before ArticleModel adapting");
							articleModel = mediaDetailResource.adaptTo(ArticleModel.class);
							isValidMedia = true;
							log.info("The resource type is valid " + isValidMedia);
						}
					}
				} else {
					log.info(mediaDetailResource + " Resource does not exists");
				}
			}
		}

	}

	public String getResourceType(Node mediaResourceNode)
			throws ValueFormatException, PathNotFoundException, RepositoryException {
		return mediaResourceNode.hasProperty("sling:resourceType")
				? mediaResourceNode.getProperty("sling:resourceType").getString()
				: null;
	}

	public ArticleModel getArticleModel() {
		return articleModel;
	}

	public boolean isValidMedia() {
		return isValidMedia;
	}
}
