package com.platform.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * 文件读写操作帮助类
 * 
 * @author wuming
 * 
 */
public class FileOperateHelper {

	/**
	 * 以追加的方式将信息写入文件
	 * 
	 * @param path
	 * @param message
	 */
	@SuppressWarnings("resource")
	public static void fileWrite(String path, String message) {
		if (null == path || "".equals(path)) {
			return;
		}
		try {
			path = path+".log";
			File file = new File(path);
			if (file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, true); // 如果追加方式用true
			StringBuffer sb = new StringBuffer();
			sb.append(message);
			out.write(sb.toString().getBytes("utf-8"));
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	/**
	 * 文件读取方法
	 * @param path
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String fileReader(String path) {		
		StringBuffer sb = new StringBuffer();
		String tempString = "";
		try {
			File file = new File(path);			
			if (!file.exists())
				return "当前没有日志信息！";
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			while ((tempString = br.readLine()) != null) {
				sb.append(tempString).append("\r\n");
			}
		} catch (Exception e) {
//			Configs.CONSOLE_LOGGER.info(e.getMessage());
		}
		return sb.toString();
	}
	
	/**
	 * 文件读取方法
	 * @param path
	 * @return
	 */

	@SuppressWarnings("resource")
	public static String fileReaderAndendline(String path) {

		StringBuffer sb = new StringBuffer();
		String tempString = "";
		try {
			File file = new File(path);
			if (!file.exists())

				return "";

			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			while ((tempString = br.readLine()) != null) {
				sb.append(tempString+"\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return sb.toString();
	}

	@Test
 public void fileReaderAndendlineTest(){
	 String fileContent=FileOperateHelper.fileReaderAndendline("res/getTreeData.sh");
	 System.out.println(fileContent);
 }
	
	@Test
	 public void fileReaderTest(){
		 String fileContent=FileOperateHelper.fileReader("res/getTreeData.sh");
		 System.out.println(fileContent);
	 }
}
