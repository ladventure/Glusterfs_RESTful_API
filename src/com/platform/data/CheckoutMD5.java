package com.platform.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.platform.utils.Constant;
import com.platform.utils.GanymedSSH;

public class CheckoutMD5 {
	public static Logger log = Logger.getLogger(CheckoutMD5.class);
	String sourcePath;
	String destPath;
	String dataName;
	// String cmd_crateSourceMD5File="find "+sourcePath+dataName+"/app/ -type f
	// -print0 | xargs -0 md5sum | sort >"+deskPath+dataName+"_md5.txt";
	String cmd_getSourceMD5File;
	// String cmd_crateDestMD5File="find "+destPath+dataName+"/app/ -type f
	// -print0 | xargs -0 md5sum | sort >"+deskPath+dataName+"_md5.txt";
	String cmd_getDestMD5File;
	Map<String, String> source_md5 = new HashMap<String, String>();
	Map<String, String> dest_md5 = new HashMap<String, String>();

	public CheckoutMD5() {
		// TODO Auto-generated constructor stub
	}

	public CheckoutMD5(String sourcePath, String destPath, String dataName) {
		// TODO Auto-generated constructor stub
		this.sourcePath = sourcePath;
		this.destPath = destPath;
		this.dataName = dataName;
		cmd_getSourceMD5File = "find " + sourcePath + dataName + "/app/ -type f -print0 | xargs -0 md5sum | sort ";
		cmd_getDestMD5File = "find " + destPath + dataName + "/app/ -type f -print0 | xargs -0 md5sum | sort ";
	}

	/**
	 * 文件夹校验 校验sourcePath和destPath是否完全相同，如果相同，返回1；
	 * 如果不相同，返回0，如果获取文件MD5出错，返回-1；如何源文件不存在返回-2；目标文件不存在，返回-3；
	 * 
	 * @param sourcePath
	 * @param destPath
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int checkoutMD5Folder(String sourcePath, String destPath) {
		log.info("start checkout md5 "+sourcePath+" and "+ destPath);
		List<String> wrong_files = new ArrayList<String>();
		String source_cmd = "find " + sourcePath + " -type f -print0 | xargs -0 md5sum";
		String dest_cmd = "find " + destPath + " -type f -print0 | xargs -0 md5sum";
		List<String> sourceReStrings = Constant.execCmdObject.execCmdWaitAcquiescent(source_cmd);
		if (sourceReStrings == null || sourceReStrings.size() == 0) {
			log.error("get " + sourcePath + " MD5 error!");
			return -1;
		}
		if(sourceReStrings.get(0).contains(Constant.noSuchFile)){
			log.error(sourcePath+" is not exist!");
			return -2;
		}
		List<String> destReStrings = Constant.execCmdObject.execCmdWaitAcquiescent(dest_cmd);
		if (destReStrings == null || destReStrings.size() == 0) {
			log.error("get " + destReStrings + " MD5 error!");
			return -1;
		}
		if(destReStrings.get(0).contains(Constant.noSuchFile)){
			log.error(destPath+" is not exist!");
			return -3;
		}
		Map<String, String> source_md5 = new HashMap<String, String>();
		Map<String, String> dest_md5 = new HashMap<String, String>();
		for (String line : sourceReStrings) {
			String[] lines = line.split("  ");
			String key = lines[1].replace(sourcePath, "").trim();
			String value = lines[0].trim();
			source_md5.put(key, value);
		}
		for (String line : destReStrings) {
			String[] lines = line.split("  ");
 			String key = lines[1].replace(destPath, "").trim();
			String value = lines[0].trim();
			dest_md5.put(key, value);
		}
		for (Map.Entry<String, String> mapEntry : source_md5.entrySet()) {
			if (!(dest_md5.containsKey(mapEntry.getKey())
					&& dest_md5.get(mapEntry.getKey()).equals(mapEntry.getValue()))) {
				
				log.info(sourcePath+mapEntry.getKey() + " and " + destPath+mapEntry.getKey() + " is not same!");
				return 0;
				// System.out.println(mapEntry.getKey());
			}

		}

		log.info(sourcePath + " and " + destPath + " is  same!");
		return 1;
	}

	public static void main(String[] args) {
		CheckoutMD5 checkoutMD5 = new CheckoutMD5();
		Constant.execCmdObject = new GanymedSSH(Constant.hostIp, Constant.rootUser, Constant.rootPasswd, Constant.port);
		System.out.println(checkoutMD5.checkoutMD5Folder("/home/v1_copy","/home/ubuntu"));
	}
}
