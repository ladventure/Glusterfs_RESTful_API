package com.platform.glusterfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.jfinal.ext.interceptor.POST;
import com.platform.entities.FolderNode;
import com.platform.entities.PostData;
import com.platform.utils.Constant;
import com.platform.utils.ExecuteCommand;
import com.platform.utils.FileOperateHelper;
import com.platform.utils.GanymedSSH;

import freemarker.core._RegexBuiltins.replace_reBI;

/**
 * <一句话功能简述> 获得GFS某个目录下的子目录 <功能详细描述>
 * 
 * @author chen
 * @version [版本号，2016年9月8日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class GetTreeData {
	ShowData showData = new ShowData();
	public static Logger log = Logger.getLogger("");

	/**
	 * <一句话功能简述> 获得所以子目录 <功能详细描述>
	 * 
	 * @param name
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public FolderNode getDatas(String name) {
		String names[] = name.split("/");
		String only_name = names[names.length - 1];
		FolderNode fileNode = new FolderNode(only_name);
		fileNode.setPath(name);
		Map<String, String> files = showData.showFolderData(name);
		if (files == null || files.size() == 0) {
			return fileNode;
		}
		fileNode.setIsFolder(files.size());
		List<FolderNode> list = new ArrayList<FolderNode>();
		fileNode.setChildNodes(list);
		for (Map.Entry<String, String> entry : files.entrySet()) {
			if (entry.getKey().equals("app")) {
				continue;
			}
			int number = Integer.parseInt(entry.getValue());
			if (number == 1) {
				fileNode.getChildNodes().add(new FolderNode(entry.getKey(), number));
			}
			if (number > 1) {
				FolderNode temp = getDatas(name + "/" + entry.getKey());
				fileNode.getChildNodes().add(temp);
			}
		}

		return fileNode;
	}

	/**
	 * <一句话功能简述> 获得所以子目录 <功能详细描述>
	 * 
	 * @param name
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public FolderNode getDatasWithShell(String name, PostData resData, ExecuteCommand executeCommand) {
		if (name.endsWith("/")) {
			name = name.substring(0, name.length() - 1);
		}
		FolderNode fileNode = new FolderNode(name);
		fileNode.setPath(name);
	
		String cmd ="find "+name+" -type d |grep -P  '^((?!app).)*$'|grep -P  '^((?!\\/\\.).)*$'";
		executeCommand.execCmdWaitAcquiescent(cmd, resData);
		List<String> files = executeCommand.execCmdWaitAcquiescent(cmd, resData);
		if (files == null) {
			String mess = "5001 get result is null";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return null;
		}
		if (files.size() == 0) {
			String mess = "5002 the folder of " + name + " is empty";
			log.info(mess);
			resData.pushExceptionsStack(mess);
			return fileNode;
		}
		fileNode=fileNode.CreateFolderTree(files);

		return fileNode;
	}

	@Test
	public void test_getTreeData() {

		GetTreeData getTreeData = new GetTreeData();
		// FolderNode fileOrFolder=getTreeData.getDatas("/home/gfs_ftp_point");
		FolderNode fileOrFolder = getTreeData.getDatasWithShell("/home/peng", new PostData(new Object()),
				new GanymedSSH(Constant.hostIp, Constant.rootUser, Constant.rootPasswd, Constant.port));
		for(FolderNode one:fileOrFolder.getChildNodes()){
			System.out.println(one.getName());
		}
		System.out.println(fileOrFolder);
	}

}
