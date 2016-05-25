package com.weibo.utils.bean;

public class Weibo {
	private String time;
	private String user;
	private String content;
	private String contentOrigin;
	private String topic;
	private String relativeUser;
	private double credit;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContentOrigin() {
		return contentOrigin;
	}
	public void setContentOrigin(String contentOrigin) {
		this.contentOrigin = contentOrigin;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getRelativeUser() {
		return relativeUser;
	}
	public void setRelativeUser(String relativeUser) {
		this.relativeUser = relativeUser;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	
}
