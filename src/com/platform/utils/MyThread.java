
/**
 * 文件名   :   MyThread.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月20日
 * 修改内容：      <修改内容>
 */
package com.platform.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.platform.entities.PostData;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class MyThread extends Thread {
	private final Logger log = Logger.getLogger("");
	private Object myClass;
	private String funName;
	private PostData postData;
	private String volumeName;
	private int timeout=Constant.timeout;
	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public MyThread(Object myClass, String funName, PostData postData) {
		// TODO Auto-generated constructor stub
		this.setMyClass(myClass);
		this.funName = funName;
		this.postData = postData;
		this.setVolumeName(null);
		this.postData.getExceptionsStack().clear();
	}
	public MyThread(Object myClass, String funName, PostData postData,int timeout) {
		// TODO Auto-generated constructor stub
		this.setMyClass(myClass);
		this.funName = funName;
		this.postData = postData;
		this.setVolumeName(null);
		this.timeout=timeout;
		this.postData.getExceptionsStack().clear();
	}

	public MyThread(Object myClass, String funName, String volumeName, PostData postData) {
		// TODO Auto-generated constructor stub
		this.setMyClass(myClass);
		this.funName = funName;
		this.postData = postData;
		this.setVolumeName(volumeName);
		this.postData.getExceptionsStack().clear();
	}

	/**
	 * 
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void run() {
		try {
			final ExecutorService exec = Executors.newFixedThreadPool(1);
			Callable<String> call = new Callable<String>() {
				public String call() throws Exception {
					// 放入耗时操作代码块

					// new Thread().sleep(Constant.timeout);
					if(volumeName==null)
					{
						execFunction(postData);
					}
					else {
						execFunction(postData,volumeName);
					}
					// System.out.println("is running");
					return "finished";
				}
			};
			try {
				Future<String> future = exec.submit(call);
				String obj = future.get(timeout, TimeUnit.MILLISECONDS); // 任务处理超时时间设为1秒

				exec.shutdown();

			} catch (TimeoutException ex) {
				String mess = "2003" + ex.toString();
				log.error(mess);
				postData.pushExceptionsStack(mess);
				// execFunction(postData);
			} catch (Exception e) {
				String mess = "2002" + e.toString();
				log.error(mess);
				postData.pushExceptionsStack(mess);
				// e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			String mess = "2001" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		}

	}

	public void execFunction(PostData resData) {
		try {
			Method method = myClass.getClass().getMethod(funName, new Class[] { PostData.class });
			method.invoke(myClass, new Object[] { resData });
		} catch (Exception e) {
			String mess = "2004" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		}
	}
	
	public void execFunction(PostData resData,String name) {
		try {
			Method method = myClass.getClass().getMethod(funName, new Class[] { PostData.class,String.class });
			method.invoke(myClass, new Object[] { resData,name });
		} catch (Exception e) {
			String mess = "2004" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		}
	}

	/**
	 * @return the myClass
	 */
	public Object getMyClass() {
		return myClass;
	}

	/**
	 * @param myClass
	 *            the myClass to set
	 */
	public void setMyClass(Object myClass) {
		this.myClass = myClass;
	}

	/**
	 * @return the funName
	 */
	public String getFunName() {
		return funName;
	}

	/**
	 * @param funName
	 *            the funName to set
	 */
	public void setFunName(String funName) {
		this.funName = funName;
	}

	/**
	 * @return the postData
	 */
	public PostData getPostData() {
		return postData;
	}

	/**
	 * @param postData
	 *            the postData to set
	 */
	public void setPostData(PostData postData) {
		this.postData = postData;
	}

	/**
	 * @return the volumeName
	 */
	public String getVolumeName() {
		return volumeName;
	}

	/**
	 * @param volumeName the volumeName to set
	 */
	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

}
