
/**
 * 文件名   :   SshOperateData.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年11月2日
 * 修改内容：      <修改内容>
 */
package com.platform.data;

import java.io.File;
import java.util.List;

import javax.net.ssl.SSLException;

import org.junit.Test;

import com.jfinal.core.Const;
import com.platform.entities.PostData;
import com.platform.glusterfs.CopyData;
import com.platform.utils.Constant;
import com.platform.utils.GanymedSSH;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年11月2日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class SshOperateData extends OperateData {

//	/**
//	 * <一句话功能简述> <功能详细描述>
//	 * 
//	 * @see [类、类#方法、类#成员]
//	 */
//	public SshOperateData(String sourceName, String destName) {
//		// TODO Auto-generated constructor stub
//		super(sourceName, destName);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.data.OperateData#isexistsFile(java.lang.String)
	 */
	@Override
	public boolean isexistsFile(String name) {
		// TODO Auto-generated method stub
		String command = "ls " + name;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);
		try {
			if (reStrings.size() == 0) {
				return true;
			}
			if (reStrings.get(0).contains(Constant.noSuchFile)) {
				return false;
			}
		} catch (NullPointerException e) {
			log.error("5201 " + command + " execute error");
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.data.OperateData#copyData(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int copyData(String sourceName,String destName,PostData resData) {
		// TODO Auto-generated method stub
		log.info("start copy data from "+sourceName+" to "+destName);
		this.sourceName=sourceName;
		this.destName=destName;
		if(!isexistsFile(sourceName)){
			String mess="7001 "+sourceName+" is not exists";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		if(!destName.endsWith("/")){
			destName=destName+"/";
		}
		if(sourceName.endsWith("/")){
			sourceName=sourceName.substring(0, sourceName.length()-1);
		}
		this.sourceName=sourceName;
		this.destName=destName;
		final TaskOperateData taskOperateData = new TaskOperateData(sourceName, destName);
		Constant.copyDataTask.add(taskOperateData);
		// TODO Auto-generated method stub
		
		final String name=sourceName.split("/")[sourceName.split("/").length-1];
		taskOperateData.setName(name);
		/**
		 * 目标文件或者文件夹已经存在
		 */
		if(!isexistsFile(this.destName+name)){
			TaskOperateData removeExistsDestNametask = new TaskOperateData(this.destName+name);
			realRemove(removeExistsDestNametask);
		}
		new CopyData().createFolder(this.destName+name);
		final Thread setProgress=new Thread(new Runnable() {
			public void run() {
				while(taskOperateData.getMoveThread().isAlive()){
					long comepletedSize=getFolderSize(SshOperateData.this.destName+name);
					if(comepletedSize<0){
						log.error("获取文件夹大小失败");
						continue;
					}else{
						taskOperateData.setCompletedSize(comepletedSize);
					}
					
					try {
						Thread.sleep(Constant.timeSetProgress);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		Thread copyDataThread = new Thread(new Runnable() {
			public void run() {
				int copyStatus = SshOperateData.this.realcopy(taskOperateData);
				log.info("copy data finished ");
				setProgress.stop();
				taskOperateData.taskFinished(copyStatus);	
			}
		});
		taskOperateData.setMoveThread(copyDataThread);
		copyDataThread.start();	
		setProgress.start();
		return 1;

	} 
	
	/* (non-Javadoc)
	 * @see com.platform.data.OperateData#removeData()
	 */
	@Override
	public int removeData(String removeName,PostData resData) {
		// TODO Auto-generated method stub
		log.info("start remove "+removeName);
		this.removeName=removeName;
		if(!isexistsFile(removeName)){
			
			String mess="8001 "+removeName+" is not exists";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		final TaskOperateData taskOperateData = new TaskOperateData(removeName);
		Constant.copyDataTask.add(taskOperateData);
		// TODO Auto-generated method stub

		Thread removeDataThread = new Thread(new Runnable() {
			public void run() {
				int removeStatus = SshOperateData.this.realRemove(taskOperateData);
				log.info("remove data finished");
				taskOperateData.taskFinished(removeStatus);
			}
		});
		taskOperateData.setMoveThread(removeDataThread);
		removeDataThread.start();
		return 1;
	}
	
	
	/**
	 * 创建文件夹,1:创建成功，-1：创建失败 <一句话功能简述> <功能详细描述>
	 * 
	 * @param folder
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int createFolders(String folder,String name) {
//		log.info("create " + folder);
		String cmd = "mkdir ";
		try {
			String splitFolder[] = folder.substring(1).split("/");
			for (String one : splitFolder) {
				cmd += "/" + one.replaceAll(" ", "");
				List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd);
				if (reStrings.size() == 0 || reStrings.get(0).contains("File exists")) {
					continue;
				} else {
					log.error(cmd + " 返回错误\n" + reStrings.get(0));
					return -1;
				}
			}
			cmd="rm -r "+folder+name;
			List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd);
		} catch (NullPointerException e) {
			log.error(cmd + " 命令执行失败");
			return -1;
		}

		return 1;
	}
	
	/**
	 * -1:源文件不存在，-2：创建目标文件夹失败，-3：拷贝失败，1,：拷贝成功
	 * 主要拷贝过程函数
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int realcopy(TaskOperateData taskOperateData) {

		String sourceName = SshOperateData.this.sourceName;
		String destName = SshOperateData.this.destName;
		long allsize = getFolderSize(sourceName);

		if (allsize == -1) {
			/**
			 * source file is not exists
			 */
			log.debug("源文件或者文件夹不存在");
			return -1;
		}
		/**
		 * 创建目标文件夹失败
		 */
		String name=sourceName.split("/")[sourceName.split("/").length-1];
		if(createFolders(destName,name)!=1){
			log.debug("目标文件夹创建失败");
			return -2;
		}

		if (!destName.endsWith("/")) {
			destName = destName + "/";
		}

		/**
		 * 设置文件夹总大小
		 */
		taskOperateData.setAllSize(allsize);

		/**
		 * 文件夹复制
		 */
		log.info("copy " + sourceName + " to " + destName);
		String command = "cp -rp " + sourceName + " " + destName;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);
		try {
			if (reStrings.size() == 0) {
				log.debug("拷贝完成");
				return 1;
			} else {
				log.debug("拷贝失败\n" + reStrings.get(0));
				return -3;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
			log.debug("拷贝失败，命令执行错误");
			return -3;
		}
	}
	
	/**
	 * -1:源文件不存在，-2：删除失败，1：删除成功
	 * 主要拷贝过程函数
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int realRemove(TaskOperateData taskOperateData) {

		String removeName = SshOperateData.this.removeName;

		/**
		 * 文件删除
		 */
		log.info("remove " + removeName);
		String command = "rm -rf " + removeName;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);
		try {
			if (reStrings.size() == 0) {
				log.info("删除完成");
				return 1;
			} else {
				log.error("删除失败\n" + reStrings.get(0));
				return -3;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
			log.debug("删除失败，命令执行错误");
			return -3;
		}
	}
	/**
	 * 返回folder的大小字节表示 -2表示获取大小出错，-1表示folder不存在，其他表示folder的大小
	 * 
	 * @param folderPath
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public long getFolderSize(String folderPath) {
		log.info("get " + folderPath + " Size ");
		long size = 0L;
		String command = "du -k -d 0 " + folderPath + " | grep  " + folderPath + "|awk  \'{print $1}\'";
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);
		try {
			size = Long.valueOf(reStrings.get(0));
		} catch (NullPointerException e) {
			// TODO: handle exception
			log.error("get " + folderPath + " Size error!");
			size = -2;
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			log.error(folderPath + " is not exists");
			size = -1;
		}

		return size;
	}

	@Test
	public void SshCopyDataTest(){
		final String sourcePath = "/home/FTP_GFS_point/320811_138";
		final String destPath = "/home/lili_test1_point";
//		System.out.println(new SshOperateData().copyData(sourcePath,destPath));
		while (true) {
			
			if(Constant.copyDataTask.size()==0){
				continue;
			}
			TaskOperateData taskOperateData=Constant.copyDataTask.get(0);
			log.info(taskOperateData.getAllSize() + "\t" + taskOperateData.getCompletedSize());
			log.info(taskOperateData.getProgress());
			if(!taskOperateData.getMoveThread().isAlive()){
				break;
			}
			try {
				new Thread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		Constant.execCmdObject = new GanymedSSH(Constant.hostIp, Constant.rootUser, Constant.rootPasswd, Constant.port);
		final String sourcePath = "/home/FTP_GFS_point/320811_138";
		final String destPath = "/home/lili_test1_point";
		new SshOperateData().SshCopyDataTest();
	}

	
}
