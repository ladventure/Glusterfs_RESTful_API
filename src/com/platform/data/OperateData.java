
/**
 * 文件名   :   OperateData.java
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

import org.apache.log4j.Logger;

import com.platform.entities.PostData;
import com.platform.glusterfs.ClusterInfo;
import com.platform.glusterfs.RemoveData;
import com.platform.glusterfs.SetCluster;

import net.sf.json.JSONObject;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年11月2日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public abstract class OperateData {
	public Logger log =   Logger.getLogger ( OperateData.class); 
//	protected String sourceName;
//	protected String destName;
//	protected String removeName;
//	/**
//	 * <一句话功能简述>
//	 * <功能详细描述>
//	 * @see [类、类#方法、类#成员]
//	 */
	public OperateData() {
		// TODO Auto-generated constructor stub
	}
//	public OperateData(String removeName) {
//		// TODO Auto-generated constructor stub
//		this.removeName=removeName;
//	}
//	/**
//	 * 
//	 * <功能详细描述>
//	 * @see [类、类#方法、类#成员]
//	 */
//	public OperateData(String sourceName,String destName) {
//		// TODO Auto-generated constructor stub
//		this.destName=destName;
//		this.sourceName=sourceName;
//	}
	
	/**
	 * 判断文件是否存在
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param name
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public abstract boolean isexistsFile(String name);
	/**
	 * 启动数据拷贝函数
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public abstract int copyData(String sourceName,String destName,PostData resData);
	
	/**
	 * 启动数据删除函数
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public abstract int removeData(String removeName,PostData resData);
	
	
	
}
