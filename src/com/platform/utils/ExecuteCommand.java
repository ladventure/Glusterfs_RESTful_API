
/**
 * 文件名   :   ExecuteCommand.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月20日
 * 修改内容：      <修改内容>
 */
package com.platform.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.platform.entities.PostData;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年10月20日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public abstract class ExecuteCommand {
	final Logger log = Logger.getLogger("");
	public abstract void execCmdNoWaitAcquiescent(String cmd,PostData postData);
	public abstract void execCmdNoWaitAcquiescent(String cmd);
	public abstract List<String> execCmdWaitAcquiescent(String cmd,PostData postData);
	public abstract List<String> execCmdWaitAcquiescent(String cmd);
}
