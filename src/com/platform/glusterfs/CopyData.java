
package com.platform.glusterfs;

import java.util.List;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.platform.entities.PostData;
import com.platform.utils.Constant;

/**
 * <一句话功能简述> 复制数据 <功能详细描述>
 * 
 * @author chen
 * @version [版本号，2016年9月8日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class CopyData {
	public static Logger log = Logger.getLogger(CopyData.class);

	public int copyVolumeFiles(String sourceVolumeName, String destVolumeName, String fileName) {
		log.info("start copy " + fileName + " from " + sourceVolumeName + " to " + destVolumeName);
		int status = -1;
		/**
		 * get mount point of volumeName
		 */

		String sourceFolderName = sourceVolumeName;
		String destFolderName = destVolumeName;
		status = copyFolderFilesAnyway(sourceFolderName, destFolderName, fileName);
		return status;
	}

	/**
	 * 将sourceFolderName拷贝到destFolderName 如果拷贝正常返回1，如果sourceFolderName不存在返回-2
	 * ，如果destFolderName不存在返回-3
	 * 
	 * @param sourceFolderName
	 * @param destFolderName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int copyFolder(String sourceFolderName, String destFolderName) {
		createFolders(destFolderName);
		int progress = 0;
		log.info("start copy " + sourceFolderName + " to " + destFolderName);
		ShowData showData = new ShowData();
		Map<String, String> reStrings = showData.showFolderData(destFolderName);
		if (reStrings == null) {
			log.info("3201 " + destFolderName + " is not exists");
			return -3;
		}

		reStrings = showData.showFolderData(sourceFolderName);
		if (reStrings == null) {
			log.info("3202 " + sourceFolderName + " is not exists");
			return -2;
		}
		String command = "cp -rp " + sourceFolderName + " " + destFolderName;

		Constant.execCmdObject.execCmdNoWaitAcquiescent(command);

		log.info("copy " + sourceFolderName + " to " + destFolderName + "  running");
		return 1;
	}

	/**
	 * -1 :error; -2: the filename is not exists ;-3 :destFolderName ; 1: right
	 * not exists
	 * 
	 * @param folderName
	 * @param fileName
	 * @return
	 */
	public int copyFolderFiles(String sourceFolderName, String destFolderName, String fileName) {
		int progress = 0;
		log.info("start copy " + fileName + " from " + sourceFolderName + " to " + destFolderName);
		ShowData showData = new ShowData();
		Map<String, String> reStrings = showData.showFolderData(destFolderName);
		if (reStrings == null) {
			log.info("3201 " + destFolderName + " is not exists");
			return -3;
		}

		reStrings = showData.showFolderData(sourceFolderName + "/" + fileName);
		if (reStrings == null) {
			log.info("3202 " + sourceFolderName + "/" + fileName + " is not exists");
			return -2;
		}
		String command = "cp -rp " + sourceFolderName + "/" + fileName + " " + destFolderName;
		/*
		 * RunCommand runCommand = new RunCommand();
		 * 
		 * List<String> reStrings = runCommand.runCommandWait(command);
		 */
		Constant.execCmdObject.execCmdNoWaitAcquiescent(command);

		log.info("copy " + sourceFolderName + "/" + fileName + " to " + destFolderName + "  running");
		return 1;
	}

	/**
	 * 不管目的路径存在与否，强制拷贝
	 * 
	 * @param sourceFolderName
	 * @param destFolderName
	 * @param fileName
	 * @return
	 */
	public int copyFolderFilesAnyway(String sourceFolderName, String destFolderName, String fileName) {
		createFolders(destFolderName);
		int result = copyFolderFiles(sourceFolderName, destFolderName, fileName);
		return result;
	}

	public int createFolders(String folder) {
		log.info("create " + folder);
		String splitFolder[] = folder.substring(1).split("/");
		
		String names="";
		String name="";
		for (String one : splitFolder) {
			name=name+ "/" + one.trim();
			names =names+" "+name;		
		}
		String cmd = "mkdir "+names;
		Constant.execCmdObject.execCmdWaitAcquiescent(cmd);
		return 1;
	}

	public int createFolder(String folder) {
		log.info("create " + folder);
		String cmd = "mkdir " + folder;
		Constant.execCmdObject.execCmdWaitAcquiescent(cmd);
		return 1;
	}

	@Test
	public void testcreateFolders() {

		createFolders("/aaa/vvv/ddd/www/rrrr");
	}

	// @Test
	public void testCopyFolderFiles() {

		copyFolderFiles("/home", "/home/ubuntu", "system_data");
	}
}
