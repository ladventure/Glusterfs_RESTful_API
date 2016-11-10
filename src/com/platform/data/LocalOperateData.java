
/**
 * 文件名   :   LocalOperateData.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年11月2日
 * 修改内容：      <修改内容>
 */
package com.platform.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jfinal.log.Log;
import com.platform.entities.PostData;
import com.platform.utils.Constant;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年11月2日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@RunWith(Parameterized.class)
public class LocalOperateData extends OperateData {
	// /**
	// * <一句话功能简述> <功能详细描述>
	// *
	// * @see [类、类#方法、类#成员]
	// */
	// public LocalOperateData(String sourceName, String destName) {
	// // TODO Auto-generated constructor stub
	// super(sourceName, destName);
	//
	// }
	// public LocalOperateData(String removeName) {
	// // TODO Auto-generated constructor stub
	// super(removeName);
	//
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.data.OperateData#isexistsFile(java.lang.String)
	 */
	@Override
	public boolean isexistsFile(String name) {
		File file = new File(name);
		if (file.exists()) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * -1:源文件不存在，-2：创建目标文件夹失败，-3：拷贝失败，1,：拷贝成功 <一句话功能简述> <功能详细描述>
	 * 
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int realcopy(TaskOperateData taskOperateData) {
		String sourceName = LocalOperateData.this.sourceName;
		String destName = LocalOperateData.this.destName;
		File sourceFile = new File(sourceName);
		if (!sourceFile.exists()) {
			/**
			 * source file is not exists
			 */
			log.debug("源文件或者文件夹不存在");
			return -1;
		}
		File destFile = new File(destName);
		if (!destFile.exists()) {
			/**
			 * 创建目的文件夹失败
			 */
			if (destFile.mkdirs() == false) {
				log.debug("创建目的文件夹失败");
				return -2;
			}
		}
		if (!destName.endsWith(File.separator)) {
			destName = destName + File.separator;
		}

		/**
		 * 设置文件夹总大小
		 */
		taskOperateData.setAllSize(getFolderSize(sourceFile));

		/**
		 * 文件夹复制
		 */
		if (sourceFile.isDirectory()) {

			if (copyDirectory(sourceName, destName, true, taskOperateData) == true) {
				/**
				 * copy directory successed
				 */
				return 1;
			} else {
				/**
				 * copy directory failed
				 */
				log.debug("拷贝文件夹失败");
				return -3;
			}
		}
		/**
		 * 文件复制
		 */
		else {
			destName = destName + sourceFile.getName();
			boolean flag = nioBufferCopy(sourceName, destName, taskOperateData);
			if (flag) {
				return 1;
			}
			/**
			 * copy file failed
			 */
			log.debug("拷贝文件失败");
			return -4;

		}
	}

	/*
	 * *(non-Javadoc)
	 * 
	 * @see com.platform.data.OperateData#copyData(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int copyData(String sourceName, String destName, PostData resData) {
		log.info("start copy data from "+sourceName+" to "+destName);
		if(sourceName.endsWith(File.separator)){
			sourceName=sourceName.substring(0, sourceName.length()-1);
		}
		this.sourceName = sourceName;
		this.destName = destName;
		
		if (!isexistsFile(sourceName)) {
			String mess = "7002 " + sourceName + " is not exists";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		final TaskOperateData taskOperateData = new TaskOperateData(sourceName, destName);
		Constant.copyDataTask.add(taskOperateData);
		// TODO Auto-generated method stub
		
		String name=sourceName.split("/")[sourceName.split("/").length-1];
		taskOperateData.setName(name);
		Thread copyDataThread = new Thread(new Runnable() {
			public void run() {
				int copyTaskReturn = LocalOperateData.this.realcopy(taskOperateData);
				log.info("copy data finished ");
				taskOperateData.taskFinished(copyTaskReturn);				
			}
		});
		taskOperateData.setMoveThread(copyDataThread);
		copyDataThread.start();
		return 1;
	}

	public boolean copyDirectory(String srcDirName, String destDirName, boolean overlay,
			TaskOperateData taskOperateData) {
		// 判断源目录是否存在
		File srcDir = new File(srcDirName);
		if (!srcDir.exists()) {
			return false;
		} else if (!srcDir.isDirectory()) {

			return false;
		}

		// 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		File destDir = new File(destDirName);
		// 如果目标文件夹存在
		if (destDir.exists()) {
			// 如果允许覆盖则删除已存在的目标目录
			if (overlay) {
				// new File(destDirName).delete();
			} else {
				return false;
			}
		} else {
			// 创建目的目录
			System.out.println("目的目录不存在，准备创建。。。");
			if (!destDir.mkdirs()) {
				System.out.println("复制目录失败：创建目的目录失败！");
				return false;
			}
		}

		boolean flag = true;
		File[] files = srcDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 复制文件
			if (files[i].isFile()) {
				flag = nioBufferCopy(files[i].getAbsolutePath(), destDirName + files[i].getName(), taskOperateData);
				if (!flag)
					break;
			} else if (files[i].isDirectory()) {
				flag = copyDirectory(files[i].getAbsolutePath(), destDirName + files[i].getName(), true,
						taskOperateData);
				if (!flag)
					break;
			}
		}
		if (!flag) {

			return false;
		} else {
			return true;
		}
	}

	/**
	 * 快速拷贝文件，并更新进度 <一句话功能简述> <功能详细描述>
	 * 
	 * @param source
	 * @param target
	 * @see [类、类#方法、类#成员]
	 */
	public boolean nioBufferCopy(String source, String target, TaskOperateData taskOperateData) {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		File sourceFile = new File(source);
		File targetFile = new File(target);
		try {
			inStream = new FileInputStream(sourceFile);
			outStream = new FileOutputStream(targetFile);
			in = inStream.getChannel();
			out = outStream.getChannel();
			/**
			 * 以字节为单位
			 */
			ByteBuffer buffer = ByteBuffer.allocate(Constant.bufferSize);
			while (in.read(buffer) != -1) {
				buffer.flip();
				out.write(buffer);
				buffer.clear();
				taskOperateData.setCompletedSize(taskOperateData.getCompletedSize() + Constant.bufferSize);
			}
			inStream.close();
			in.close();
			outStream.close();
			out.close();
		} catch (IOException e) {
			log.error(e.toString());
			return false;
		}
		return true;
	}

	public Long getFolderSize(File folderName) {
		Long size = 0L;
		if (!folderName.exists()) {
			size = 0L;
			return size;
		}
		if (!folderName.isDirectory()) {
			size = folderName.length();
			return size;
		}
		File files[] = folderName.listFiles();
		for (File oneFile : files) {
			if (oneFile.isDirectory()) {
				size = size + getFolderSize(oneFile);
			}
			size = size + oneFile.length();
		}
		return size;
	}

	@Test
	public void copyDataTest() {
		final String sourcePath = "E:\\Movies\\[电影天堂www.dy2018.com]机械师2：复活HD韩版高清中字.mkv";
		final String destPath = "E:\\迅雷下载\\Qiyi\\Offline";
		// System.out.println(new LocalOperateData().copyData(sourcePath,
		// destPath));
		while (true) {

			if (Constant.copyDataTask.size() == 0) {
				continue;
			}
			TaskOperateData taskOperateData = Constant.copyDataTask.get(0);
			log.info(taskOperateData.getProgress() + "\t" + taskOperateData.getCompletedSize());
			if (!taskOperateData.getMoveThread().isAlive()) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.data.OperateData#removeData()
	 */
	@Override
	public int removeData(String removeName, PostData resData) {
		// TODO Auto-generated method stub
		log.info("start remove "+removeName);
		this.removeName = removeName;
		if (!isexistsFile(removeName)) {
			String mess = "7001 " + removeName + " is not exists";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		final TaskOperateData taskOperateData = new TaskOperateData(removeName);
		Constant.copyDataTask.add(taskOperateData);
		// TODO Auto-generated method stub

		Thread removeDataThread = new Thread(new Runnable() {
			public void run() {
				int removeStatus = LocalOperateData.this.realRemove(taskOperateData);
				log.info("remove data finished");
				taskOperateData.taskFinished(removeStatus);
			}
		});
		taskOperateData.setMoveThread(removeDataThread);
		removeDataThread.start();
		return 1;
	}

	/**
	 * 删除数据程序 -1：表示源文件或者文件夹不存在，-2：表示删除文件夹失败，1：表示删除成功 <一句话功能简述> <功能详细描述>
	 * 
	 * @param taskOperateData
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int realRemove(TaskOperateData taskOperateData) {
		String fileName = taskOperateData.getRemoveDataName();
		File file = new File(fileName);
		if (!file.exists()) {
			return -1;
		}
		if (file.isDirectory()) {
			if (deleteDirectory(fileName)) {
				return 1;
			} else {
				return -2;
			}
		} else {
			if (deleteFile(fileName)) {
				return 1;
			} else {
				return -2;
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	/**
	 * 删除目录及目录下的文件
	 * 
	 * @param dir
	 *            要删除的目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator))
			dir = dir + File.separator;
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			System.out.println("删除目录失败：" + dir + "不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹中的所有文件包括子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			System.out.println("删除目录失败！");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("删除目录" + dir + "成功！");
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		String name = "E:\\迅雷下载\\Qiyi";
		// new LocalOperateData().removeData(name);
	}
}
