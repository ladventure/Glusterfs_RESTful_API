package com.platform.glusterfs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.platform.utils.Constant;



public class RemoveData {
	
	public static Logger log =   Logger.getLogger ( RemoveData.class); 
	
	
	/**
	 * -1 :error; 0: the filename is not exists ; 1:  right
	 * @param folderName
	 * @param fileName
	 * @return
	 */
	public int deleteFolder(String folderName){
		log.info("start delete "+folderName);	
		
		ShowData showData=new ShowData();
		Map<String,String> reStrings=showData.showFolderData(folderName);
		
		if(reStrings==null){
			log.error("3301 "+folderName+" is not exists");
			return -1;
		}
		
		String command="rm -r "+folderName;
				
//		int status=runCommand.runCommand(command);
		Constant.execCmdObject.execCmdNoWaitAcquiescent(command);
		
		log.info("delete "+folderName+" running");	
		return 1;
	}
	
   
	@Test
	public void testDeleteFolderFiles() {
		PropertyConfigurator.configure("log4j.properties");
		deleteFolder("/home/ubuntu");
	}
	
}
