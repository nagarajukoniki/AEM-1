package com.tda.apac.core.models;
import java.util.List; 

public class HomeBean {
	private String title;
	private String pagePath;
	private String active;
	private List<HomeBean> homeBeans;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPagePath() {
		return pagePath;
	}
	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public List<HomeBean> getHomeBeans() {
		return homeBeans;
	}
	public void setHomeBeans(List<HomeBean> homeBeans) {
		this.homeBeans = homeBeans;
	}
}
