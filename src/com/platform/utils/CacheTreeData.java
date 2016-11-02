package com.platform.utils;

import java.util.List;
import java.util.Map;

import com.platform.entities.FolderNode;
import com.platform.entities.VolumeEntity;

public class CacheTreeData {
	
	private static List<FolderNode> folders = null;
	
	private static List<VolumeEntity> volumeList = null;

	public static List<FolderNode> getFolders() {
		return folders;
	}

	public synchronized static void setFolders(List<FolderNode> folders) {
		CacheTreeData.folders = folders;
	}

	/**
	 * @return the volumeList
	 */
	public static List<VolumeEntity> getVolumeList() {
		return volumeList;
	}

	/**
	 * @param volumeList the volumeList to set
	 */
	public synchronized static void setVolumeList(List<VolumeEntity> volumeList) {
		CacheTreeData.volumeList = volumeList;
	}
	
}


