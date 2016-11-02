
/**
 * 文件名   :   MyProcess.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月19日
 * 修改内容：      <修改内容>
 */
package com.platform.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.platform.entities.PostData;

/**
 * 执行本地命令类
 * 
 * @author liliy
 * @version [版本号，2016年10月19日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class MyProcess extends ExecuteCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.platform.utils.ExecuteCommand#execCmdNoWaitAcquiescent(java.lang.
	 * String)
	 */
	@Override
	public void execCmdNoWaitAcquiescent(String cmd, PostData postData) {
		// TODO Auto-generated method stub
		try {
			String[] execmd = new String[] { "/bin/sh", "-c", cmd };
			Process ps = Runtime.getRuntime().exec(execmd);
		} catch (Exception e) {
			String mess = "3001" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.utils.ExecuteCommand#execCmdWaitAcquiescent(java.lang.
	 * String)
	 */
	@Override
	public List<String> execCmdWaitAcquiescent(String cmd, PostData postData) {
		// TODO Auto-generated method stub
		List<String> reStrings = new ArrayList<String>();
		try {
			String[] execmd = new String[] { "/bin/sh", "-c", cmd };
			Process ps = Runtime.getRuntime().exec(execmd);

			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));

			String line;
			while ((line = br.readLine()) != null) {
				reStrings.add(line);
			}
			if (reStrings.size() == 0) {
				BufferedReader erro = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
				while ((line = erro.readLine()) != null) {
					reStrings.add(line);
				}
			}
		} catch (Exception e) {
			String mess = "3002" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		}
		return reStrings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.platform.utils.ExecuteCommand#execCmdNoWaitAcquiescent(java.lang.
	 * String)
	 */
	@Override
	public void execCmdNoWaitAcquiescent(String cmd) {
		// TODO Auto-generated method stub
		try {
			String[] execmd = new String[] { "/bin/sh", "-c", cmd };
			Process ps = Runtime.getRuntime().exec(execmd);
		} catch (Exception e) {
			String mess = "3001" + e.toString();
			log.error(mess);
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.utils.ExecuteCommand#execCmdWaitAcquiescent(java.lang.
	 * String)
	 */
	@Override
	public List<String> execCmdWaitAcquiescent(String cmd) {
		// TODO Auto-generated method stub
		List<String> reStrings = new ArrayList<String>();
		try {
			String[] execmd = new String[] { "/bin/sh", "-c", cmd };
			Process ps = Runtime.getRuntime().exec(execmd);

			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));

			String line;
			while ((line = br.readLine()) != null) {
				reStrings.add(line);
			}
			if (reStrings.size() == 0) {
				BufferedReader erro = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
				while ((line = erro.readLine()) != null) {
					reStrings.add(line);
				}
			}
		} catch (Exception e) {
			String mess = "3002" + e.toString();
			log.error(mess);
		
		}
		return reStrings;
	}

}
