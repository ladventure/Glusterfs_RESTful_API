package com.platform.entities;

import java.util.ArrayList;
import java.util.List;

public class FolderNode {
	private String name;
	private int isFolder; // 1 is file and other integer is folder show children number
	private String path;
	private List<FolderNode> childNodes = new ArrayList<FolderNode>();
	

	public FolderNode() {
		// TODO Auto-generated constructor stub
	}

	public FolderNode(String name) {
		this.name = name;
	}

	public FolderNode(String name, int isFolder) {
		this.name = name;
		this.isFolder = isFolder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isFolder
	 */
	public int getIsFolder() {
		return isFolder;
	}

	/**
	 * @param isFolder the isFolder to set
	 */
	public void setIsFolder(int isFolder) {
		this.isFolder = isFolder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<FolderNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<FolderNode> childNodes) {
		this.childNodes = childNodes;
	}
	
	public FolderNode CreateFolderTree(List<String> folderLists){
		FolderNode treeRoot=new FolderNode();
		if (folderLists==null || folderLists.size()==0)
		{
			return null;
		}
		treeRoot.setPath(folderLists.get(0));
		treeRoot.setName(folderLists.get(0).replaceAll(".*/", ""));   
		if(folderLists.size()==1)
		{
			return treeRoot;
		}
		int lastFlag=1;
		for(int i=1;i<folderLists.size();i++){
			String temp=folderLists.get(i);
			temp=temp.replace(treeRoot.getPath()+"/","");
			if(!temp.contains("/") && i!=1){
				treeRoot.getChildNodes().add(CreateFolderTree(folderLists.subList(lastFlag, i)));
				lastFlag=i;
			}
			
		}
		treeRoot.getChildNodes().add(CreateFolderTree(folderLists.subList(lastFlag, folderLists.size())));
		return treeRoot;
	}

}
