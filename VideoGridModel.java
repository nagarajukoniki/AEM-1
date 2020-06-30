package com.tda.apac.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
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

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class VideoGridModel {
	private static final Logger log = LoggerFactory.getLogger(VideoGridModel.class);
	@Inject
	SlingHttpServletRequest request;

	@Inject
	private ResourceResolverFactory resolverFactory;

	public String VIDEOLIST = "videolist";
	String mediaPath = null;
	String resourceType = null;
	public VideoModel videoModel;
	public ArrayList<VideoModel> videoModelArray = new ArrayList<VideoModel>();

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
			log.error("Exception in VideoGridModel:" + e);
		} finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
	}

	public void checkResourcetypeValidity(ResourceResolver resourceResolver, String resourcePath)
			throws ValueFormatException, PathNotFoundException, RepositoryException {

		Node mediaDetailNode = null, videogrid = null;
		Resource resource, mediaDetailResource = null;
		resource = resourceResolver.getResource(resourcePath);
		videogrid = resource.adaptTo(Node.class);
		if (videogrid.hasNode(VIDEOLIST)) {
			videogrid = videogrid.getNode(VIDEOLIST);
			if (videogrid.hasNodes()) {
				NodeIterator childNodes = videogrid.getNodes();

				while (childNodes.hasNext()) {
					Node pagePathNode = (Node) childNodes.next();
					if (pagePathNode.hasProperty("pagepath")) {
						log.info("Video path ::: " + pagePathNode.getPath());
						mediaPath = pagePathNode.getProperty("pagepath").getString();
						if (mediaPath != null) {
							mediaDetailResource = resourceResolver.getResource(mediaPath + "/jcr:content/parsys");
							if (mediaDetailResource != null
									&& !ResourceUtil.isNonExistingResource(mediaDetailResource)) {
								mediaDetailNode = mediaDetailResource.adaptTo(Node.class);
								resourceType = getResourceType(mediaDetailNode);
								if (resourceType != null
										&& resourceType.equals("ecms-aem/components/content/video-component")) {
									if (mediaDetailResource != null
											&& !ResourceUtil.isNonExistingResource(mediaDetailResource)) {
										log.debug("Before VideoGridModel adapting");
										videoModel = mediaDetailResource.adaptTo(VideoModel.class);
										videoModelArray.add(videoModel);

									}
								}
							} else {
								log.info(mediaDetailResource + " Resource does not exists");
							}
						}
					}
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

	public VideoModel getVideoModel() {
		return videoModel;
	}

}
