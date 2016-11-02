
/**
 * 文件名   :   VolumeEntity.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  chen
 * 版本       :   <版本>
 * 修改时间：      2016年9月9日
 * 修改内容：      <修改内容>
 */
package com.platform.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *  gfs的 volume 对象
 * 
 * @author    chen
 * @version   [版本号，2016年9月9日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class VolumeEntity {

	/** volume总大小  */ 
	private double allSize;
	
	/** volume已使用大小  */
	private double usedSize;
	
	/** volume名称  */
	private String name;
	
	/** 挂载点  */
	private String path;
	
	/**  * exist，正常返回状态Started,Stopped,Created */
	private String status;
	
	private String type;
	
	/** volume数据的树形目录  */
	private FolderNode folder = new FolderNode();
	
	/** volume的 块  */
	private List<Brick> brick = new ArrayList<Brick>();

	/**
	 * @return the allSize
	 */
	public double getAllSize() {
		return allSize;
	}

	/**
	 * @param allSize the allSize to set
	 */
	public void setAllSize(double allSize) {
		this.allSize = allSize;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the folder
	 */
	public FolderNode getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(FolderNode folder) {
		this.folder = folder;
	}

	/**
	 * @return the brick
	 */
	public List<Brick> getBrick() {
		return brick;
	}

	/**
	 * @param brick the brick to set
	 */
	public void setBrick(List<Brick> brick) {
		this.brick = brick;
	}

	
}
