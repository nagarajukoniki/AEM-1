package com.tda.apac.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)

public class VideoModel {
	@Inject
	@Default(values = "")
	@Optional
	public String videoId;
	
	@Inject
	@Default(values = "default")
	@Optional
	public String playerId;
	
	@Inject
	@Default(values = "")
	@Optional
	public String title;

	@Inject
	@Default(values = "")
	@Optional
	public String description;

	@Inject
	@Optional
	public Resource disclosureids;

	@Inject
	@Optional
	public Resource relatedvideos;

	public Resource getDisclosureids() {
		return disclosureids;
	}

	public Resource getRelatedVideos() {
		return relatedvideos;
	}

	public String getVideoId() {
		return videoId;
	}
	
	public String getPlayerId() {
		return playerId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
}
