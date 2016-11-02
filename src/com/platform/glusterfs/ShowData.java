package com.platform.glusterfs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.platform.utils.Constant;
import com.platform.utils.ProcessMyUtil;


public class ShowData {
	
	ProcessMyUtil proMy = new ProcessMyUtil();
	
	public static Logger log =   Logger.getLogger ( ShowData.class); 

	/**
	 * get the data of volumeName Map<string s1,string s2> s1 is data name and s2 is type file or folder
	 * <功能详细描述>
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String,String> showVolumeFiles(String volumeName){
//		log.info("start show the data");		
		Map<String,String> data_type=new HashMap<String, String>();
		/**
		 * get mount point of volumeName
		 */
		String folderName=volumeName;
		
		data_type=showFolderData(volumeName);
		return data_type;
}
	/**
	 * get the data of folder name
	 * Map<String,String> is folder name and type 1 is file and others is folder


	 * @param FolderName
	 * @return
	 */
	public Map<String,String> showFolderData(String folderName){
//		log.info(" start get "+folderName+" data");		


		Map<String,String> data_type=new HashMap<String, String>();
		String command="ls -l "+folderName;
		
		/*
		 * RunCommand runCommand=new RunCommand(); List<String>
		 * reStrings=runCommand.runCommandWait(command);
		 */
		List<String> reStrings = proMy.execCmdWaitAcquiescent(command);
		if (reStrings == null) {
			log.error("2101 command get result is null");
			return null;
		}
		if(reStrings.size()==0){
			log.info("2102 the folder is empty");
			return data_type;
		}
		if(reStrings.get(0).contains("No such file or directory")){
			log.info("2103 the "+folderName+" is not exists");
			return null;
		}
		/**
		 * remove first line total number
		 */
		reStrings.remove(0);
		
		for(Iterator it2 = reStrings.iterator();it2.hasNext();){
			String line=(String)it2.next();
			line=line.replaceAll(" +", " ");
			String keyValue[]=line.split(" ");
			if(keyValue.length<9){
				log.error("2104 "+line+" length is short");
				continue;
			}
			
			data_type.put(keyValue[8], keyValue[1]);
						
		}
//		log.info(" get "+folderName+" data successed");	
		return data_type;
	}
	
	/**
	 * 返回folder的大小字节表示
	 * -2表示获取大小出错，-1表示folder不存在，其他表示folder的大小
	 * @param folderPath
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public long getFolderSize(String folderPath) {
//		log.info("get " + folderPath + " Size ");

		String command = "du -k -d 0 "+folderPath+" | grep  " + folderPath + "|awk  \'{print $1}\'";
		List<String> reStrings = proMy.execCmdWaitAcquiescent(command);
		if(reStrings==null || reStrings.size()==0){
			log.error("get " + folderPath + " Size error!");
			return -2;
		}
		if (reStrings.get(0).contains(Constant.noSuchFile)) {
			log.error(folderPath+" is not exists");
			return -1;
		} 
		long size = Long.valueOf(reStrings.get(0));
		return size;
	}
	
	
	/**
	 * 
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @see [类、类#方法、类#成员]
	 */
	@Test
	public void testShowData(){
		
		System.out.println(showFolderData("/home"));

	}
}