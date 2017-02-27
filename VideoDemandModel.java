package com.songming.player;

import java.io.Serializable;

/**
 * 视频实体类
 * 
 * @author lw
 * 
 */
public class VideoDemandModel implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 9196548775996993519L;

	/**
	 * 视频的标题
	 */
	private String title;

	/**
	 * 视频的站点链接
	 */
	private String path;

	/**
	 * 视频大小
	 */
	private long size;

	/**
	 * 视频时长
	 */
	private long time;

	public VideoDemandModel(String title, String path, long size,
			long time) {
		super();
		this.title = title;
		this.path = path;
		this.size = size;
		this.time = time;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "VideoDemandModel [title=" + title + ", path=" + path
				+ ", size=" + size + ", time=" + time + "]";
	}

}
