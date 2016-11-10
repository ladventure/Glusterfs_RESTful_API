
/**
 * 文件名   :   TaskOperateData.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年11月2日
 * 修改内容：      <修改内容>
 */
package com.platform.data;

import java.util.ArrayList;
import java.util.List;

import com.platform.controller.GfsData;
import com.platform.entities.PostData;
import com.platform.glusterfs.SetCluster;

import net.sf.json.JSONObject;

/**
 * 数据操作中记录 <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年11月2日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class TaskOperateData {
	/**
	 * 数据状态，0：准备迁移，1：表示正在迁移，2：表示迁移完成，正在MD5校验，3：表示校验成功，-1：表示迁移完成，校验失败,-2:表示迁移失败
	 * 4:表示正在删除，5：表示删除完成，-3：表示删除失败
	 */
	private int status = 1;

	/**
	 * 任务完成后返回状态
	 */
	// private int taskReturn = 0;

	/**
	 * 任务拷贝数据总大小，以字节为单位
	 */
	private Long allSize = 0L;
	/**
	 * 已经拷贝了的大小
	 */
	private Long completedSize = 0L;
	/**
	 * 进度
	 */
	private int progress = 0;

	/**
	 * 执行迁移的进程
	 */
	private Thread moveThread = null;

	/**
	 * 源路径和目的路径
	 */
	private String sourcePath = null;
	private String destPath = null;
	

	/**
	 * 删除任务数据
	 */
	private String removeDataName = null;

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public TaskOperateData() {
		// TODO Auto-generated constructor stub
	}

	public TaskOperateData(String removeDataName) {
		// TODO Auto-generated constructor stub
		this.removeDataName = removeDataName;
	}

	public TaskOperateData(String sourcePath, String destPath) {
		// TODO Auto-generated constructor stub
		this.sourcePath = sourcePath;
		this.destPath = destPath;

	}

	public TaskOperateData(String sourcePath, String destPath, Long allSize) {
		// TODO Auto-generated constructor stub
		this.sourcePath = sourcePath;
		this.destPath = destPath;
		this.allSize = allSize;
	}

	public void taskFinished(int taskReturn) {
		if (sourcePath != null) {
			setProgress(100);
			/**
			 * 如果拷贝成功，则开始校验
			 */
			int md5Return = 0;
			if (taskReturn == 1) {

				setStatus(2);
				md5Return = new CheckoutMD5().checkoutMD5Folder(getSourcePath(), getDestPath());
			}

			/**
			 * 如果拷贝失败或者校验失败，则删除已经拷贝的数据
			 */
			if (taskReturn != 1 || md5Return != 1) {
				GfsData.operateData.removeData(getDestPath(), new PostData());
			}
			int dataStatus = 0;
			/**
			 * copy failed
			 */
			if (taskReturn != 1) {
				dataStatus = -2;
			}
			/**
			 * copy successed
			 */
			else {
				/**
				 * checkout successed
				 */
				if (md5Return == 1) {
					dataStatus = 3;
				}
				/**
				 * checkout failed
				 */
				else {
					dataStatus = -1;
				}
			}
			setStatus(dataStatus);

			
		} 
		/**
		 * 删除完成后结尾
		 */
		else {
			if (taskReturn == 1) {
				setStatus(5);
			} else {
				setStatus(-3);
			}
		}
	}

	/**
	 * 程序退出保存正在进行的任务 <一句话功能简述> <功能详细描述>
	 * 
	 * @param operateDataTasks
	 * @param operateDataTaskFilePath
	 * @see [类、类#方法、类#成员]
	 */
	public void saveOperateDataTask(List<TaskOperateData> operateDataTasks, String operateDataTaskFilePath) {
		List<String> operateTasksString = new ArrayList<String>();
		for (TaskOperateData taskOperateData : operateDataTasks) {
			if (taskOperateData.getStatus() != 1 && taskOperateData.getStatus() != 4) {
				continue;
			}
			JSONObject jsonObject = JSONObject.fromObject(taskOperateData);
			operateTasksString.add(jsonObject.toString());
		}
		new SetCluster().saveMoutRecord(operateTasksString, operateDataTaskFilePath);
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the moveThread
	 */
	public Thread getMoveThread() {
		return moveThread;
	}

	/**
	 * @param moveThread
	 *            the moveThread to set
	 */
	public void setMoveThread(Thread moveThread) {
		this.moveThread = moveThread;
	}

	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @param sourcePath
	 *            the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * @return the destPath
	 */
	public String getDestPath() {
		return destPath;
	}

	/**
	 * @param destPath
	 *            the destPath to set
	 */
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	/**
	 * @return the completedSize
	 */
	public Long getCompletedSize() {
		return completedSize;
	}

	/**
	 * @param completedSize
	 *            the completedSize to set
	 */
	public void setCompletedSize(Long completedSize) {
		this.completedSize = completedSize;
		if (this.allSize == 0) {
			setProgress(0);
		} else {
			setProgress((int)(Math.ceil(this.completedSize * 100 / this.allSize)));
		}

	}

	/**
	 * @return the allSize
	 */
	public Long getAllSize() {
		return allSize;
	}

	/**
	 * @param allSize
	 *            the allSize to set
	 */
	public void setAllSize(Long allSize) {
		this.allSize = allSize;
	}

	/**
	 * @return the taskReturn
	 */
//	public int getTaskReturn() {
//		return taskReturn;
//	}

	/**
	 * @param taskReturn
	 *            the taskReturn to set
	 */
//	public void setTaskReturn(int taskReturn) {
//		this.taskReturn = taskReturn;
//	}

	/**
	 * @return the removeDataName
	 */
	public String getRemoveDataName() {
		return removeDataName;
	}

	/**
	 * @param removeDataName
	 *            the removeDataName to set
	 */
	public void setRemoveDataName(String removeDataName) {
		this.removeDataName = removeDataName;
	}

}
