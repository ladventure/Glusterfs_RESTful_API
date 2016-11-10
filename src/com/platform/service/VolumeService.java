package com.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.platform.data.CheckoutMD5;
import com.platform.entities.Brick;
import com.platform.entities.FolderNode;
import com.platform.entities.VolumeEntity;
import com.platform.glusterfs.ClusterInfo;
import com.platform.glusterfs.GetTreeData;
import com.platform.glusterfs.VolumeInfo;


public class VolumeService {
	
	public static Logger log = Logger.getLogger(VolumeService.class);
	
	/** Volume信息查询 */
	private VolumeInfo volumeInfo = new VolumeInfo();

	private ClusterInfo cluster = new ClusterInfo();
	
	/** gfs目录树形展示 */
	private GetTreeData gfsTree = new GetTreeData();
	
	public void getVolumeMsg() {
		
	}

}
