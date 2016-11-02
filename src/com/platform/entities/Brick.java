
/**
 * 文件名   :   Brick.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  chen
 * 版本       :   <版本>
 * 修改时间：      2016年9月9日
 * 修改内容：      <修改内容>
 */
package com.platform.entities;

/**
 * <一句话功能简述> volume 下的 块 对象
 * <功能详细描述>
 * @author    chen
 * @version   [版本号，2016年9月9日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class Brick {
	
	/** brick总大小  */
	private double availableSize;
	
	/** brick已使用大小  */
	private double usedSize;

	/** ip  */
	private String ip;
	
	/** 路径  */
	private String path;
	
	/**
	 * true 有连接，  false： 失去连接
	 */
	private boolean status;

	/**
	 * @return the availableSize
	 */
	public double getAvailableSize() {
		return availableSize;
	}

	/**
	 * @param availableSize the availableSize to set
	 */
	public void setAvailableSize(double availableSize) {
		this.availableSize = availableSize;
	}

	/**
	 * @return the usedSize
	 */
	public double getUsedSize() {
		return usedSize;
	}

	/**
	 * @param usedSize the usedSize to set
	 */
	public void setUsedSize(double usedSize) {
		this.usedSize = usedSize;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
}
