package com.platform.utils;

import java.util.List;

import com.platform.entities.FolderNode;

public class getTreeDataByPath {
	
	/**
	 * @param path
	 * @return
	 */
	public FolderNode findByPath(String path) {
		List<FolderNode> folderNodelist = CacheTreeData.getFolders();
		if (null == folderNodelist) {
			return null;
		}
		FolderNode folder = null;
		for (FolderNode folderNode : folderNodelist) {
			folder = getFolder(folderNode, path);
			if (null != folder) {
				break;
			}
		}
		return folder;
	}
	
	/**
	 * @param f
	 * @return
	 */
	private FolderNode getFolder(FolderNode f, String path){
		FolderNode result = null;
		if(path.equals(f.getPath())){
			return f;
		}
		List<FolderNode> folds = f.getChildNodes();
		if (null != folds) {
			for (FolderNode folderNode : folds) {
				result = getFolder(folderNode, path);
				if (null != result) {
					break;
				}
			}
		}
		return result;
	}
}

