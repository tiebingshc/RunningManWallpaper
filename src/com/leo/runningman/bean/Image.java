package com.leo.runningman.bean;

public class Image {
	private String thumbNailURL;
	private String pathURL;
	private String createTime;
	
	public String getThumbNailURL() {
		return thumbNailURL;
	}
	public void setThumbNailURL(String thumbNailURL) {
		this.thumbNailURL = thumbNailURL;
	}
	public String getPathURL() {
		return pathURL;
	}
	public void setPathURL(String pathURL) {
		this.pathURL = pathURL;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "thumbNailURL:"+thumbNailURL+" pathURL:"+pathURL;
	}
	
	
}
